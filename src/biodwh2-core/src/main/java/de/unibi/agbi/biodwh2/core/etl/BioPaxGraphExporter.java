package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.biopax.*;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * General exporter for <a href="https://www.biopax.org/release/biopax-level3-documentation.pdf">BioPAX level 3</a>
 * files.
 * <p/>
 * <a href="https://www.biopax.org/owldoc/Level3/">https://www.biopax.org/owldoc/Level3/</a>
 */
public abstract class BioPaxGraphExporter<D extends DataSource> extends GraphExporter<D> {
    private static final Logger LOGGER = LogManager.getLogger(BioPaxGraphExporter.class);

    private static class MappingClassAndConsumer<T> {
        public final Class<T> type;
        public final BiConsumer<Graph, T> consumer;

        private MappingClassAndConsumer(final Class<T> type, final BiConsumer<Graph, T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        public void consume(final Graph graph, final Object obj) {
            //noinspection unchecked
            consumer.accept(graph, (T) obj);
        }
    }

    private final Map<String, MappingClassAndConsumer<?>> bioPaxTypeConsumerMap = new HashMap<>();

    public BioPaxGraphExporter(final D dataSource) {
        super(dataSource);
        bioPaxTypeConsumerMap.put("BindingFeature",
                                  new MappingClassAndConsumer<>(BindingFeature.class, this::exportBindingFeature));
        bioPaxTypeConsumerMap.put("BiochemicalPathwayStep", new MappingClassAndConsumer<>(BiochemicalPathwayStep.class,
                                                                                          this::exportBiochemicalPathwayStep));
        bioPaxTypeConsumerMap.put("BiochemicalReaction", new MappingClassAndConsumer<>(BiochemicalReaction.class,
                                                                                       this::exportBiochemicalReaction));
        bioPaxTypeConsumerMap.put("BioSource", new MappingClassAndConsumer<>(BioSource.class, this::exportBioSource));
        bioPaxTypeConsumerMap.put("Catalysis", new MappingClassAndConsumer<>(Catalysis.class, this::exportCatalysis));
        bioPaxTypeConsumerMap.put("CellularLocationVocabulary",
                                  new MappingClassAndConsumer<>(CellularLocationVocabulary.class,
                                                                this::exportCellularLocationVocabulary));
        bioPaxTypeConsumerMap.put("CellVocabulary",
                                  new MappingClassAndConsumer<>(CellVocabulary.class, this::exportCellVocabulary));
        bioPaxTypeConsumerMap.put("ChemicalStructure", new MappingClassAndConsumer<>(ChemicalStructure.class,
                                                                                     this::exportChemicalStructure));
        bioPaxTypeConsumerMap.put("Complex", new MappingClassAndConsumer<>(Complex.class, this::exportComplex));
        bioPaxTypeConsumerMap.put("ComplexAssembly",
                                  new MappingClassAndConsumer<>(ComplexAssembly.class, this::exportComplexAssembly));
        bioPaxTypeConsumerMap.put("Control", new MappingClassAndConsumer<>(Control.class, this::exportControl));
        bioPaxTypeConsumerMap.put("Conversion",
                                  new MappingClassAndConsumer<>(Conversion.class, this::exportConversion));
        bioPaxTypeConsumerMap.put("Degradation",
                                  new MappingClassAndConsumer<>(Degradation.class, this::exportDegradation));
        bioPaxTypeConsumerMap.put("DeltaG", new MappingClassAndConsumer<>(DeltaG.class, this::exportDeltaG));
        bioPaxTypeConsumerMap.put("Dna", new MappingClassAndConsumer<>(Dna.class, this::exportDna));
        bioPaxTypeConsumerMap.put("DnaReference",
                                  new MappingClassAndConsumer<>(DnaReference.class, this::exportDnaReference));
        bioPaxTypeConsumerMap.put("DnaRegion", new MappingClassAndConsumer<>(DnaRegion.class, this::exportDnaRegion));
        bioPaxTypeConsumerMap.put("DnaRegionReference", new MappingClassAndConsumer<>(DnaRegionReference.class,
                                                                                      this::exportDnaRegionReference));
        bioPaxTypeConsumerMap.put("EntityFeature",
                                  new MappingClassAndConsumer<>(EntityFeature.class, this::exportEntityFeature));
        bioPaxTypeConsumerMap.put("EntityReferenceTypeVocabulary",
                                  new MappingClassAndConsumer<>(EntityReferenceTypeVocabulary.class,
                                                                this::exportEntityReferenceTypeVocabulary));
        bioPaxTypeConsumerMap.put("Evidence", new MappingClassAndConsumer<>(Evidence.class, this::exportEvidence));
        bioPaxTypeConsumerMap.put("EvidenceCodeVocabulary", new MappingClassAndConsumer<>(EvidenceCodeVocabulary.class,
                                                                                          this::exportEvidenceCodeVocabulary));
        bioPaxTypeConsumerMap.put("ExperimentalForm",
                                  new MappingClassAndConsumer<>(ExperimentalForm.class, this::exportExperimentalForm));
        bioPaxTypeConsumerMap.put("ExperimentalFormVocabulary",
                                  new MappingClassAndConsumer<>(ExperimentalFormVocabulary.class,
                                                                this::exportExperimentalFormVocabulary));
        bioPaxTypeConsumerMap.put("FragmentFeature",
                                  new MappingClassAndConsumer<>(FragmentFeature.class, this::exportFragmentFeature));
        bioPaxTypeConsumerMap.put("Gene", new MappingClassAndConsumer<>(Gene.class, this::exportGene));
        bioPaxTypeConsumerMap.put("GeneticInteraction", new MappingClassAndConsumer<>(GeneticInteraction.class,
                                                                                      this::exportGeneticInteraction));
        bioPaxTypeConsumerMap.put("Interaction",
                                  new MappingClassAndConsumer<>(Interaction.class, this::exportInteraction));
        bioPaxTypeConsumerMap.put("InteractionVocabulary", new MappingClassAndConsumer<>(InteractionVocabulary.class,
                                                                                         this::exportInteractionVocabulary));
        bioPaxTypeConsumerMap.put("ModificationFeature", new MappingClassAndConsumer<>(ModificationFeature.class,
                                                                                       this::exportModificationFeature));
        bioPaxTypeConsumerMap.put("Modulation",
                                  new MappingClassAndConsumer<>(Modulation.class, this::exportModulation));
        bioPaxTypeConsumerMap.put("MolecularInteraction", new MappingClassAndConsumer<>(MolecularInteraction.class,
                                                                                        this::exportMolecularInteraction));
        bioPaxTypeConsumerMap.put("Pathway", new MappingClassAndConsumer<>(Pathway.class, this::exportPathway));
        bioPaxTypeConsumerMap.put("PathwayStep",
                                  new MappingClassAndConsumer<>(PathwayStep.class, this::exportPathwayStep));
        bioPaxTypeConsumerMap.put("PhysicalEntity",
                                  new MappingClassAndConsumer<>(PhysicalEntity.class, this::exportPhysicalEntity));
        bioPaxTypeConsumerMap.put("Protein", new MappingClassAndConsumer<>(Protein.class, this::exportProtein));
        bioPaxTypeConsumerMap.put("ProteinReference",
                                  new MappingClassAndConsumer<>(ProteinReference.class, this::exportProteinReference));
        bioPaxTypeConsumerMap.put("Provenance",
                                  new MappingClassAndConsumer<>(Provenance.class, this::exportProvenance));
        bioPaxTypeConsumerMap.put("PublicationXref",
                                  new MappingClassAndConsumer<>(PublicationXref.class, this::exportPublicationXref));
        bioPaxTypeConsumerMap.put("RelationshipTypeVocabulary",
                                  new MappingClassAndConsumer<>(RelationshipTypeVocabulary.class,
                                                                this::exportRelationshipTypeVocabulary));
        bioPaxTypeConsumerMap.put("RelationshipXref",
                                  new MappingClassAndConsumer<>(RelationshipXref.class, this::exportRelationshipXref));
        bioPaxTypeConsumerMap.put("Rna", new MappingClassAndConsumer<>(Rna.class, this::exportRna));
        bioPaxTypeConsumerMap.put("RnaReference",
                                  new MappingClassAndConsumer<>(RnaReference.class, this::exportRnaReference));
        bioPaxTypeConsumerMap.put("RnaRegion", new MappingClassAndConsumer<>(RnaRegion.class, this::exportRnaRegion));
        bioPaxTypeConsumerMap.put("RnaRegionReference", new MappingClassAndConsumer<>(RnaRegionReference.class,
                                                                                      this::exportRnaRegionReference));
        bioPaxTypeConsumerMap.put("Score", new MappingClassAndConsumer<>(Score.class, this::exportScore));
        bioPaxTypeConsumerMap.put("SequenceInterval",
                                  new MappingClassAndConsumer<>(SequenceInterval.class, this::exportSequenceInterval));
        bioPaxTypeConsumerMap.put("SequenceLocation",
                                  new MappingClassAndConsumer<>(SequenceLocation.class, this::exportSequenceLocation));
        bioPaxTypeConsumerMap.put("SequenceModificationVocabulary",
                                  new MappingClassAndConsumer<>(SequenceModificationVocabulary.class,
                                                                this::exportSequenceModificationVocabulary));
        bioPaxTypeConsumerMap.put("SequenceRegionVocabulary",
                                  new MappingClassAndConsumer<>(SequenceRegionVocabulary.class,
                                                                this::exportSequenceRegionVocabulary));
        bioPaxTypeConsumerMap.put("SequenceSite",
                                  new MappingClassAndConsumer<>(SequenceSite.class, this::exportSequenceSite));
        bioPaxTypeConsumerMap.put("SmallMolecule",
                                  new MappingClassAndConsumer<>(SmallMolecule.class, this::exportSmallMolecule));
        bioPaxTypeConsumerMap.put("SmallMoleculeReference", new MappingClassAndConsumer<>(SmallMoleculeReference.class,
                                                                                          this::exportSmallMoleculeReference));
        bioPaxTypeConsumerMap.put("Stoichiometry",
                                  new MappingClassAndConsumer<>(Stoichiometry.class, this::exportStoichiometry));
        bioPaxTypeConsumerMap.put("TemplateReaction",
                                  new MappingClassAndConsumer<>(TemplateReaction.class, this::exportTemplateReaction));
        bioPaxTypeConsumerMap.put("TissueVocabulary",
                                  new MappingClassAndConsumer<>(TissueVocabulary.class, this::exportTissueVocabulary));
        bioPaxTypeConsumerMap.put("Transport", new MappingClassAndConsumer<>(Transport.class, this::exportTransport));
        bioPaxTypeConsumerMap.put("TransportWithBiochemicalReaction",
                                  new MappingClassAndConsumer<>(TransportWithBiochemicalReaction.class,
                                                                this::exportTransportWithBiochemicalReaction));
        bioPaxTypeConsumerMap.put("UnificationXref",
                                  new MappingClassAndConsumer<>(UnificationXref.class, this::exportUnificationXref));
        bioPaxTypeConsumerMap.put("TemplateReactionRegulation",
                                  new MappingClassAndConsumer<>(TemplateReactionRegulation.class,
                                                                this::exportTemplateReactionRegulation));
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        for (final String key : bioPaxTypeConsumerMap.keySet())
            graph.addIndex(IndexDescription.forNode(key, "about", IndexDescription.Type.UNIQUE));
        exportGraph(graph, dataSource.resolveSourceFilePath(workspace, getFileName()));
        return true;
    }

