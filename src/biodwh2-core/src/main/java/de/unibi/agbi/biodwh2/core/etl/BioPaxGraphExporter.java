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
    static final String ABOUT_KEY = "about";

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
        public long sourceId;
        public String target;

        public EdgeInfo(final long sourceId, final String target) {
            this.sourceId = sourceId;
            this.target = target;
        }
    }

    private final Map<String, MappingClassAndConsumer<?>> bioPaxTypeConsumerMap = new HashMap<>();
    private final Map<String, List<EdgeInfo>> edges = new HashMap<>();

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
            graph.addIndex(IndexDescription.forNode(key, ABOUT_KEY, IndexDescription.Type.UNIQUE));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting nodes...");
        exportGraph(graph, dataSource.resolveSourceFilePath(workspace, getFileName()));
        for (final String label : edges.keySet()) {
            graph.beginEdgeIndicesDelay(label);
            final List<EdgeInfo> labelEdges = edges.get(label);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting {} edges with label {}...", labelEdges.size(), label);
            for (final EdgeInfo edge : labelEdges) {
                final var target = graph.findNodeId(ABOUT_KEY, edge.target);
                if (target != null)
                    graph.addEdge(edge.sourceId, target, label);
            }
            graph.endEdgeIndicesDelay(label);
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
        final Set<String> warnedTypeKeys = new HashSet<>();
        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.FIELD_NAME) {
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
        final Node node = graph.buildNode().withLabel("BindingFeature").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.bindsTo != null)
            for (final ResourceRef ref : entry.bindsTo)
                addEdge(graph, "BINDS_TO", node.getId(), ref.resource);
    }

    private void addEdge(final Graph graph, final String label, final long sourceNodeId, final String target) {
        final var targetNode = graph.findNodeId(ABOUT_KEY, target);
        if (targetNode != null) {
            graph.addEdge(sourceNodeId, targetNode, label);
        } else {
            edges.computeIfAbsent(label, k -> new ArrayList<>()).add(new EdgeInfo(sourceNodeId, target));
        }
    }

    private void exportBiochemicalPathwayStep(final Graph graph, final BiochemicalPathwayStep entry) {
        final Node node = graph.buildNode().withLabel("BiochemicalPathwayStep").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.stepConversion != null)
            addEdge(graph, "HAS_CONVERSION", node.getId(), entry.stepConversion.resource);
    }

    private void exportBiochemicalReaction(final Graph graph, final BiochemicalReaction entry) {
        final Node node = graph.buildNode().withLabel("BiochemicalReaction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.deltaG != null)
            for (final ResourceRef ref : entry.deltaG)
                addEdge(graph, "HAS_DELTA_G", node.getId(), ref.resource);
        if (entry.kEQ != null)
            for (final ResourceRef ref : entry.kEQ)
                addEdge(graph, "HAS_K_EQ", node.getId(), ref.resource);
    }

    private void exportBioSource(final Graph graph, final BioSource entry) {
        final Node node = graph.buildNode().withLabel("BioSource").withModel(entry).build();
        if (entry.xref != null)
            addEdge(graph, "HAS_XREF", node.getId(), entry.xref.resource);
        if (entry.tissue != null)
            addEdge(graph, "HAS_TISSUE", node.getId(), entry.tissue.resource);
        if (entry.cellType != null)
            for (final var cellType : entry.cellType)
                addEdge(graph, "HAS_CELL_TYPE", node.getId(), cellType.resource);
    }

    private void exportCatalysis(final Graph graph, final Catalysis entry) {
        final Node node = graph.buildNode().withLabel("Catalysis").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.cofactor != null)
            for (final var ref : entry.cofactor)
                addEdge(graph, "HAS_COFACTOR", node.getId(), ref.resource);
    }

    private void exportCellularLocationVocabulary(final Graph graph, final CellularLocationVocabulary entry) {
        final Node node = graph.buildNode().withLabel("CellularLocationVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportCellVocabulary(final Graph graph, final CellVocabulary entry) {
        final Node node = graph.buildNode().withLabel("CellVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportChemicalStructure(final Graph graph, final ChemicalStructure entry) {
        graph.buildNode().withLabel("ChemicalStructure").withModel(entry).build();
    }

    private void exportComplex(final Graph graph, final Complex entry) {
        final Node node = graph.buildNode().withLabel("Complex").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.component != null)
            for (final var ref : entry.component)
                addEdge(graph, "HAS_COMPONENT", node.getId(), ref.resource);
        if (entry.componentStoichiometry != null)
            for (final var ref : entry.componentStoichiometry)
                addEdge(graph, "HAS_COMPONENT_STOICHIOMETRY", node.getId(), ref.resource);
    }

    private void exportComplexAssembly(final Graph graph, final ComplexAssembly entry) {
        final Node node = graph.buildNode().withLabel("ComplexAssembly").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportControl(final Graph graph, final Control entry) {
        final Node node = graph.buildNode().withLabel("Control").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportConversion(final Graph graph, final Conversion entry) {
        final Node node = graph.buildNode().withLabel("Conversion").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportDegradation(final Graph graph, final Degradation entry) {
        final Node node = graph.buildNode().withLabel("Degradation").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportDeltaG(final Graph graph, final DeltaG entry) {
        graph.buildNode().withLabel("DeltaG").withModel(entry).build();
    }

    private void exportDna(final Graph graph, final Dna entry) {
        final Node node = graph.buildNode().withLabel("Dna").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportDnaReference(final Graph graph, final DnaReference entry) {
        final Node node = graph.buildNode().withLabel("DnaReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
    }

    private void exportDnaRegion(final Graph graph, final DnaRegion entry) {
        final Node node = graph.buildNode().withLabel("DnaRegion").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportDnaRegionReference(final Graph graph, final DnaRegionReference entry) {
        final Node node = graph.buildNode().withLabel("DnaRegionReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
    }

    private void exportEntityFeature(final Graph graph, final EntityFeature entry) {
        final Node node = graph.buildNode().withLabel("EntityFeature").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportEntityReferenceTypeVocabulary(final Graph graph, final EntityReferenceTypeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("EntityReferenceTypeVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportEvidence(final Graph graph, final Evidence entry) {
        final Node node = graph.buildNode().withLabel("Evidence").withModel(entry).build();
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                addEdge(graph, "HAS_XREF", node.getId(), ref.resource);
        if (entry.confidence != null)
            for (final ResourceRef ref : entry.confidence)
                addEdge(graph, "HAS_CONFIDENCE", node.getId(), ref.resource);
        if (entry.experimentalForm != null)
            for (final ResourceRef ref : entry.experimentalForm)
                addEdge(graph, "HAS_EXPERIMENTAL_FORM", node.getId(), ref.resource);
        if (entry.evidenceCode != null)
            addEdge(graph, "HAS_EVIDENCE_CODE", node.getId(), entry.evidenceCode.resource);
    }

    private void exportEvidenceCodeVocabulary(final Graph graph, final EvidenceCodeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("EvidenceCodeVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportExperimentalForm(final Graph graph, final ExperimentalForm entry) {
        final Node node = graph.buildNode().withLabel("ExperimentalForm").withModel(entry).build();
        // TODO: experimentalFormDescription, experimentalFormEntity, experimentalFeature
    }

    private void exportExperimentalFormVocabulary(final Graph graph, final ExperimentalFormVocabulary entry) {
        final Node node = graph.buildNode().withLabel("ExperimentalFormVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportFragmentFeature(final Graph graph, final FragmentFeature entry) {
        final Node node = graph.buildNode().withLabel("FragmentFeature").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportGene(final Graph graph, final Gene entry) {
        final Node node = graph.buildNode().withLabel("Gene").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
    }

    private void exportGeneticInteraction(final Graph graph, final GeneticInteraction entry) {
        final Node node = graph.buildNode().withLabel("GeneticInteraction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.interactionScore != null)
            for (final ResourceRef ref : entry.interactionScore)
                addEdge(graph, "HAS_SCORE", node.getId(), ref.resource);
        if (entry.phenotype != null)
            for (final ResourceRef ref : entry.phenotype)
                addEdge(graph, "HAS_PHENOTYPE", node.getId(), ref.resource);
    }

    private void exportInteraction(final Graph graph, final Interaction entry) {
        final Node node = graph.buildNode().withLabel("Interaction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportInteractionVocabulary(final Graph graph, final InteractionVocabulary entry) {
        final Node node = graph.buildNode().withLabel("InteractionVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportModificationFeature(final Graph graph, final ModificationFeature entry) {
        final Node node = graph.buildNode().withLabel("ModificationFeature").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.modificationType != null)
            addEdge(graph, "HAS_MODIFICATION_TYPE", node.getId(), entry.modificationType.resource);
    }

    private void exportModulation(final Graph graph, final Modulation entry) {
        final Node node = graph.buildNode().withLabel("Modulation").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportMolecularInteraction(final Graph graph, final MolecularInteraction entry) {
        final Node node = graph.buildNode().withLabel("MolecularInteraction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportPathway(final Graph graph, final Pathway entry) {
        final Node node = graph.buildNode().withLabel("Pathway").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
        if (entry.pathwayComponent != null)
            for (final var component : entry.pathwayComponent)
                addEdge(graph, "HAS_COMPONENT", node.getId(), component.resource);
        if (entry.pathwayOrder != null)
            for (final ResourceRef ref : entry.pathwayOrder)
                addEdge(graph, "HAS_ORDER", node.getId(), ref.resource);
    }

    private void exportPathwayStep(final Graph graph, final PathwayStep entry) {
        final Node node = graph.buildNode().withLabel("PathwayStep").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportPhysicalEntity(final Graph graph, final PhysicalEntity entry) {
        final Node node = graph.buildNode().withLabel("PhysicalEntity").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        final Node node = graph.buildNode().withLabel("Protein").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportProteinReference(final Graph graph, final ProteinReference entry) {
        final Node node = graph.buildNode().withLabel("ProteinReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
        // TODO: memberEntityReference, entityFeature, entityReferenceType
    }

    private void exportProvenance(final Graph graph, final Provenance entry) {
        final Node node = graph.buildNode().withLabel("Provenance").withModel(entry).build();
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                addEdge(graph, "HAS_XREF", node.getId(), ref.resource);
    }

    private void exportPublicationXref(final Graph graph, final PublicationXref entry) {
        graph.buildNode().withLabel("PublicationXref").withModel(entry).build();
    }

    private void exportRelationshipTypeVocabulary(final Graph graph, final RelationshipTypeVocabulary entry) {
        final Node node = graph.buildNode().withLabel("RelationshipTypeVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportRelationshipXref(final Graph graph, final RelationshipXref entry) {
        final Node node = graph.buildNode().withLabel("RelationshipXref").withModel(entry).build();
        if (entry.relationshipType != null)
            for (final ResourceRef ref : entry.relationshipType)
                addEdge(graph, "OF_TYPE", node.getId(), ref.resource);
    }

    private void exportRna(final Graph graph, final Rna entry) {
        final Node node = graph.buildNode().withLabel("Rna").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportRnaReference(final Graph graph, final RnaReference entry) {
        final Node node = graph.buildNode().withLabel("RnaReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
    }

    private void exportRnaRegion(final Graph graph, final RnaRegion entry) {
        final Node node = graph.buildNode().withLabel("RnaRegion").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportRnaRegionReference(final Graph graph, final RnaRegionReference entry) {
        final Node node = graph.buildNode().withLabel("RnaRegionReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.organism != null)
            addEdge(graph, "BELONGS_TO", node.getId(), entry.organism.resource);
    }

    private void exportScore(final Graph graph, final Score entry) {
        final Node node = graph.buildNode().withLabel("Score").withModel(entry).build();
        if (entry.scoreSource != null)
            addEdge(graph, "HAS_SOURCE", node.getId(), entry.scoreSource.resource);
    }

    private void exportSequenceInterval(final Graph graph, final SequenceInterval entry) {
        final Node node = graph.buildNode().withLabel("SequenceInterval").withModel(entry).build();
        if (entry.sequenceIntervalBegin != null)
            addEdge(graph, "HAS_INTERVAL_BEGIN", node.getId(), entry.sequenceIntervalBegin.resource);
        if (entry.sequenceIntervalEnd != null)
            addEdge(graph, "HAS_INTERVAL_END", node.getId(), entry.sequenceIntervalEnd.resource);
    }

    private void exportSequenceLocation(final Graph graph, final SequenceLocation entry) {
        graph.buildNode().withLabel("SequenceLocation").withModel(entry).build();
    }

    private void exportSequenceModificationVocabulary(final Graph graph, final SequenceModificationVocabulary entry) {
        final Node node = graph.buildNode().withLabel("SequenceModificationVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportSequenceRegionVocabulary(final Graph graph, final SequenceRegionVocabulary entry) {
        final Node node = graph.buildNode().withLabel("SequenceRegionVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportSequenceSite(final Graph graph, final SequenceSite entry) {
        graph.buildNode().withLabel("SequenceSite").withModel(entry).build();
    }

    private void exportSmallMolecule(final Graph graph, final SmallMolecule entry) {
        final Node node = graph.buildNode().withLabel("SmallMolecule").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.entityReference != null)
            for (final ResourceRef ref : entry.entityReference)
                addEdge(graph, "HAS_ENTITY", node.getId(), ref.resource);
    }

    private void exportSmallMoleculeReference(final Graph graph, final SmallMoleculeReference entry) {
        final Node node = graph.buildNode().withLabel("SmallMoleculeReference").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.structure != null)
            addEdge(graph, "HAS_STRUCTURE", node.getId(), entry.structure.resource);
    }

    private void exportStoichiometry(final Graph graph, final Stoichiometry entry) {
        final Node node = graph.buildNode().withLabel("Stoichiometry").withModel(entry).build();
        // TODO: physicalEntity
    }

    private void exportTemplateReaction(final Graph graph, final TemplateReaction entry) {
        final Node node = graph.buildNode().withLabel("TemplateReaction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.product != null)
            for (final ResourceRef ref : entry.product)
                addEdge(graph, "HAS_PRODUCT", node.getId(), ref.resource);
        if (entry.template != null)
            addEdge(graph, "HAS_TEMPLATE", node.getId(), entry.template.resource);
    }

    private void exportTissueVocabulary(final Graph graph, final TissueVocabulary entry) {
        final Node node = graph.buildNode().withLabel("TissueVocabulary").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportTransport(final Graph graph, final Transport entry) {
        final Node node = graph.buildNode().withLabel("Transport").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void exportTransportWithBiochemicalReaction(final Graph graph,
                                                        final TransportWithBiochemicalReaction entry) {
        final Node node = graph.buildNode().withLabel("TransportWithBiochemicalReaction").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
        if (entry.deltaG != null)
            for (final ResourceRef ref : entry.deltaG)
                addEdge(graph, "HAS_DELTA_G", node.getId(), ref.resource);
        if (entry.kEQ != null)
            for (final ResourceRef ref : entry.kEQ)
                addEdge(graph, "HAS_K_EQ", node.getId(), ref.resource);
    }

    private void exportUnificationXref(final Graph graph, final UnificationXref entry) {
        graph.buildNode().withLabel("UnificationXref").withModel(entry).build();
    }

    private void exportTemplateReactionRegulation(final Graph graph, final TemplateReactionRegulation entry) {
        final Node node = graph.buildNode().withLabel("TemplateReactionRegulation").withModel(entry).build();
        addRelations(graph, node.getId(), entry);
    }

    private void addRelations(final Graph graph, final Long id, final ControlledVocabulary entry) {
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                addEdge(graph, "HAS_XREF", id, ref.resource);
    }

    private void addRelations(final Graph graph, final Long id, final EntityReference entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                addEdge(graph, "HAS_EVIDENCE", id, ref.resource);
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                addEdge(graph, "HAS_XREF", id, ref.resource);
        if (entry.entityReferenceType != null)
            for (final ResourceRef ref : entry.entityReferenceType)
                addEdge(graph, "OF_TYPE", id, ref.resource);
        if (entry.memberEntityReference != null)
            for (final ResourceRef ref : entry.memberEntityReference)
                addEdge(graph, "HAS_MEMBER", id, ref.resource);
        if (entry.entityFeature != null)
            for (final ResourceRef ref : entry.entityFeature)
                addEdge(graph, "HAS_FEATURE", id, ref.resource);
    }

    private void addRelations(final Graph graph, final Long id, final PhysicalEntity entry) {
        addRelations(graph, id, (Entity) entry);
        if (entry.feature != null)
            for (final ResourceRef ref : entry.feature)
                addEdge(graph, "HAS_FEATURE", id, ref.resource);
        if (entry.notFeature != null)
            for (final ResourceRef ref : entry.notFeature)
                addEdge(graph, "HAS_NOT_FEATURE", id, ref.resource);
        if (entry.cellularLocation != null)
            addEdge(graph, "LOCALIZED_IN", id, entry.cellularLocation.resource);
        if (entry.memberPhysicalEntity != null)
            for (final ResourceRef ref : entry.memberPhysicalEntity)
                addEdge(graph, "HAS_MEMBER", id, ref.resource);
    }

    private void addRelations(final Graph graph, final Long id, final Conversion entry) {
        addRelations(graph, id, (Interaction) entry);
        if (entry.left != null)
            for (final ResourceRef ref : entry.left)
                addEdge(graph, "HAS_LEFT", id, ref.resource);
        if (entry.right != null)
            for (final ResourceRef ref : entry.right)
                addEdge(graph, "HAS_RIGHT", id, ref.resource);
        if (entry.participantStoichiometry != null)
            for (final ResourceRef ref : entry.participantStoichiometry)
                addEdge(graph, "HAS_PARTICIPANT_STOICHIOMETRY", id, ref.resource);
    }

    private void addRelations(final Graph graph, final Long id, final Control entry) {
        addRelations(graph, id, (Interaction) entry);
        if (entry.controller != null)
            addEdge(graph, "HAS_CONTROLLER", id, entry.controller.resource);
        if (entry.controlled != null)
            addEdge(graph, "HAS_CONTROLLED", id, entry.controlled.resource);
    }

    private void addRelations(final Graph graph, final Long id, final Interaction entry) {
        addRelations(graph, id, (Entity) entry);
        if (entry.interactionType != null)
            addEdge(graph, "OF_TYPE", id, entry.interactionType.resource);
        if (entry.participant != null)
            for (final ResourceRef ref : entry.participant)
                addEdge(graph, "HAS_PARTICIPANT", id, ref.resource);
    }

    private void addRelations(final Graph graph, final Long id, final Entity entry) {
        if (entry.evidence != null)
            addEdge(graph, "HAS_EVIDENCE", id, entry.evidence.resource);
        if (entry.xref != null)
            for (final ResourceRef ref : entry.xref)
                addEdge(graph, "HAS_XREF", id, ref.resource);
        if (entry.dataSource != null)
            addEdge(graph, "HAS_SOURCE", id, entry.dataSource.resource);
    }

    private void addRelations(final Graph graph, final Long id, final EntityFeature entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                addEdge(graph, "HAS_EVIDENCE", id, ref.resource);
        if (entry.memberFeature != null)
            for (final ResourceRef ref : entry.memberFeature)
                addEdge(graph, "HAS_MEMBER_FEATURE", id, ref.resource);
        // TODO: featureLocation, featureLocationType
    }

    private void addRelations(final Graph graph, final Long id, final PathwayStep entry) {
        if (entry.evidence != null)
            for (final ResourceRef ref : entry.evidence)
                addEdge(graph, "HAS_EVIDENCE", id, ref.resource);
        if (entry.nextStep != null)
            for (final var ref : entry.nextStep)
                addEdge(graph, "HAS_NEXT_STEP", id, ref.resource);
        if (entry.stepProcess != null)
            for (final var ref : entry.stepProcess)
                addEdge(graph, "HAS_PROCESS", id, ref.resource);
    }
}
