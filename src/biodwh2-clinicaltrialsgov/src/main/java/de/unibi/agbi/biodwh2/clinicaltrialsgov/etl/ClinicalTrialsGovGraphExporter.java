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
        builder.withProperty("phase", study.phase.value);
        builder.withProperty("study_type", study.studyType.value);
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
        builder.build();
        /* TODO:
        RequiredHeaderStruct requiredHeader
        IdInfoStruct idInfo
        SponsorsStruct sponsors
        OversightInfoStruct oversightInfo
        VariableDateStruct startDate
        VariableDateStruct completionDate
        VariableDateStruct primaryCompletionDate
        YesNoEnum hasExpandedAccess
        ExpandedAccessInfoStruct expandedAccessInfo
        StudyDesignInfoStruct studyDesignInfo
        ArrayList<ProtocolOutcomeStruct> primaryOutcome
        ArrayList<ProtocolOutcomeStruct> secondaryOutcome
        ArrayList<ProtocolOutcomeStruct> otherOutcome
        BigInteger numberOfArms
        BigInteger numberOfGroups
        EnrollmentStruct enrollment
        ArrayList<String> condition
        ArrayList<ArmGroupStruct> armGroup
        ArrayList<InterventionStruct> intervention
        BiospecRetentionEnum biospecRetention
        TextblockStruct biospecDescr
        EligibilityStruct eligibility
        ArrayList<InvestigatorStruct> overallOfficial
        ContactStruct overallContact
        ContactStruct overallContactBackup
        ArrayList<LocationStruct> location
        CountriesStruct locationCountries
        CountriesStruct removedCountries
        ArrayList<LinkStruct> link
        ArrayList<ReferenceStruct> reference
        ArrayList<ReferenceStruct> resultsReference
        String verificationDate
        String studyFirstSubmitted
        String studyFirstSubmittedQc
        VariableDateStruct studyFirstPosted
        String resultsFirstSubmitted
        String resultsFirstSubmittedQc
        VariableDateStruct resultsFirstPosted
        String dispositionFirstSubmitted
        String dispositionFirstSubmittedQc
        VariableDateStruct dispositionFirstPosted
        String lastUpdateSubmitted
        String lastUpdateSubmittedQc
        VariableDateStruct lastUpdatePosted
        ResponsiblePartyStruct responsibleParty
        BrowseStruct conditionBrowse
        BrowseStruct interventionBrowse
        PatientDataStruct patientData
        StudyDocsStruct studyDocs
        ProvidedDocumentSectionStruct providedDocumentSection
        PendingResultsStruct pendingResults
        ClinicalResultsStruct clinicalResults
        */
    }
}
