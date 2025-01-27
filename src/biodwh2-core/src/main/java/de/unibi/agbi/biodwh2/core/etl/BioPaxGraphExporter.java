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
import de.unibi.agbi.biodwh2.core.model.graph.Node;
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

    private static class EdgeInfo {
        public String source;
        public Long sourceId;
        public String target;
        public Long targetId;
        public String label;

        public EdgeInfo(final String source, final String target, final String label) {
            this.source = source;
            this.target = target;
            this.label = label;
        }

        public EdgeInfo(final Long sourceId, final String target, final String label) {
            this.sourceId = sourceId;
            this.target = target;
            this.label = label;
        }

        public EdgeInfo(final String source, final Long targetId, final String label) {
            this.source = source;
            this.targetId = targetId;
            this.label = label;
        }
    }

    private final Map<String, MappingClassAndConsumer<?>> bioPaxTypeConsumerMap = new HashMap<>();
    private final List<EdgeInfo> edges = new ArrayList<>();

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
        edges.clear();
        for (final String key : bioPaxTypeConsumerMap.keySet())
            graph.addIndex(IndexDescription.forNode(key, "about", IndexDescription.Type.UNIQUE));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting nodes...");
        exportGraph(graph, dataSource.resolveSourceFilePath(workspace, getFileName()));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting {} edges...", edges.size());
        for (final EdgeInfo edge : edges) {
            final var source = edge.sourceId != null ? edge.sourceId : graph.findNodeId("about", edge.source);
            final var target = edge.targetId != null ? edge.targetId : graph.findNodeId("about", edge.target);
            if (source != null && target != null)
                graph.addEdge(source, target, edge.label);
        }
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
        long testCounter = 0;
        final Set<String> warnedTypeKeys = new HashSet<>();
        while ((token = parser.nextToken()) != null) {
            if ("FIELD_NAME".equals(token.name())) {
                lastTypeKey = parser.getValueAsString();
            }
            if (token.isStructStart() && lastTypeKey != null) {
                if (lastTypeKey.equals("Ontology")) {
                    continue; // ignored
                }
                testCounter++;
                if (testCounter >= 1_000_000) {
                    break;
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
        final Node node = graph.buildNode().withLabel("BindingFeature").withModel(entry).build();
        addRelations(node.getId(), entry);
        if (entry.bindsTo != null)
            for (final ResourceRef ref : entry.bindsTo)
                edges.add(new EdgeInfo(node.getId(), ref.resource, "BINDS_TO"));
    }

    private void exportBiochemicalPathwayStep(final Graph graph, final BiochemicalPathwayStep entry) {
        final Node node = graph.buildNode().withLabel("BiochemicalPathwayStep").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: stepConversion
    }

    private void exportBiochemicalReaction(final Graph graph, final BiochemicalReaction entry) {
        final Node node = graph.buildNode().withLabel("BiochemicalReaction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: deltaG, interactionType, kEQ, left, right, participantStoichiometry, participant
    }

    private void exportBioSource(final Graph graph, final BioSource entry) {
        final Node node = graph.buildNode().withLabel("BioSource").withModel(entry).build();
        if (entry.xref != null)
            edges.add(new EdgeInfo(node.getId(), entry.xref.resource, "HAS_XREF"));
        // TODO: cellType, tissue
    }

    private void exportCatalysis(final Graph graph, final Catalysis entry) {
        final Node node = graph.buildNode().withLabel("Catalysis").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportCellularLocationVocabulary(final Graph graph, final CellularLocationVocabulary entry) {
        final Node node = graph.buildNode().withLabel("CellularLocationVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportCellVocabulary(final Graph graph, final CellVocabulary entry) {
        final Node node = graph.buildNode().withLabel("CellVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportChemicalStructure(final Graph graph, final ChemicalStructure entry) {
        graph.buildNode().withLabel("ChemicalStructure").withModel(entry).build();
    }

    private void exportComplex(final Graph graph, final Complex entry) {
        final Node node = graph.buildNode().withLabel("Complex").withModel(entry).build();
        // TODO: cellularLocation, componentStoichiometry, feature, notFeature, memberPhysicalEntity
        addRelations(node.getId(), entry);
        if (entry.component != null)
            for (final var component : entry.component)
                edges.add(new EdgeInfo(node.getId(), component.resource, "HAS_COMPONENT"));
    }

    private void exportComplexAssembly(final Graph graph, final ComplexAssembly entry) {
        final Node node = graph.buildNode().withLabel("ComplexAssembly").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportControl(final Graph graph, final Control entry) {
        final Node node = graph.buildNode().withLabel("Control").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportConversion(final Graph graph, final Conversion entry) {
        final Node node = graph.buildNode().withLabel("Conversion").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportDegradation(final Graph graph, final Degradation entry) {
        final Node node = graph.buildNode().withLabel("Degradation").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportDeltaG(final Graph graph, final DeltaG entry) {
        graph.buildNode().withLabel("DeltaG").withModel(entry).build();
    }

    private void exportDna(final Graph graph, final Dna entry) {
        final Node node = graph.buildNode().withLabel("Dna").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: entityReference
    }

    private void exportDnaReference(final Graph graph, final DnaReference entry) {
        final Node node = graph.buildNode().withLabel("DnaReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: organism
    }

    private void exportDnaRegion(final Graph graph, final DnaRegion entry) {
        final Node node = graph.buildNode().withLabel("DnaRegion").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: cellularLocation, feature, notFeature, entityReference, memberPhysicalEntity
    }

    private void exportDnaRegionReference(final Graph graph, final DnaRegionReference entry) {
        final Node node = graph.buildNode().withLabel("DnaRegionReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: organism
    }

    private void exportEntityFeature(final Graph graph, final EntityFeature entry) {
        final Node node = graph.buildNode().withLabel("EntityFeature").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportEntityReferenceTypeVocabulary(final Graph graph, final EntityReferenceTypeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("EntityReferenceTypeVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportEvidence(final Graph graph, final Evidence entry) {
        final Node node = graph.buildNode().withLabel("Evidence").withModel(entry).build();
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                edges.add(new EdgeInfo(node.getId(), ref.resource, "HAS_XREF"));
        // TODO: confidence, evidenceCode, experimentalForm
    }

    private void exportEvidenceCodeVocabulary(final Graph graph, final EvidenceCodeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("EvidenceCodeVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportExperimentalForm(final Graph graph, final ExperimentalForm entry) {
        final Node node = graph.buildNode().withLabel("ExperimentalForm").withModel(entry).build();
        // TODO
    }

    private void exportExperimentalFormVocabulary(final Graph graph, final ExperimentalFormVocabulary entry) {
        final Node node = graph.buildNode().withLabel("ExperimentalFormVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportFragmentFeature(final Graph graph, final FragmentFeature entry) {
        final Node node = graph.buildNode().withLabel("FragmentFeature").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportGene(final Graph graph, final Gene entry) {
        final Node node = graph.buildNode().withLabel("Gene").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportGeneticInteraction(final Graph graph, final GeneticInteraction entry) {
        final Node node = graph.buildNode().withLabel("GeneticInteraction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportInteraction(final Graph graph, final Interaction entry) {
        final Node node = graph.buildNode().withLabel("Interaction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: participant, interactionType
    }

    private void exportInteractionVocabulary(final Graph graph, final InteractionVocabulary entry) {
        final Node node = graph.buildNode().withLabel("InteractionVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportModificationFeature(final Graph graph, final ModificationFeature entry) {
        final Node node = graph.buildNode().withLabel("ModificationFeature").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportModulation(final Graph graph, final Modulation entry) {
        final Node node = graph.buildNode().withLabel("Modulation").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportMolecularInteraction(final Graph graph, final MolecularInteraction entry) {
        final Node node = graph.buildNode().withLabel("MolecularInteraction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportPathway(final Graph graph, final Pathway entry) {
        final Node node = graph.buildNode().withLabel("Pathway").withModel(entry).build();
        // TODO: pathwayOrder
        addRelations(node.getId(), entry);
        if (entry.organism != null)
            edges.add(new EdgeInfo(node.getId(), entry.organism.resource, "BELONGS_TO"));
        if (entry.pathwayComponent != null)
            for (final var component : entry.pathwayComponent)
                edges.add(new EdgeInfo(node.getId(), component.resource, "HAS_COMPONENT"));
    }

    private void exportPathwayStep(final Graph graph, final PathwayStep entry) {
        final Node node = graph.buildNode().withLabel("PathwayStep").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportPhysicalEntity(final Graph graph, final PhysicalEntity entry) {
        final Node node = graph.buildNode().withLabel("PhysicalEntity").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        final Node node = graph.buildNode().withLabel("Protein").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportProteinReference(final Graph graph, final ProteinReference entry) {
        final Node node = graph.buildNode().withLabel("ProteinReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: organism, memberEntityReference, entityFeature, entityReferenceType
    }

    private void exportProvenance(final Graph graph, final Provenance entry) {
        final Node node = graph.buildNode().withLabel("Provenance").withModel(entry).build();
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                edges.add(new EdgeInfo(node.getId(), ref.resource, "HAS_XREF"));
    }

    private void exportPublicationXref(final Graph graph, final PublicationXref entry) {
        graph.buildNode().withLabel("PublicationXref").withModel(entry).build();
    }

    private void exportRelationshipTypeVocabulary(final Graph graph, final RelationshipTypeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("RelationshipTypeVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportRelationshipXref(final Graph graph, final RelationshipXref entry) {
        final Node node = graph.buildNode().withLabel("RelationshipXref").withModel(entry).build();
        // TODO: relationshipType
    }

    private void exportRna(final Graph graph, final Rna entry) {
        final Node node = graph.buildNode().withLabel("Rna").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportRnaReference(final Graph graph, final RnaReference entry) {
        final Node node = graph.buildNode().withLabel("RnaReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportRnaRegion(final Graph graph, final RnaRegion entry) {
        final Node node = graph.buildNode().withLabel("RnaRegion").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportRnaRegionReference(final Graph graph, final RnaRegionReference entry) {
        final Node node = graph.buildNode().withLabel("RnaRegionReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportScore(final Graph graph, final Score entry) {
        final Node node = graph.buildNode().withLabel("Score").withModel(entry).build();
        // TODO
    }

    private void exportSequenceInterval(final Graph graph, final SequenceInterval entry) {
        final Node node = graph.buildNode().withLabel("SequenceInterval").withModel(entry).build();
        // TODO
    }

    private void exportSequenceLocation(final Graph graph, final SequenceLocation entry) {
        final Node node = graph.buildNode().withLabel("SequenceLocation").withModel(entry).build();
        // TODO
    }

    private void exportSequenceModificationVocabulary(final Graph graph, final SequenceModificationVocabulary entry) {
        final Node node = graph.buildNode().withLabel("SequenceModificationVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportSequenceRegionVocabulary(final Graph graph, final SequenceRegionVocabulary entry) {
        final Node node = graph.buildNode().withLabel("SequenceRegionVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportSequenceSite(final Graph graph, final SequenceSite entry) {
        final Node node = graph.buildNode().withLabel("SequenceSite").withModel(entry).build();
        // TODO
    }

    private void exportSmallMolecule(final Graph graph, final SmallMolecule entry) {
        final Node node = graph.buildNode().withLabel("SmallMolecule").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: cellularLocation, entityReference, feature, notFeature, memberPhysicalEntity
    }

    private void exportSmallMoleculeReference(final Graph graph, final SmallMoleculeReference entry) {
        final Node node = graph.buildNode().withLabel("SmallMoleculeReference").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportStoichiometry(final Graph graph, final Stoichiometry entry) {
        final Node node = graph.buildNode().withLabel("Stoichiometry").withModel(entry).build();
        // TODO
    }

    private void exportTemplateReaction(final Graph graph, final TemplateReaction entry) {
        final Node node = graph.buildNode().withLabel("TemplateReaction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportTissueVocabulary(final Graph graph, final TissueVocabulary entry) {
        final Node node = graph.buildNode().withLabel("TissueVocabulary").withModel(entry).build();
        addRelations(node.getId(), entry);
    }

    private void exportTransport(final Graph graph, final Transport entry) {
        final Node node = graph.buildNode().withLabel("Transport").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportTransportWithBiochemicalReaction(final Graph graph,
                                                        final TransportWithBiochemicalReaction entry) {
        final Node node = graph.buildNode().withLabel("TransportWithBiochemicalReaction").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO
    }

    private void exportUnificationXref(final Graph graph, final UnificationXref entry) {
        graph.buildNode().withLabel("UnificationXref").withModel(entry).build();
    }

    private void exportTemplateReactionRegulation(final Graph graph, final TemplateReactionRegulation entry) {
        final Node node = graph.buildNode().withLabel("TemplateReactionRegulation").withModel(entry).build();
        addRelations(node.getId(), entry);
        // TODO: controlled, controller, interactionType, participant
    }

    private void addRelations(final Long id, final ControlledVocabulary entry) {
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_XREF"));
    }

    private void addRelations(final Long id, final EntityReference entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_EVIDENCE"));
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_XREF"));
        // TODO: entityFeature, entityReferenceType, memberEntityReference
    }

    private void addRelations(final Long id, final Entity entry) {
        if (entry.evidence != null)
            edges.add(new EdgeInfo(id, entry.evidence.resource, "HAS_EVIDENCE"));
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_XREF"));
        // TODO: dataSource
    }

    private void addRelations(final Long id, final EntityFeature entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_EVIDENCE"));
        // TODO: featureLocation, featureLocationType, memberFeature
    }

    private void addRelations(final Long id, final PathwayStep entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                edges.add(new EdgeInfo(id, ref.resource, "HAS_EVIDENCE"));
        if (entry.nextStep != null)
            for (final var nextStep : entry.nextStep)
                edges.add(new EdgeInfo(id, nextStep.resource, "HAS_NEXT_STEP"));
        // TODO: stepProcess
    }
}
