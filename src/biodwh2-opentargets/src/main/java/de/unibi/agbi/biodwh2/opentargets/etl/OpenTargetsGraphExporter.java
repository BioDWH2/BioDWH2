package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public class OpenTargetsGraphExporter extends GraphExporter<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsGraphExporter.class);
    static final String MOLECULE_LABEL = "Molecule";
    static final String DISEASE_LABEL = "Disease";
    static final String TARGET_LABEL = "Target";
    static final String HAS_CHILD_LABEL = "HAS_CHILD";

    public OpenTargetsGraphExporter(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MOLECULE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportMolecules(workspace, graph);
        exportDiseases(workspace, graph);
        exportTargets(workspace, graph);
        return false;
    }

    private void exportMolecules(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting molecules...");
        final Map<String, List<Long>> parentChemblIdChildNodeIdsMap = new HashMap<>();
        processParquetZip(workspace, OpenTargetsUpdater.MOLECULE_FILE_NAME,
                          (entry) -> exportMolecule(graph, parentChemblIdChildNodeIdsMap, entry));
        graph.beginEdgeIndicesDelay(HAS_CHILD_LABEL);
        for (final var entry : parentChemblIdChildNodeIdsMap.entrySet()) {
            final Node parentNode = graph.findNode(MOLECULE_LABEL, ID_KEY, entry.getKey());
            if (parentNode != null)
                for (final Long childNodeId : entry.getValue())
                    graph.addEdge(parentNode, childNodeId, HAS_CHILD_LABEL);
        }
        graph.endEdgeIndicesDelay(HAS_CHILD_LABEL);
    }

    @SuppressWarnings("unchecked")
    private void exportMolecule(final Graph graph, final Map<String, List<Long>> parentChemblIdChildNodeIdsMap,
                                final GenericRecord entry) {
        // id : string
        // canonicalSmiles : string
        // inchiKey : string
        // drugType : string
        // blackBoxWarning : bool
        // name : string
        // yearOfFirstApproval : long
        // maximumClinicalTrialPhase : double
        // parentId : string
        // hasBeenWithdrawn : bool
        // isApproved : boolean
        // tradeNames: string[]
        // synonyms: string[]
        // crossReferences : { [source : string] : string[] }
        // childChemblIds: string[]
        // TODO: linkedDiseases: { rows: string[]; count : int }[]
        // TODO: linkedTargets: { rows: string[]; count : int }[]
        // description : string
        final NodeBuilder builder = graph.buildNode().withLabel(MOLECULE_LABEL);
        builder.withProperty(ID_KEY, (String) entry.get("id"));
        builder.withPropertyIfNotNull("canonical_smiles", (String) entry.get("canonicalSmiles"));
        builder.withPropertyIfNotNull("inchi_key", (String) entry.get("inchiKey"));
        builder.withPropertyIfNotNull("drug_type", (String) entry.get("drugType"));
        if (entry.get("blackBoxWarning") != null)
            builder.withProperty("black_box_warning", (Boolean) entry.get("blackBoxWarning"));
        builder.withPropertyIfNotNull("name", (String) entry.get("name"));
        if (entry.get("yearOfFirstApproval") != null)
            builder.withProperty("year_of_first_approval", ((Long) entry.get("yearOfFirstApproval")).intValue());
        if (entry.get("maximumClinicalTrialPhase") != null)
            builder.withProperty("maximum_clinical_trial_phase", ((Double) entry.get(
                    "maximumClinicalTrialPhase")).intValue());
        if (entry.get("hasBeenWithdrawn") != null)
            builder.withProperty("withdrawn", (Boolean) entry.get("hasBeenWithdrawn"));
        if (entry.get("isApproved") != null)
            builder.withProperty("approved", (Boolean) entry.get("isApproved"));
        builder.withPropertyIfNotNull("trade_names", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("tradeNames"), String[]::new));
        builder.withPropertyIfNotNull("synonyms", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("synonyms"), String[]::new));
        builder.withPropertyIfNotNull("description", (String) entry.get("description"));
        if (entry.get("crossReferences") != null) {
            final List<String> xrefs = new ArrayList<>();
            final var map = (Map<String, GenericData.Array<GenericData.Record>>) entry.get("crossReferences");
            for (final var xrefEntry : map.entrySet()) {
                final var xrefArray = genericDataArrayToArray(xrefEntry.getValue(), String[]::new);
                if (xrefArray != null)
                    xrefs.addAll(Arrays.stream(xrefArray).map(x -> xrefEntry.getKey() + ':' + x)
                                       .collect(Collectors.toList()));
            }
            if (!xrefs.isEmpty())
                builder.withProperty("xrefs", xrefs.toArray(new String[0]));
        }
        final Node node = builder.build();
        if (entry.get("parentId") != null) {
            parentChemblIdChildNodeIdsMap.computeIfAbsent((String) entry.get("parentId"), (k) -> new ArrayList<>()).add(
                    node.getId());
        }
    }

    private void processParquetZip(Workspace workspace, final String fileName, final Consumer<GenericRecord> consumer) {
        try (final var stream = FileUtils.openZip(workspace, dataSource, fileName)) {
            while (stream.getNextEntry() != null) {
                final var inputFile = getInputFileFromZipStream(stream);
                final var conf = new Configuration();
                try (var reader = AvroParquetReader.<GenericRecord>builder(inputFile).withDataModel(
                        new ReflectData(getClass().getClassLoader())).disableCompatibility().withConf(conf).build()) {
                    GenericRecord entry;
                    while ((entry = reader.read()) != null) {
                        consumer.accept(entry);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private static InputFile getInputFileFromZipStream(final ZipInputStream stream) throws IOException {
        final byte[] data = stream.readAllBytes();
        final var dataStream = new ByteArrayInputStream(data) {
            public int getPos() {
                return super.pos;
            }

            public void seek(int newPos) {
                pos = newPos;
            }
        };
        return new InputFile() {
            @Override
            public long getLength() {
                return data.length;
            }

            @Override
            public SeekableInputStream newStream() {
                return new DelegatingSeekableInputStream(dataStream) {
                    @Override
                    public long getPos() {
                        return dataStream.getPos();
                    }

                    @Override
                    public void seek(long newPos) {
                        dataStream.seek((int) newPos);
                    }
                };
            }
        };
    }

    private <T> T[] genericDataArrayToArray(final GenericData.Array<GenericData.Record> array,
                                            final IntFunction<T[]> generator) {
        if (array != null && !array.isEmpty())
            return array.stream().map(x -> x.get("element")).toArray(generator);
        return null;
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting diseases...");
        processParquetZip(workspace, OpenTargetsUpdater.DISEASES_FILE_NAME, (entry) -> exportDisease(graph, entry));
    }

    @SuppressWarnings("unchecked")
    private void exportDisease(final Graph graph, final GenericRecord entry) {
        // id : string
        // code : string
        // dbXRefs: string[]
        // description : string
        // name : string
        // TODO: directLocationIds: string[]
        // TODO: obsoleteTerms: string[]
        // TODO: parents: string[]
        // synonyms: { hasBroadSynonym: string[]; hasExactSynonym: string[]; hasNarrowSynonym: string[]; hasRelatedSynonym: string[] }[]
        // TODO: ancestors: string[]
        // TODO: descendants: string[]
        // TODO: children: string[]
        // TODO: therapeuticAreas: string[]
        // TODO: indirectLocationIds: string[]
        // TODO: ontology: { isTherapeuticArea : bool; leaf : bool; sources: { url : string; name : string }[] }[]
        final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
        builder.withProperty(ID_KEY, (String) entry.get("id"));
        builder.withPropertyIfNotNull("code", (String) entry.get("code"));
        builder.withPropertyIfNotNull("name", (String) entry.get("name"));
        builder.withPropertyIfNotNull("description", (String) entry.get("description"));
        builder.withPropertyIfNotNull("xrefs", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("dbXRefs"), String[]::new));
        final var synonyms = (GenericData.Record) entry.get("synonyms");
        if (synonyms != null) {
            builder.withPropertyIfNotNull("broad_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasBroadSynonym"), String[]::new));
            builder.withPropertyIfNotNull("exact_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasExactSynonym"), String[]::new));
            builder.withPropertyIfNotNull("narrow_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasNarrowSynonym"), String[]::new));
            builder.withPropertyIfNotNull("related_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasRelatedSynonym"), String[]::new));
        }
        builder.build();
    }

    private void exportTargets(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting targets...");
        processParquetZip(workspace, OpenTargetsUpdater.TARGETS_FILE_NAME, (entry) -> exportTarget(graph, entry));
    }

    @SuppressWarnings("unchecked")
    private void exportTarget(final Graph graph, final GenericRecord entry) {
        // id : string
        // approvedSymbol : string
        // biotype : string
        // transcriptIds: string[]
        // TODO: canonicalTranscript: { id : string; chromosome : string; start : long; end : long; strand : string }[]
        // TODO: canonicalExons: string[]
        // TODO: genomicLocation: { chromosome : string; start : long; end : long; strand : int }[]
        // TODO: alternativeGenes: string[]
        // approvedName : string
        // TODO: go: { id : string; source : string; evidence : string; aspect : string; geneProduct : string; ecoId : string }[]
        // TODO: hallmarks: { attributes: { pmid : long; description : string; attribute_name : string }[]; cancerHallmarks: { pmid : long; description : string; impact : string; label : string }[] }[]
        // TODO: synonyms: { label : string; source : string }[]
        // TODO: symbolSynonyms: { label : string; source : string }[]
        // TODO: nameSynonyms: { label : string; source : string }[]
        // TODO: functionDescriptions: string[]
        // TODO: subcellularLocations: { location : string; source : string; termSL : string; labelSL : string }[]
        // TODO: targetClass: { id : long; label : string; level : string
        // TODO: obsoleteSymbols: { label : string; source : string }[]
        // TODO: obsoleteNames: { label : string; source : string }[]
        // TODO: constraint: { constraintType : string; score : float; exp : float; obs : int; oe : float; oeLower : float; oeUpper : float; upperRank : int; upperBin : int; upperBin6 : int }[]
        // TODO: tep: { targetFromSourceId : string; description : string; therapeuticArea : string; url : string }[]
        // TODO: proteinIds: { id : string; source : string }[]
        // dbXrefs: { id : string; source : string }[]
        // TODO: chemicalProbes: { control : string; drugId : string; id : string; isHighQuality : bool; mechanismOfAction: string[]; origin: string[]; probeMinerScore : long; probesDrugsScore : long; scoreInCells : long; scoreInOrganisms : long; targetFromSourceId : string; urls: { niceName : string; url : string }[] }[]
        // TODO: homologues: { speciesId : string; speciesName : string; homologyType : string; targetGeneId : string; isHighConfidence : string; targetGeneSymbol : string; queryPercentageIdentity : double; targetPercentageIdentity : double; priority : int }[]
        // TODO: tractability: { modality : string; id : string; value : bool }[]
        // TODO: safetyLiabilities: { event : string; eventId : string; effects: { direction : string; dosing : string }[]; biosamples: { cellFormat : string; cellLabel : string; tissueId : string; tissueLabel : string }[]; isHumanApplicable : bool; datasource : string; literature : string; url : string; studies: { description : string; name : string; type : string }[] }[]
        // TODO: pathways: { pathwayId : string; pathway : string; topLevelTerm : string }[]
        final NodeBuilder builder = graph.buildNode().withLabel(TARGET_LABEL);
        builder.withProperty(ID_KEY, (String) entry.get("id"));
        builder.withPropertyIfNotNull("approved_symbol", (String) entry.get("approvedSymbol"));
        builder.withPropertyIfNotNull("biotype", (String) entry.get("biotype"));
        builder.withPropertyIfNotNull("approved_name", (String) entry.get("approvedName"));
        builder.withPropertyIfNotNull("transcript_ids", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("transcriptIds"), String[]::new));
        final var xrefsArray = (GenericData.Array<GenericData.Record>) entry.get("dbXrefs");
        if (xrefsArray != null) {
            final List<String> xrefs = new ArrayList<>();
            for (final var record : xrefsArray) {
                final var element = (GenericData.Record) record.get("element");
                xrefs.add((String) element.get("source") + ':' + element.get("id"));
            }
            if (!xrefs.isEmpty())
                builder.withProperty("xrefs", xrefs.toArray(new String[0]));
        }
        builder.build();
    }

    // GO_FILE_NAME
    // id : string
    // name : string

    // HPO_FILE_NAME
    // dbXRefs : string[]
    // description : string
    // id : string
    // name : string
    // namespace : string[]
    // obsolete_terms : string[]
    // parents : string[]

    // INDICATION_FILE_NAME
    // id : string
    // indications: { disease : string; efoName : string; references: { source : string; ids : string[] }[]; maxPhaseForIndication : double }[]
    // approvedIndications : string[]
    // indicationCount : int

    // INTERACTION_FILE_NAME
    // sourceDatabase : string
    // targetA : string
    // intA : string
    // intABiologicalRole : string
    // targetB : string
    // intB : string
    // intBBiologicalRole : string
    // speciesA: { mnemonic : string; scientific_name : string; taxon_id : long }[]
    // speciesB: { mnemonic : string; scientific_name : string; taxon_id : long }[]
    // count : long
    // scoring : double

    // INTERACTION_EVIDENCE_FILE_NAME
    // hostOrganismTissue: { fullName : string; shortName : string; xrefs: string[] }[]
    // targetB : string
    // evidenceScore : double
    // intBBiologicalRole : string
    // interactionResources: { databaseVersion : string; sourceDatabase : string }[]
    // interactionTypeMiIdentifier : string
    // interactionDetectionMethodShortName : string
    // intA : string
    // intBSource : string
    // speciesB: { mnemonic : string; scientificName : string; taxonId : long }[]
    // interactionIdentifier : string
    // hostOrganismTaxId : long
    // participantDetectionMethodA: { miIdentifier : string; shortName : string }[]
    // expansionMethodShortName : string
    // speciesA: { mnemonic : string; scientificName : string; taxonId : long }[]
    // intASource : string
    // intB : string
    // pubmedId : string
    // intABiologicalRole : string
    // hostOrganismScientificName : string
    // interactionScore : double
    // interactionTypeShortName : string
    // expansionMethodMiIdentifier : string
    // targetA : string
    // participantDetectionMethodB: { miIdentifier : string; shortName : string }[]
    // interactionDetectionMethodMiIdentifier : string

    // MECHANISM_OF_ACTION_FILE_NAME
    // actionType : string
    // mechanismOfAction : string
    // chemblIds: string[]
    // targetName : string
    // targetType : string
    // targets: string[]
    // references: { source : string; ids: string[]; urls: string[] }[]

    // MOUSE_PHENOTYPES_FILE_NAME
    // biologicalModels: { allelicComposition : string; geneticBackground : string; id : string; literature: string[] }[]
    // modelPhenotypeClasses: { id : string; label : string }[]
    // modelPhenotypeId : string
    // modelPhenotypeLabel : string
    // targetFromSourceId : string
    // targetInModel : string
    // targetInModelEnsemblId : string
    // targetInModelMgiId : string

    // REACTOME_FILE_NAME
    // id : string
    // label : string
    // ancestors: string[]
    // descendants: string[]
    // children: string[]
    // parents: string[]
    // path: { ? }[]

    // BASELINE_EXPRESSION_FILE_NAME
    // id : string
    // tissues: {
    //   efo_code : string;
    //   label : string;
    //   organs: string[];
    //   anatomical_systems: string[];
    //   rna: { value : double; zscore : int; level : int; unit : string }[];
    //   protein: { reliability : bool; level : int; cell_type: { name : string; reliability : bool; level : int }[] }[];
    // }[]

    // DISEASE_TO_PHENOTYPE_FILE_NAME
    // disease : string
    // phenotype : string
    // evidence: {
    //   aspect : string;
    //   bioCuration : string;
    //   diseaseFromSourceId : string;
    //   diseaseFromSource : string;
    //   diseaseName : string;
    //   evidenceType : string;
    //   frequency : string;
    //   modifiers: string[];
    //   onset: string[];
    //   qualifier : string;
    //   qualifierNot : boolean;
    //   references: string[];
    //   sex : string;
    //   resource : string
    // }[]

    // DRUG_WARNINGS_FILE_NAME
    // toxicityClass : string
    // chemblIds: string[]
    // country : string
    // description : string
    // id : long
    // references: { ref_id : string; ref_type : string; ref_url : string }[]
    // warningType : string
    // year : long
    // efo_term : string
    // efo_id : string
    // efo_id_for_warning_class : string
    // meddraSocCode : int

    // FDA_ADR_FILE_NAME
    // chembl_id : string
    // event : string
    // count : long
    // llr : double
    // critval : double
    // meddraCode : string

    // FDA_ATR_FILE_NAME
    // TODO

    // EPMC_COOCCURRENCES_FILE_NAME
    // TODO

    // EXPRESSION_SPECIFICITY_FILE_NAME
    // ensemblGeneId : string
    // expression: { bodyPartId : string; bodyPartLevel : string; bodyPartName : string; tpm : double }[]
    // expressionSpecificity: {
    //   adatissScores: { adatissScore : double; bodyPartId : string; bodyPartLevel : string; bodyPartName : string }[];
    //   gini : double;
    //   hpaDistribution : string;
    //   hpaSpecificity : string;
    // }[]

    // PHARMACOGENOMICS_FILE_NAME
    // datasourceId : string
    // datasourceVersion : string
    // datatypeId : string
    // drugFromSource : string
    // drugFromSourceId : string
    // drugId : string
    // evidenceLevel : string
    // genotype : string
    // genotypeAnnotationText : string
    // genotypeId : string
    // isDirectTarget : boolean
    // literature: string[]
    // pgxCategory : string
    // phenotypeFromSourceId : string
    // phenotypeText : string
    // studyId : string
    // targetFromSourceId : string
    // variantFunctionalConsequenceId : string
    // variantRsId : string

    // TARGET_ESSENTIALITY_FILE_NAME
    // id : string
    // geneEssentiality: {
    //   depMapEssentiality: {
    //     screens: { cellLineName : string; depmapId : string; diseaseCellLineId : string; diseaseFromSource : string; expression : double; geneEffect : double; mutation : string }[];
    //     tissueId : string;
    //     tissueName : string
    //   }[];
    //   isEssential : bool
    // }[]

    // TARGET_PRIORITISATION_FILE_NAME
    // targetId : string
    // isInMembrane : int
    // isSecreted : int
    // hasSafetyEvent : int
    // hasPocket : int
    // hasLigand : int
    // hasSmallMoleculeBinder : int
    // geneticConstraint : double
    // paralogMaxIdentityPercentage : double
    // mouseOrthologMaxIdentityPercentage : double
    // isCancerDriverGene : int
    // hasTEP : int
    // mouseKOScore : double
    // hasHighQualityChemicalProbes : int
    // maxClinicalTrialPhase : double
    // tissueSpecificity : double
    // tissueDistribution : double
}
