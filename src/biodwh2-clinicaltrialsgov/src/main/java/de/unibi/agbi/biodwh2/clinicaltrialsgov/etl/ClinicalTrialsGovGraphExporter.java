package de.unibi.agbi.biodwh2.clinicaltrialsgov.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.ClinicalTrialsGovDataSource;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClinicalTrialsGovGraphExporter extends GraphExporter<ClinicalTrialsGovDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(ClinicalTrialsGovGraphExporter.class);
    private static final String OUTCOME_LABEL = "Outcome";
    private static final String DOCUMENT_LABEL = "Document";
    private static final String ARM_GROUP_LABEL = "ArmGroup";
    private static final String PERSON_LABEL = "Person";
    private static final String SPONSOR_LABEL = "Sponsor";
    private static final String LOCATION_LABEL = "Location";
    static final String REFERENCE_LABEL = "Reference";
    static final String TRIAL_LABEL = "Trial";
    private static final String CONDITION_LABEL = "Condition";
    private static final String INTERVENTION_LABEL = "Intervention";

    private final Map<String, Long> nonPmidCitationNodeIdMap = new HashMap<>();
    private final Map<String, Long> sponsorNameNodeIdMap = new HashMap<>();

    public ClinicalTrialsGovGraphExporter(final ClinicalTrialsGovDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(TRIAL_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CONDITION_LABEL, "mesh_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(INTERVENTION_LABEL, "mesh_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DOCUMENT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportClinicalTrials(workspace, graph);
        return true;
    }

    private void exportClinicalTrials(final Workspace workspace, final Graph graph) {
        int numberOfRecords = 0;
        try {
            final String[] fileNames = FileUtils.readZipFilePaths(
                    dataSource.resolveSourceFilePath(workspace, ClinicalTrialsGovUpdater.FILE_NAME));
            for (final String fileName : fileNames)
                if (fileName.endsWith(".xml"))
                    numberOfRecords++;
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        int counter = 0;
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        try (ZipInputStream zipStream = FileUtils.openZip(workspace, dataSource, ClinicalTrialsGovUpdater.FILE_NAME)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xml")) {
                    exportClinicalTrial(graph, zipStream, xmlMapper);
                    counter++;
                    if (counter % 10000 == 0 && LOGGER.isInfoEnabled())
                        LOGGER.info("Exporting trial progress " + counter + "/" + numberOfRecords);
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportClinicalTrial(final Graph graph, final InputStream stream,
                                     final XmlMapper xmlMapper) throws IOException {
        final ClinicalStudy study = xmlMapper.readValue(stream, ClinicalStudy.class);
        final NodeBuilder builder = graph.buildNode().withLabel(TRIAL_LABEL).withProperty(ID_KEY, study.idInfo.nctId);
        builder.withPropertyIfNotNull("org_study_id", study.idInfo.orgStudyId);
        withArrayIfNotNull(builder, study.idInfo.secondaryId, "secondary_ids");
        withArrayIfNotNull(builder, study.idInfo.nctAlias, "nct_aliases");
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
        withArrayIfNotNull(builder, study.keyword, "keywords");
        withTextBlockIfNotNull(builder, study.briefSummary, "brief_summary");
        withTextBlockIfNotNull(builder, study.detailedDescription, "detailed_description");
        builder.withPropertyIfNotNull("overall_status", study.overallStatus);
        builder.withPropertyIfNotNull("last_known_status", study.lastKnownStatus);
        builder.withPropertyIfNotNull("target_duration", study.targetDuration);
        withVariableDateIfNotNull(builder, study.startDate, "start_date");
        withVariableDateIfNotNull(builder, study.completionDate, "completion_date");
        withVariableDateIfNotNull(builder, study.primaryCompletionDate, "primary_completion_date");
        withYesNoEnumIfNotNull(builder, study.hasExpandedAccess, "has_expanded_access");
        builder.withPropertyIfNotNull("verification_date", study.verificationDate);
        builder.withPropertyIfNotNull("study_first_submitted", study.studyFirstSubmitted);
        builder.withPropertyIfNotNull("study_first_submitted_qc", study.studyFirstSubmittedQc);
        withVariableDateIfNotNull(builder, study.studyFirstPosted, "study_first_posted");
        builder.withPropertyIfNotNull("results_first_submitted", study.resultsFirstSubmitted);
        builder.withPropertyIfNotNull("results_first_submitted_qc", study.resultsFirstSubmittedQc);
        withVariableDateIfNotNull(builder, study.resultsFirstPosted, "results_first_posted");
        withVariableDateIfNotNull(builder, study.dispositionFirstPosted, "disposition_first_posted");
        builder.withPropertyIfNotNull("disposition_first_submitted", study.dispositionFirstSubmitted);
        builder.withPropertyIfNotNull("disposition_first_submitted_qc", study.dispositionFirstSubmittedQc);
        builder.withPropertyIfNotNull("last_update_submitted", study.lastUpdateSubmitted);
        builder.withPropertyIfNotNull("last_update_submitted_qc", study.lastUpdateSubmittedQc);
        withVariableDateIfNotNull(builder, study.lastUpdatePosted, "last_update_posted");
        builder.withPropertyIfNotNull("number_of_arms", study.numberOfArms);
        builder.withPropertyIfNotNull("number_of_groups", study.numberOfGroups);
        if (study.enrollment != null) {
            builder.withPropertyIfNotNull("enrollment", study.enrollment.value);
            builder.withPropertyIfNotNull("enrollment_type",
                                          study.enrollment.type != null ? study.enrollment.type.value : null);
        }
        if (study.locationCountries != null)
            withArrayIfNotNull(builder, study.locationCountries.country, "location_countries");
        if (study.patientData != null) {
            builder.withPropertyIfNotNull("sharing_ipd", study.patientData.sharingIpd);
            builder.withPropertyIfNotNull("ipd_description", study.patientData.ipdDescription);
            withArrayIfNotNull(builder, study.patientData.ipdInfoType, "ipd_info_type");
            builder.withPropertyIfNotNull("ipd_time_frame", study.patientData.ipdTimeFrame);
            builder.withPropertyIfNotNull("ipd_access_criteria", study.patientData.ipdAccessCriteria);
            builder.withPropertyIfNotNull("ipd_url", study.patientData.ipdUrl);
        }
        if (study.oversightInfo != null) {
            withYesNoEnumIfNotNull(builder, study.oversightInfo.hasDmc, "has_dmc");
            withYesNoEnumIfNotNull(builder, study.oversightInfo.isFdaRegulatedDrug, "is_fda_regulated_drug");
            withYesNoEnumIfNotNull(builder, study.oversightInfo.isFdaRegulatedDevice, "is_fda_regulated_device");
            withYesNoEnumIfNotNull(builder, study.oversightInfo.isUnapprovedDevice, "is_unapproved_device");
            withYesNoEnumIfNotNull(builder, study.oversightInfo.isPpsd, "is_ppsd");
            withYesNoEnumIfNotNull(builder, study.oversightInfo.isUsExport, "is_us_export");
        }
        if (study.expandedAccessInfo != null) {
            withYesNoEnumIfNotNull(builder, study.expandedAccessInfo.expandedAccessTypeIndividual,
                                   "expanded_access_type_individual");
            withYesNoEnumIfNotNull(builder, study.expandedAccessInfo.expandedAccessTypeIntermediate,
                                   "expanded_access_type_intermediate");
            withYesNoEnumIfNotNull(builder, study.expandedAccessInfo.expandedAccessTypeTreatment,
                                   "expanded_access_type_treatment");
        }
        if (study.studyDesignInfo != null) {
            builder.withPropertyIfNotNull("allocation", study.studyDesignInfo.allocation);
            builder.withPropertyIfNotNull("intervention_model", study.studyDesignInfo.interventionModel);
            builder.withPropertyIfNotNull("intervention_model_description",
                                          study.studyDesignInfo.interventionModelDescription);
            builder.withPropertyIfNotNull("primary_purpose", study.studyDesignInfo.primaryPurpose);
            builder.withPropertyIfNotNull("observational_model", study.studyDesignInfo.observationalModel);
            builder.withPropertyIfNotNull("time_perspective", study.studyDesignInfo.timePerspective);
            builder.withPropertyIfNotNull("masking", study.studyDesignInfo.masking);
            builder.withPropertyIfNotNull("masking_description", study.studyDesignInfo.maskingDescription);
        }
        if (study.eligibility != null) {
            builder.withPropertyIfNotNull("eligibility_gender_description", study.eligibility.genderDescription);
            builder.withPropertyIfNotNull("eligibility_minimum_age", study.eligibility.minimumAge);
            builder.withPropertyIfNotNull("eligibility_maximum_age", study.eligibility.maximumAge);
            builder.withPropertyIfNotNull("eligibility_healthy_volunteers", study.eligibility.healthyVolunteers);
            withYesNoEnumIfNotNull(builder, study.eligibility.genderBased, "eligibility_gender_based");
            withTextBlockIfNotNull(builder, study.eligibility.studyPop, "eligibility_study_population");
            withTextBlockIfNotNull(builder, study.eligibility.criteria, "eligibility_criteria");
            if (study.eligibility.gender != null)
                builder.withPropertyIfNotNull("eligibility_gender", study.eligibility.gender.value);
            if (study.eligibility.samplingMethod != null)
                builder.withPropertyIfNotNull("eligibility_sampling_method", study.eligibility.samplingMethod.value);
        }
        if (study.pendingResults != null) {
            withVariableDateIfNotNull(builder, study.pendingResults.returned, "pending_results_returned");
            withVariableDateIfNotNull(builder, study.pendingResults.submitted, "pending_results_submitted");
            withVariableDateIfNotNull(builder, study.pendingResults.submissionCanceled,
                                      "pending_results_submission_canceled");
        }
        withTextBlockIfNotNull(builder, study.biospecDescr, "biospecimen_description");
        if (study.biospecRetention != null)
            builder.withPropertyIfNotNull("biospecimen_retention", study.biospecRetention.value);
        withStudyLinks(builder, study);
        withArrayIfNotNull(builder, study.condition, "conditions");
        if (study.conditionBrowse != null)
            withArrayIfNotNull(builder, study.conditionBrowse.meshTerm, "conditions_mesh");
        if (study.interventionBrowse != null)
            withArrayIfNotNull(builder, study.interventionBrowse.meshTerm, "interventions_mesh");
        // TODO: ResponsiblePartyStruct responsibleParty, ClinicalResultsStruct clinicalResults
        final Node node = builder.build();
        exportStudyReferences(graph, study, node);
        exportStudySponsors(graph, study, node);
        exportStudyPeople(graph, study, node);
        exportStudyDocuments(graph, study, node);
        exportStudyOutcomes(graph, node, study.primaryOutcome, "primary");
        exportStudyOutcomes(graph, node, study.secondaryOutcome, "secondary");
        exportStudyOutcomes(graph, node, study.otherOutcome, "other");
        exportStudyArmGroups(graph, study, node);
        exportStudyInterventions(graph, study, node);
        exportStudyClinicalResults(graph, study, node);
    }

    private void withArrayIfNotNull(final NodeBuilder builder, final List<String> value, final String key) {
        if (value != null && !value.isEmpty())
            builder.withPropertyIfNotNull(key, value.toArray(new String[0]));
    }

    private void withVariableDateIfNotNull(final NodeBuilder builder, final VariableDateStruct value,
                                           final String key) {
        if (value != null) {
            builder.withPropertyIfNotNull(key, value.value);
            builder.withPropertyIfNotNull(key + "_type", value.type != null ? value.type.value : null);
        }
    }

    private void withYesNoEnumIfNotNull(final NodeBuilder builder, final YesNoEnum value, final String key) {
        if (value != null)
            builder.withPropertyIfNotNull(key, value == YesNoEnum.YES);
    }

    private void withTextBlockIfNotNull(final NodeBuilder builder, final TextblockStruct value, final String key) {
        if (value != null && value.textblock != null)
            builder.withPropertyIfNotNull(key, value.textblock);
    }

    private void withStudyLinks(final NodeBuilder builder, final ClinicalStudy study) {
        if (study.link != null && !study.link.isEmpty()) {
            final String[] links = new String[study.link.size()];
            for (int i = 0; i < study.link.size(); i++) {
                LinkStruct link = study.link.get(i);
                links[i] = link.url + "|" + (link.description != null ? link.description : "");
            }
            builder.withProperty("links", links);
        }
    }

    private void exportStudyReferences(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.reference != null) {
            for (final ReferenceStruct reference : study.reference) {
                final Node referenceNode = getOrCreateReference(graph, reference);
                if (referenceNode != null)
                    graph.addEdge(studyNode, referenceNode, "REFERENCES");
            }
        }
        if (study.resultsReference != null) {
            for (final ReferenceStruct reference : study.resultsReference) {
                final Node referenceNode = getOrCreateReference(graph, reference);
                if (referenceNode != null)
                    graph.addEdge(studyNode, referenceNode, "RESULT_REFERENCES");
            }
        }
    }

    private Node getOrCreateReference(final Graph graph, final ReferenceStruct reference) {
        if (reference.citation.contains("has not been published in"))
            return null;
        Node node = graph.findNode(REFERENCE_LABEL, "pmid", reference.pmid);
        if (node == null) {
            if (reference.pmid != null)
                node = graph.addNode(REFERENCE_LABEL, "pmid", reference.pmid, "citation", reference.citation);
            else {
                final Long nonPmidNodeId = nonPmidCitationNodeIdMap.get(reference.citation);
                if (nonPmidNodeId != null) {
                    node = graph.getNode(nonPmidNodeId);
                } else {
                    node = graph.addNode(REFERENCE_LABEL, "citation", reference.citation);
                    nonPmidCitationNodeIdMap.put(reference.citation, node.getId());
                }
            }
        }
        return node;
    }

    private void exportStudySponsors(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.sponsors != null) {
            if (study.sponsors.leadSponsor != null) {
                final Node sponsorNode = getOrCreateSponsor(graph, study.sponsors.leadSponsor);
                graph.addEdge(sponsorNode, studyNode, "SPONSORS", "is_lead", true);
            }
            if (study.sponsors.collaborator != null) {
                for (final SponsorStruct sponsor : study.sponsors.collaborator) {
                    if (study.sponsors.leadSponsor == null || !study.sponsors.leadSponsor.agency.equals(
                            sponsor.agency)) {
                        final Node sponsorNode = getOrCreateSponsor(graph, sponsor);
                        graph.addEdge(sponsorNode, studyNode, "SPONSORS");
                    }
                }
            }
        }
    }

    private Node getOrCreateSponsor(final Graph graph, final SponsorStruct sponsor) {
        Node node;
        Long nodeId = sponsorNameNodeIdMap.get(sponsor.agency);
        if (nodeId != null) {
            node = graph.getNode(nodeId);
        } else {
            if (sponsor.agencyClass != null)
                node = graph.addNode(SPONSOR_LABEL, "name", sponsor.agency, "agency_type", sponsor.agencyClass.value);
            else
                node = graph.addNode(SPONSOR_LABEL, "name", sponsor.agency);
            sponsorNameNodeIdMap.put(sponsor.agency, node.getId());
        }
        return node;
    }

    private void exportStudyPeople(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        final Map<String, Long> contactKeyNodeIdMap = new HashMap<>();
        if (study.overallContact != null) {
            final Node personNode = createPerson(graph, study.overallContact);
            final String personKey = study.overallContact.firstName + '|' + study.overallContact.middleName + '|' +
                                     study.overallContact.lastName + '|' + study.overallContact.degrees;
            contactKeyNodeIdMap.put(personKey, personNode.getId());
            graph.addEdge(studyNode, personNode, "HAS_CONTACT");
        }
        if (study.overallContactBackup != null) {
            final Node personNode = createPerson(graph, study.overallContactBackup);
            final String personKey = study.overallContact.firstName + '|' + study.overallContact.middleName + '|' +
                                     study.overallContact.lastName + '|' + study.overallContact.degrees;
            contactKeyNodeIdMap.put(personKey, personNode.getId());
            graph.addEdge(studyNode, personNode, "HAS_CONTACT", "is_backup", true);
        }
        final Map<String, Long> investigatorKeyNodeIdMap = new HashMap<>();
        if (study.overallOfficial != null) {
            for (final InvestigatorStruct investigator : study.overallOfficial) {
                final Node personNode = createPerson(graph, investigator);
                final String personKey =
                        investigator.firstName + '|' + investigator.middleName + '|' + investigator.lastName + '|' +
                        investigator.degrees + '|' + investigator.affiliation;
                investigatorKeyNodeIdMap.put(personKey, personNode.getId());
                if (investigator.role != null)
                    graph.addEdge(studyNode, personNode, "HAS_INVESTIGATOR", "role", investigator.role.value);
                else
                    graph.addEdge(studyNode, personNode, "HAS_INVESTIGATOR");
            }
        }
        if (study.location != null) {
            for (final LocationStruct location : study.location) {
                final NodeBuilder builder = graph.buildNode().withLabel(LOCATION_LABEL);
                builder.withPropertyIfNotNull("status", location.status);
                if (location.facility != null) {
                    builder.withPropertyIfNotNull("facility_name", location.facility.name);
                    if (location.facility.address != null) {
                        builder.withPropertyIfNotNull("facility_address_city", location.facility.address.city);
                        builder.withPropertyIfNotNull("facility_address_country", location.facility.address.country);
                        builder.withPropertyIfNotNull("facility_address_zip", location.facility.address.zip);
                        builder.withPropertyIfNotNull("facility_address_state", location.facility.address.state);
                    }
                }
                final Node node = builder.build();
                graph.addEdge(studyNode, node, "HAS_LOCATION");
                if (location.contact != null) {
                    final String personKey = location.contact.firstName + '|' + location.contact.middleName + '|' +
                                             location.contact.lastName + '|' + location.contact.degrees;
                    Long contactNodeId = contactKeyNodeIdMap.get(personKey);
                    if (contactNodeId == null) {
                        final Node personNode = createPerson(graph, location.contact);
                        contactKeyNodeIdMap.put(personKey, personNode.getId());
                        contactNodeId = personNode.getId();
                    }
                    graph.addEdge(contactNodeId, node, "LOCATED_AT");
                }
                if (location.contactBackup != null) {
                    final String personKey =
                            location.contactBackup.firstName + '|' + location.contactBackup.middleName + '|' +
                            location.contactBackup.lastName + '|' + location.contactBackup.degrees;
                    Long contactNodeId = contactKeyNodeIdMap.get(personKey);
                    if (contactNodeId == null) {
                        final Node personNode = createPerson(graph, location.contactBackup);
                        contactKeyNodeIdMap.put(personKey, personNode.getId());
                        contactNodeId = personNode.getId();
                    }
                    graph.addEdge(contactNodeId, node, "LOCATED_AT");
                }
                if (location.investigator != null) {
                    for (final InvestigatorStruct investigator : location.investigator) {
                        final String personKey =
                                investigator.firstName + '|' + investigator.middleName + '|' + investigator.lastName +
                                '|' + investigator.degrees + '|' + investigator.affiliation;
                        Long investigatorNodeId = investigatorKeyNodeIdMap.get(personKey);
                        if (investigatorNodeId == null) {
                            final Node personNode = createPerson(graph, investigator);
                            investigatorKeyNodeIdMap.put(personKey, personNode.getId());
                            investigatorNodeId = personNode.getId();
                        }
                        graph.addEdge(investigatorNodeId, node, "LOCATED_AT");
                    }
                }
            }
        }
    }

    private Node createPerson(final Graph graph, final PersonStruct person) {
        final NodeBuilder builder = graph.buildNode().withLabel(PERSON_LABEL);
        builder.withPropertyIfNotNull("first_name", person.firstName);
        builder.withPropertyIfNotNull("middle_name", person.middleName);
        builder.withPropertyIfNotNull("last_name", person.lastName);
        builder.withPropertyIfNotNull("degrees", person.degrees);
        // Ignore phone and email in ContactStruct for now
        if (person instanceof InvestigatorStruct)
            builder.withPropertyIfNotNull("affiliation", ((InvestigatorStruct) person).affiliation);
        return builder.build();
    }

    private void exportStudyDocuments(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.studyDocs != null && study.studyDocs.studyDoc != null) {
            for (final StudyDocStruct document : study.studyDocs.studyDoc) {
                final Node documentNode = getOrCreateDocument(graph, document);
                graph.addEdge(studyNode, documentNode, "HAS_DOCUMENT");
            }
        }
        if (study.providedDocumentSection != null && study.providedDocumentSection.providedDocument != null) {
            for (final ProvidedDocumentStruct document : study.providedDocumentSection.providedDocument) {
                final NodeBuilder builder = graph.buildNode().withLabel("Document");
                builder.withPropertyIfNotNull("date", document.documentDate);
                builder.withPropertyIfNotNull("url", document.documentUrl);
                builder.withPropertyIfNotNull("type", document.documentType);
                builder.withPropertyIfNotNull("has_protocol", document.documentHasProtocol);
                builder.withPropertyIfNotNull("has_icf", document.documentHasIcf);
                builder.withPropertyIfNotNull("has_sap", document.documentHasSap);
                final Node node = builder.build();
                graph.addEdge(studyNode, node, "HAS_PROVIDED_DOCUMENT");
            }
        }
    }

    private Node getOrCreateDocument(final Graph graph, final StudyDocStruct document) {
        Node node = graph.findNode(DOCUMENT_LABEL, ID_KEY, document.docId);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(DOCUMENT_LABEL).withProperty(ID_KEY,
                                                                                                 document.docId);
            builder.withPropertyIfNotNull("url", document.docUrl);
            builder.withPropertyIfNotNull("type", document.docType);
            builder.withPropertyIfNotNull("comment", document.docComment);
            node = builder.build();
        }
        return node;
    }

    private void exportStudyOutcomes(final Graph graph, final Node studyNode,
                                     final List<ProtocolOutcomeStruct> outcomes, final String type) {
        if (outcomes != null)
            for (final ProtocolOutcomeStruct outcome : outcomes)
                graph.addEdge(studyNode, createOutcomeNode(graph, outcome), "HAS_OUTCOME", "type", type);
    }

    private Node createOutcomeNode(final Graph graph, final ProtocolOutcomeStruct outcome) {
        return graph.addNode(OUTCOME_LABEL, "measure", outcome.measure, "time_frame", outcome.timeFrame, "description",
                             outcome.description);
    }

    private void exportStudyArmGroups(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.armGroup != null) {
            for (final ArmGroupStruct group : study.armGroup) {
                final Node node = graph.addNode(ARM_GROUP_LABEL, "label", group.armGroupLabel, "type",
                                                group.armGroupType, "description", group.description);
                graph.addEdge(studyNode, node, "HAS_ARM_GROUP");
            }
        }
    }

    private void exportStudyInterventions(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.intervention != null)
            for (final InterventionStruct intervention : study.intervention)
                exportStudyIntervention(graph, studyNode, intervention);
    }

    private void exportStudyIntervention(final Graph graph, final Node studyNode,
                                         final InterventionStruct intervention) {
        final NodeBuilder builder = graph.buildNode().withLabel(INTERVENTION_LABEL);
        builder.withPropertyIfNotNull("type", intervention.interventionType.value);
        builder.withPropertyIfNotNull("name", intervention.interventionName);
        builder.withPropertyIfNotNull("description", intervention.description);
        withArrayIfNotNull(builder, intervention.armGroupLabel, "arm_group_label");
        withArrayIfNotNull(builder, intervention.otherName, "other_name");
        final Node interventionNode = builder.build();
        graph.addEdge(studyNode, interventionNode, "HAS_INTERVENTION");
    }

    private void exportStudyClinicalResults(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.clinicalResults == null)
            return;
        final ClinicalResultsStruct clinicalResults = study.clinicalResults;
        final NodeBuilder clinicalResultsBuilder = graph.buildNode().withLabel("ClinicalResults");
        clinicalResultsBuilder.withPropertyIfNotNull("limitations_and_caveats", clinicalResults.limitationsAndCaveats);
        if (clinicalResults.pointOfContact != null) {
            clinicalResultsBuilder.withPropertyIfNotNull("point_of_contact_name_or_title",
                                                         clinicalResults.pointOfContact.nameOrTitle);
            clinicalResultsBuilder.withPropertyIfNotNull("point_of_contact_organization",
                                                         clinicalResults.pointOfContact.organization);
            // Omitted phone and email for privacy reasons
        }
        if (clinicalResults.certainAgreements != null) {
            if (clinicalResults.certainAgreements.piEmployee != null)
                clinicalResultsBuilder.withPropertyIfNotNull("certain_agreements_pi_employee",
                                                             clinicalResults.certainAgreements.piEmployee.value);
            clinicalResultsBuilder.withPropertyIfNotNull("certain_agreements_restrictive_agreement",
                                                         clinicalResults.certainAgreements.restrictiveAgreement);
        }
        final Node clinicalResultsNode = clinicalResultsBuilder.build();
        graph.addEdge(studyNode, clinicalResultsNode, "HAS_CLINICAL_RESULTS");
        final BaselineStruct baseline = clinicalResults.baseline;
        final ParticipantFlowStruct participantFlow = clinicalResults.participantFlow;
        final Map<String, Long> groupIdNodeIdMap = new HashMap<>();
        final List<GroupStruct> groups = new ArrayList<>();
        if (baseline != null && baseline.groupList != null && baseline.groupList.group != null)
            groups.addAll(baseline.groupList.group);
        if (participantFlow != null && participantFlow.groupList != null && participantFlow.groupList.group != null) {
            groups.addAll(participantFlow.groupList.group);
        }
        if (clinicalResults.reportedEvents != null && clinicalResults.reportedEvents.groupList != null &&
            clinicalResults.reportedEvents.groupList.group != null) {
            groups.addAll(clinicalResults.reportedEvents.groupList.group);
        }
        if (clinicalResults.outcomeList != null && clinicalResults.outcomeList.outcome != null) {
            for (final ResultsOutcomeStruct outcome : clinicalResults.outcomeList.outcome) {
                if (outcome.groupList != null && outcome.groupList.group != null)
                    groups.addAll(outcome.groupList.group);
            }
        }
        for (final GroupStruct group : groups) {
            final Node groupNode = graph.addNode("Group", ID_KEY, group.groupId, "title", group.title, "description",
                                                 group.description);
            groupIdNodeIdMap.put(group.groupId, groupNode.getId());
        }
        if (participantFlow != null) {
            final Node participantFlowNode = graph.addNode("ParticipantFlow", "recruitment_details",
                                                           participantFlow.recruitmentDetails, "pre_assignment_details",
                                                           participantFlow.preAssignmentDetails);
            graph.addEdge(clinicalResultsNode, participantFlowNode, "HAS_PARTICIPANT_FLOW");
            if (participantFlow.periodList != null && participantFlow.periodList.period != null) {
                for (final PeriodStruct period : participantFlow.periodList.period) {
                    final Node periodNode = graph.addNode("Period", "title", period.title);
                    graph.addEdge(participantFlowNode, periodNode, "HAS_PERIOD");
                    if (period.milestoneList != null && period.milestoneList.milestone != null) {
                        for (final MilestoneStruct milestone : period.milestoneList.milestone) {
                            final Node milestoneNode = graph.addNode("Milestone", "title", milestone.title);
                            graph.addEdge(periodNode, milestoneNode, "HAS_MILESTONE");
                            if (milestone.participantsList != null && milestone.participantsList.participants != null) {
                                for (final ParticipantsStruct participants : milestone.participantsList.participants) {
                                    graph.addEdge(milestoneNode, groupIdNodeIdMap.get(participants.groupId),
                                                  "HAS_PARTICIPANTS", "value", participants.value, "count",
                                                  participants.count);
                                }
                            }
                        }
                    }
                    if (period.dropWithdrawReasonList != null &&
                        period.dropWithdrawReasonList.dropWithdrawReason != null) {
                        for (final MilestoneStruct milestone : period.dropWithdrawReasonList.dropWithdrawReason) {
                            final Node milestoneNode = graph.addNode("Milestone", "title", milestone.title);
                            graph.addEdge(periodNode, milestoneNode, "HAS_DROP_WITHDRAW_REASON");
                            if (milestone.participantsList != null && milestone.participantsList.participants != null) {
                                for (final ParticipantsStruct participants : milestone.participantsList.participants) {
                                    graph.addEdge(milestoneNode, groupIdNodeIdMap.get(participants.groupId),
                                                  "HAS_PARTICIPANTS", "value", participants.value, "count",
                                                  participants.count);
                                }
                            }
                        }
                    }
                }
            }
        }
        // TODO: baseline, outcomeList, reportedEvents
    }
}
