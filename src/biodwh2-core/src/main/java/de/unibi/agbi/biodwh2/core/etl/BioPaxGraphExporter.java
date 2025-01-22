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
 * General exporter for BioPAX level 3 files
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
        final Path filePath = dataSource.resolveSourceFilePath(workspace, getFileName());
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
        return true;
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
        // TODO
    }

    private void exportBiochemicalPathwayStep(final Graph graph, final BiochemicalPathwayStep entry) {
        // TODO
    }

    private void exportBiochemicalReaction(final Graph graph, final BiochemicalReaction entry) {
        // TODO
    }

    private void exportBioSource(final Graph graph, final BioSource entry) {
        // TODO
    }

    private void exportCatalysis(final Graph graph, final Catalysis entry) {
        // TODO
    }

    private void exportCellularLocationVocabulary(final Graph graph, final CellularLocationVocabulary entry) {
        // TODO
    }

    private void exportCellVocabulary(final Graph graph, final CellVocabulary entry) {
        // TODO
    }

    private void exportChemicalStructure(final Graph graph, final ChemicalStructure entry) {
        // TODO
    }

    private void exportComplex(final Graph graph, final Complex entry) {
        // TODO
    }

    private void exportComplexAssembly(final Graph graph, final ComplexAssembly entry) {
        // TODO
    }

    private void exportControl(final Graph graph, final Control entry) {
        // TODO
    }

    private void exportConversion(final Graph graph, final Conversion entry) {
        // TODO
    }

    private void exportDegradation(final Graph graph, final Degradation entry) {
        // TODO
    }

    private void exportDeltaG(final Graph graph, final DeltaG entry) {
        // TODO
    }

    private void exportDna(final Graph graph, final Dna entry) {
        // TODO
    }

    private void exportDnaReference(final Graph graph, final DnaReference entry) {
        // TODO
    }

    private void exportDnaRegion(final Graph graph, final DnaRegion entry) {
        // TODO
    }

    private void exportDnaRegionReference(final Graph graph, final DnaRegionReference entry) {
        // TODO
    }

    private void exportEntityFeature(final Graph graph, final EntityFeature entry) {
        // TODO
    }

    private void exportEntityReferenceTypeVocabulary(final Graph graph, final EntityReferenceTypeVocabulary entry) {
        // TODO
    }

    private void exportEvidence(final Graph graph, final Evidence entry) {
        // TODO
    }

    private void exportEvidenceCodeVocabulary(final Graph graph, final EvidenceCodeVocabulary entry) {
        // TODO
    }

    private void exportExperimentalForm(final Graph graph, final ExperimentalForm entry) {
        // TODO
    }

    private void exportExperimentalFormVocabulary(final Graph graph, final ExperimentalFormVocabulary entry) {
        // TODO
    }

    private void exportFragmentFeature(final Graph graph, final FragmentFeature entry) {
        // TODO
    }

    private void exportGene(final Graph graph, final Gene entry) {
        // TODO
    }

    private void exportGeneticInteraction(final Graph graph, final GeneticInteraction entry) {
        // TODO
    }

    private void exportInteraction(final Graph graph, final Interaction entry) {
        // TODO
    }

    private void exportInteractionVocabulary(final Graph graph, final InteractionVocabulary entry) {
        // TODO
    }

    private void exportModificationFeature(final Graph graph, final ModificationFeature entry) {
        // TODO
    }

    private void exportModulation(final Graph graph, final Modulation entry) {
        // TODO
    }

    private void exportMolecularInteraction(final Graph graph, final MolecularInteraction entry) {
        // TODO
    }

    private void exportPathway(final Graph graph, final Pathway entry) {
        // TODO
    }

    private void exportPathwayStep(final Graph graph, final PathwayStep entry) {
        // TODO
    }

    private void exportPhysicalEntity(final Graph graph, final PhysicalEntity entry) {
        // TODO
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        // TODO
    }

    private void exportProteinReference(final Graph graph, final ProteinReference entry) {
        // TODO
    }

    private void exportProvenance(final Graph graph, final Provenance entry) {
        // TODO
    }

    private void exportPublicationXref(final Graph graph, final PublicationXref entry) {
        // TODO
    }

    private void exportRelationshipTypeVocabulary(final Graph graph, final RelationshipTypeVocabulary entry) {
        // TODO
    }

    private void exportRelationshipXref(final Graph graph, final RelationshipXref entry) {
        // TODO
    }

    private void exportRna(final Graph graph, final Rna entry) {
        // TODO
    }

    private void exportRnaReference(final Graph graph, final RnaReference entry) {
        // TODO
    }

    private void exportRnaRegion(final Graph graph, final RnaRegion entry) {
        // TODO
    }

    private void exportRnaRegionReference(final Graph graph, final RnaRegionReference entry) {
        // TODO
    }

    private void exportScore(final Graph graph, final Score entry) {
        // TODO
    }

    private void exportSequenceInterval(final Graph graph, final SequenceInterval entry) {
        // TODO
    }

    private void exportSequenceLocation(final Graph graph, final SequenceLocation entry) {
        // TODO
    }

    private void exportSequenceModificationVocabulary(final Graph graph, final SequenceModificationVocabulary entry) {
        // TODO
    }

    private void exportSequenceRegionVocabulary(final Graph graph, final SequenceRegionVocabulary entry) {
        // TODO
    }

    private void exportSequenceSite(final Graph graph, final SequenceSite entry) {
        // TODO
    }

    private void exportSmallMolecule(final Graph graph, final SmallMolecule entry) {
        // TODO
    }

    private void exportSmallMoleculeReference(final Graph graph, final SmallMoleculeReference entry) {
        // TODO
    }

    private void exportStoichiometry(final Graph graph, final Stoichiometry entry) {
        // TODO
    }

    private void exportTemplateReaction(final Graph graph, final TemplateReaction entry) {
        // TODO
    }

    private void exportTissueVocabulary(final Graph graph, final TissueVocabulary entry) {
        // TODO
    }

    private void exportTransport(final Graph graph, final Transport entry) {
        // TODO
    }

    private void exportTransportWithBiochemicalReaction(final Graph graph,
                                                        final TransportWithBiochemicalReaction entry) {
        // TODO
    }

    private void exportUnificationXref(final Graph graph, final UnificationXref entry) {
        // TODO
    }

    private void exportTemplateReactionRegulation(final Graph graph, final TemplateReactionRegulation entry) {
        // TODO
    }
}