    private void exportGraph(final Graph graph, final Path filePath) {
        final String extension = FilenameUtils.getExtension(filePath.toString()).toLowerCase(Locale.ROOT);
        switch (extension) {
            case "gz":
                try (final InputStream stream = FileUtils.openGzip(filePath)) {
                    exportBioPaxStream(graph, stream);
                } catch (IOException | XMLStreamException e) {
                    throw new ExporterFormatException("Failed to export '" + getFileName() + "'", e);
                }
                break;
            case "zip":
                try {
                    FileUtils.forEachZipEntry(filePath, ".owl", (stream, entry) -> exportBioPaxStream(graph, stream));
                } catch (Exception e) {
                    throw new ExporterFormatException("Failed to export '" + getFileName() + "'", e);
                }
                break;
            case "owl":
                try (final InputStream stream = FileUtils.openInput(filePath)) {
                    exportBioPaxStream(graph, stream);
                } catch (IOException | XMLStreamException e) {
                    throw new ExporterFormatException("Failed to export '" + getFileName() + "'", e);
                }
                break;
            default:
                throw new ExporterFormatException(
                        "Failed to export '" + getFileName() + "'. Unknown extension '" + extension + "'");
        }
    }

    protected abstract String getFileName();

    protected void exportBioPaxStream(final Graph graph,
                                      final InputStream stream) throws XMLStreamException, IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final FromXmlParser parser = FileUtils.createXmlParser(stream, xmlMapper);
        // Skip the first structure token which is the root node
        //noinspection UnusedAssignment
        JsonToken token = parser.nextToken();
        String lastTypeKey = null;
        final Set<String> warnedTypeKeys = new HashSet<>();
        while ((token = parser.nextToken()) != null) {
            if ("FIELD_NAME".equals(token.name())) {
                lastTypeKey = parser.getValueAsString();
            }
            if (token.isStructStart() && lastTypeKey != null) {
                if (lastTypeKey.equals("Ontology")) {
                    continue; // ignored
                }
                final var consumer = bioPaxTypeConsumerMap.get(lastTypeKey);
                if (consumer != null) {
                    consumer.consume(graph, xmlMapper.readValue(parser, consumer.type));
                } else if (LOGGER.isWarnEnabled() && Character.isUpperCase(lastTypeKey.charAt(0)) &&
                           !warnedTypeKeys.contains(lastTypeKey)) {
                    LOGGER.warn("Unhandled BioPAX element '{}'", lastTypeKey);
                    warnedTypeKeys.add(lastTypeKey);
                }
            }
        }
    }

