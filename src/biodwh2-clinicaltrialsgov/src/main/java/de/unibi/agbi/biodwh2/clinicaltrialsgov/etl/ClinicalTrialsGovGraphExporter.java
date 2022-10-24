package de.unibi.agbi.biodwh2.clinicaltrialsgov.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.ClinicalTrialsGovDataSource;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.model.ClinicalStudy;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClinicalTrialsGovGraphExporter extends GraphExporter<ClinicalTrialsGovDataSource> {
    private final XmlMapper xmlMapper;

    public ClinicalTrialsGovGraphExporter(final ClinicalTrialsGovDataSource dataSource) {
        super(dataSource);
        xmlMapper = new XmlMapper();
        xmlMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Trial", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Condition", "mesh_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Intervention", "mesh_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Reference", "pmid", IndexDescription.Type.UNIQUE));
        exportClinicalTrials(workspace, graph);
        return true;
    }

    private void exportClinicalTrials(final Workspace workspace, final Graph graph) {
        try (ZipInputStream zipStream = FileUtils.openZip(workspace, dataSource, ClinicalTrialsGovUpdater.FILE_NAME)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null)
                if (entry.getName().endsWith(".xml"))
                    exportClinicalTrial(graph, zipStream);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportClinicalTrial(final Graph graph, final InputStream stream) throws IOException {
        final ClinicalStudy study = xmlMapper.readValue(stream, ClinicalStudy.class);
        final NodeBuilder builder = graph.buildNode().withLabel("Trial").withProperty("id", study.idInfo.nctId);
        builder.withPropertyIfNotNull("org_study_id", study.idInfo.orgStudyId);
        if (study.idInfo.secondaryId != null && study.idInfo.secondaryId.size() > 0)
            builder.withPropertyIfNotNull("secondary_ids", study.idInfo.secondaryId.toArray(new String[0]));
        if (study.idInfo.nctAlias != null && study.idInfo.nctAlias.size() > 0)
            builder.withPropertyIfNotNull("nct_aliases", study.idInfo.nctAlias.toArray(new String[0]));
        if (study.requiredHeader != null) {
            builder.withPropertyIfNotNull("download_date", study.requiredHeader.downloadDate);
            builder.withPropertyIfNotNull("link_text", study.requiredHeader.linkText);
            builder.withPropertyIfNotNull("url", study.requiredHeader.url);
        }
        builder.withPropertyIfNotNull("phase", study.phase != null ? study.phase.value : null);
        builder.withPropertyIfNotNull("study_type", study.studyType != null ? study.studyType.value : null);
        builder.withPropertyIfNotNull("brief_title", study.briefTitle);
        builder.withPropertyIfNotNull("acronym", study.acronym);
        builder.withPropertyIfNotNull("official_title", study.officialTitle);
        builder.withPropertyIfNotNull("why_stopped", study.whyStopped);
        builder.withPropertyIfNotNull("source", study.source);
        if (study.keyword != null && study.keyword.size() > 0)
            builder.withProperty("keywords", study.keyword.toArray(new String[0]));
        builder.withPropertyIfNotNull("brief_summary",
                                      study.briefSummary != null ? study.briefSummary.textblock : null);
        builder.withPropertyIfNotNull("detailed_description",
                                      study.detailedDescription != null ? study.detailedDescription.textblock : null);
        builder.withPropertyIfNotNull("overall_status", study.overallStatus);
        builder.withPropertyIfNotNull("last_known_status", study.lastKnownStatus);
        builder.withPropertyIfNotNull("target_duration", study.targetDuration);
        if (study.startDate != null) {
            builder.withPropertyIfNotNull("start_date", study.startDate.value);
            builder.withPropertyIfNotNull("start_date_type",
                                          study.startDate.type != null ? study.startDate.type.value : null);
        }
        if (study.completionDate != null) {
            builder.withPropertyIfNotNull("completion_date", study.completionDate.value);
            builder.withPropertyIfNotNull("completion_date_type",
                                          study.completionDate.type != null ? study.completionDate.type.value : null);
        }
        if (study.primaryCompletionDate != null) {
            builder.withPropertyIfNotNull("primary_completion_date", study.primaryCompletionDate.value);
            builder.withPropertyIfNotNull("primary_completion_date_type", study.primaryCompletionDate.type != null ?
                                                                          study.primaryCompletionDate.type.value :
                                                                          null);
        }
        if (study.hasExpandedAccess != null)
            builder.withPropertyIfNotNull("has_expanded_access", study.hasExpandedAccess.value);
        builder.withPropertyIfNotNull("verification_date", study.verificationDate);
        builder.withPropertyIfNotNull("study_first_submitted", study.studyFirstSubmitted);
        builder.withPropertyIfNotNull("study_first_submitted_qc", study.studyFirstSubmittedQc);
        if (study.studyFirstPosted != null) {
            builder.withPropertyIfNotNull("study_first_posted", study.studyFirstPosted.value);
            builder.withPropertyIfNotNull("study_first_posted_type",
                                          study.studyFirstPosted.type != null ? study.studyFirstPosted.type.value :
                                          null);
        }
        builder.withPropertyIfNotNull("results_first_submitted", study.resultsFirstSubmitted);
        builder.withPropertyIfNotNull("results_first_submitted_qc", study.resultsFirstSubmittedQc);
        if (study.resultsFirstPosted != null) {
            builder.withPropertyIfNotNull("results_first_posted", study.resultsFirstPosted.value);
            builder.withPropertyIfNotNull("results_first_posted_type",
                                          study.resultsFirstPosted.type != null ? study.resultsFirstPosted.type.value :
                                          null);
        }
        builder.withPropertyIfNotNull("disposition_first_submitted", study.dispositionFirstSubmitted);
        builder.withPropertyIfNotNull("disposition_first_submitted_qc", study.dispositionFirstSubmittedQc);
        if (study.dispositionFirstPosted != null) {
            builder.withPropertyIfNotNull("disposition_first_posted", study.dispositionFirstPosted.value);
            builder.withPropertyIfNotNull("disposition_first_posted_type", study.dispositionFirstPosted.type != null ?
                                                                           study.dispositionFirstPosted.type.value :
                                                                           null);
        }
        builder.withPropertyIfNotNull("last_update_submitted", study.lastUpdateSubmitted);
        builder.withPropertyIfNotNull("last_update_submitted_qc", study.lastUpdateSubmittedQc);
        if (study.lastUpdatePosted != null) {
            builder.withPropertyIfNotNull("last_update_posted", study.lastUpdatePosted.value);
            builder.withPropertyIfNotNull("last_update_posted_type",
                                          study.lastUpdatePosted.type != null ? study.lastUpdatePosted.type.value :
                                          null);
        }
        if (study.numberOfArms != null)
            builder.withProperty("number_of_arms", study.numberOfArms);
        if (study.numberOfGroups != null)
            builder.withProperty("number_of_groups", study.numberOfGroups);
        if (study.enrollment != null) {
            builder.withPropertyIfNotNull("enrollment", study.enrollment.value);
            builder.withPropertyIfNotNull("enrollment_type",
                                          study.enrollment.type != null ? study.enrollment.type.value : null);
        }
        if (study.locationCountries != null && study.locationCountries.country != null &&
            study.locationCountries.country.size() > 0) {
            builder.withProperty("location_countries", study.locationCountries.country.toArray(new String[0]));
        }
        final Node node = builder.build();
        if (study.reference != null) {
            for (final ReferenceStruct reference : study.reference) {
                final Node referenceNode = getOrCreateReference(graph, reference);
                graph.addEdge(node, referenceNode, "REFERENCES");
            }
        }
        if (study.resultsReference != null) {
            for (final ReferenceStruct reference : study.resultsReference) {
                final Node referenceNode = getOrCreateReference(graph, reference);
                graph.addEdge(node, referenceNode, "RESULT_REFERENCES");
            }
        }
        /* TODO:
        SponsorsStruct sponsors
        OversightInfoStruct oversightInfo
        ExpandedAccessInfoStruct expandedAccessInfo
        StudyDesignInfoStruct studyDesignInfo
        ArrayList<ProtocolOutcomeStruct> primaryOutcome
        ArrayList<ProtocolOutcomeStruct> secondaryOutcome
        ArrayList<ProtocolOutcomeStruct> otherOutcome
        ArrayList<ArmGroupStruct> armGroup
        BiospecRetentionEnum biospecRetention
        TextblockStruct biospecDescr
        EligibilityStruct eligibility
        ArrayList<InvestigatorStruct> overallOfficial
        ContactStruct overallContact
        ContactStruct overallContactBackup
        ArrayList<LocationStruct> location
        ArrayList<LinkStruct> link
        ResponsiblePartyStruct responsibleParty
        PatientDataStruct patientData
        StudyDocsStruct studyDocs
        ProvidedDocumentSectionStruct providedDocumentSection
        PendingResultsStruct pendingResults
        ClinicalResultsStruct clinicalResults
        */
        /* TODO: extra nodes
        ArrayList<String> condition
        BrowseStruct conditionBrowse
        ArrayList<InterventionStruct> intervention
        BrowseStruct interventionBrowse
        */
    }

    private Node getOrCreateReference(final Graph graph, final ReferenceStruct reference) {
        Node node = graph.findNode("Reference", "pmid", reference.pmid);
        if (node == null) {
            if (reference.pmid != null)
                node = graph.addNode("Reference", "pmid", reference.pmid, "citation", reference.citation);
            else
                node = graph.addNode("Reference", "citation", reference.citation);
        }
        return node;
    }
}