    private void exportBindingFeature(final Graph graph, final BindingFeature entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("BindingFeature");
        builder.withModel(entry);
        // TODO: bindsTo, evidence, featureLocation, featureLocationType
        builder.build();
    }

    private void exportBiochemicalPathwayStep(final Graph graph, final BiochemicalPathwayStep entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("BiochemicalPathwayStep");
        builder.withModel(entry);
        // TODO: evidence, nextStep, stepConversion, stepProcess
        builder.build();
    }

    private void exportBiochemicalReaction(final Graph graph, final BiochemicalReaction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("BiochemicalReaction");
        builder.withModel(entry);
        // TODO: dataSource, deltaG, evidence, interactionType, kEQ, left, right, xref, participantStoichiometry,
        //  participant
        builder.build();
    }

    private void exportBioSource(final Graph graph, final BioSource entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("BioSource");
        builder.withModel(entry);
        // TODO: cellType, tissue, xref
        builder.build();
    }

    private void exportCatalysis(final Graph graph, final Catalysis entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Catalysis");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportCellularLocationVocabulary(final Graph graph, final CellularLocationVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("CellularLocationVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportCellVocabulary(final Graph graph, final CellVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("CellVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportChemicalStructure(final Graph graph, final ChemicalStructure entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ChemicalStructure");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportComplex(final Graph graph, final Complex entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Complex");
        builder.withModel(entry);
        // TODO: cellularLocation, component, componentStoichiometry, dataSource, evidence, feature, notFeature,
        //  memberPhysicalEntity, xref
        builder.build();
    }

    private void exportComplexAssembly(final Graph graph, final ComplexAssembly entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ComplexAssembly");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportControl(final Graph graph, final Control entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Control");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportConversion(final Graph graph, final Conversion entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Conversion");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportDegradation(final Graph graph, final Degradation entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Degradation");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportDeltaG(final Graph graph, final DeltaG entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("DeltaG");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportDna(final Graph graph, final Dna entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Dna");
        builder.withModel(entry);
        // TODO:
        builder.build();
    }

    private void exportDnaReference(final Graph graph, final DnaReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("DnaReference");
        builder.withModel(entry);
        // TODO: organism, memberEntityReference, entityFeature, entityReferenceType, evidence, xref
        builder.build();
    }

    private void exportDnaRegion(final Graph graph, final DnaRegion entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("DnaRegion");
        builder.withModel(entry);
        // TODO: cellularLocation, dataSource, evidence, feature, notFeature, xref, entityReference,
        //  memberPhysicalEntity
        builder.build();
    }

    private void exportDnaRegionReference(final Graph graph, final DnaRegionReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("DnaRegionReference");
        builder.withModel(entry);
        // TODO: organism, memberEntityReference, entityFeature, entityReferenceType, evidence, xref
        builder.build();
    }

    private void exportEntityFeature(final Graph graph, final EntityFeature entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("EntityFeature");
        builder.withModel(entry);
        // TODO: evidence, featureLocation, featureLocationType
        builder.build();
    }

    private void exportEntityReferenceTypeVocabulary(final Graph graph, final EntityReferenceTypeVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("EntityReferenceTypeVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportEvidence(final Graph graph, final Evidence entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Evidence");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportEvidenceCodeVocabulary(final Graph graph, final EvidenceCodeVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("EvidenceCodeVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportExperimentalForm(final Graph graph, final ExperimentalForm entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ExperimentalForm");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportExperimentalFormVocabulary(final Graph graph, final ExperimentalFormVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ExperimentalFormVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportFragmentFeature(final Graph graph, final FragmentFeature entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("FragmentFeature");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportGene(final Graph graph, final Gene entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Gene");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportGeneticInteraction(final Graph graph, final GeneticInteraction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("GeneticInteraction");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportInteraction(final Graph graph, final Interaction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Interaction");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportInteractionVocabulary(final Graph graph, final InteractionVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("InteractionVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportModificationFeature(final Graph graph, final ModificationFeature entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ModificationFeature");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportModulation(final Graph graph, final Modulation entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Modulation");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportMolecularInteraction(final Graph graph, final MolecularInteraction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("MolecularInteraction");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportPathway(final Graph graph, final Pathway entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Pathway");
        builder.withModel(entry);
        // TODO: dataSource, evidence, organism, pathwayComponent, pathwayOrder, xref
        builder.build();
    }

    private void exportPathwayStep(final Graph graph, final PathwayStep entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("PathwayStep");
        builder.withModel(entry);
        // TODO: evidence, nextStep, stepProcess
        builder.build();
    }

    private void exportPhysicalEntity(final Graph graph, final PhysicalEntity entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("PhysicalEntity");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Protein");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportProteinReference(final Graph graph, final ProteinReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("ProteinReference");
        builder.withModel(entry);
        // TODO: organism, memberEntityReference, entityFeature, entityReferenceType, evidence, xref
        builder.build();
    }

    private void exportProvenance(final Graph graph, final Provenance entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Provenance");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportPublicationXref(final Graph graph, final PublicationXref entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("PublicationXref");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRelationshipTypeVocabulary(final Graph graph, final RelationshipTypeVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("RelationshipTypeVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRelationshipXref(final Graph graph, final RelationshipXref entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("RelationshipXref");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRna(final Graph graph, final Rna entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Rna");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRnaReference(final Graph graph, final RnaReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("RnaReference");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRnaRegion(final Graph graph, final RnaRegion entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("RnaRegion");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportRnaRegionReference(final Graph graph, final RnaRegionReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("RnaRegionReference");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportScore(final Graph graph, final Score entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Score");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSequenceInterval(final Graph graph, final SequenceInterval entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SequenceInterval");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSequenceLocation(final Graph graph, final SequenceLocation entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SequenceLocation");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSequenceModificationVocabulary(final Graph graph, final SequenceModificationVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SequenceModificationVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSequenceRegionVocabulary(final Graph graph, final SequenceRegionVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SequenceRegionVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSequenceSite(final Graph graph, final SequenceSite entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SequenceSite");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportSmallMolecule(final Graph graph, final SmallMolecule entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SmallMolecule");
        builder.withModel(entry);
        // TODO: cellularLocation, dataSource, entityReference, evidence, feature, notFeature, xref,
        //  memberPhysicalEntity
        builder.build();
    }

    private void exportSmallMoleculeReference(final Graph graph, final SmallMoleculeReference entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("SmallMoleculeReference");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportStoichiometry(final Graph graph, final Stoichiometry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Stoichiometry");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportTemplateReaction(final Graph graph, final TemplateReaction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("TemplateReaction");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportTissueVocabulary(final Graph graph, final TissueVocabulary entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("TissueVocabulary");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportTransport(final Graph graph, final Transport entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Transport");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportTransportWithBiochemicalReaction(final Graph graph,
                                                        final TransportWithBiochemicalReaction entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("TransportWithBiochemicalReaction");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportUnificationXref(final Graph graph, final UnificationXref entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("UnificationXref");
        builder.withModel(entry);
        // TODO
        builder.build();
    }

    private void exportTemplateReactionRegulation(final Graph graph, final TemplateReactionRegulation entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("TemplateReactionRegulation");
        builder.withModel(entry);
        // TODO
        builder.build();
    }
}
