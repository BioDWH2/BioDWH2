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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClinicalTrialsGovGraphExporter extends GraphExporter<ClinicalTrialsGovDataSource> {
    private final Map<String, Long> nonPmidCitationNodeIdMap = new HashMap<>();
    private final Map<String, Long> sponsorNameNodeIdMap = new HashMap<>();
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
        graph.addIndex(IndexDescription.forNode("Document", "id", IndexDescription.Type.UNIQUE));
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
            builder.withPropertyIfNotNull("has_expanded_access", study.hasExpandedAccess == YesNoEnum.YES);
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
        if (study.patientData != null) {
            builder.withPropertyIfNotNull("sharing_ipd", study.patientData.sharingIpd);
            builder.withPropertyIfNotNull("ipd_description", study.patientData.ipdDescription);
            if (study.patientData.ipdInfoType != null && study.patientData.ipdInfoType.size() > 0)
                builder.withPropertyIfNotNull("ipd_info_type", study.patientData.ipdInfoType.toArray(new String[0]));
            builder.withPropertyIfNotNull("ipd_time_frame", study.patientData.ipdTimeFrame);
            builder.withPropertyIfNotNull("ipd_access_criteria", study.patientData.ipdAccessCriteria);
            builder.withPropertyIfNotNull("ipd_url", study.patientData.ipdUrl);
        }
        if (study.oversightInfo != null) {
            if (study.oversightInfo.hasDmc != null)
                builder.withPropertyIfNotNull("has_dmc", study.oversightInfo.hasDmc == YesNoEnum.YES);
            if (study.oversightInfo.isFdaRegulatedDrug != null) {
                builder.withPropertyIfNotNull("is_fda_regulated_drug",
                                              study.oversightInfo.isFdaRegulatedDrug == YesNoEnum.YES);
            }
            if (study.oversightInfo.isFdaRegulatedDevice != null) {
                builder.withPropertyIfNotNull("is_fda_regulated_device",
                                              study.oversightInfo.isFdaRegulatedDevice == YesNoEnum.YES);
            }
            if (study.oversightInfo.isUnapprovedDevice != null) {
                builder.withPropertyIfNotNull("is_unapproved_device",
                                              study.oversightInfo.isUnapprovedDevice == YesNoEnum.YES);
            }
            if (study.oversightInfo.isPpsd != null)
                builder.withPropertyIfNotNull("is_ppsd", study.oversightInfo.isPpsd == YesNoEnum.YES);
            if (study.oversightInfo.isUsExport != null)
                builder.withPropertyIfNotNull("is_us_export", study.oversightInfo.isUsExport == YesNoEnum.YES);
        }
        if (study.expandedAccessInfo != null) {
            if (study.expandedAccessInfo.expandedAccessTypeIndividual != null) {
                builder.withPropertyIfNotNull("expanded_access_type_individual",
                                              study.expandedAccessInfo.expandedAccessTypeIndividual == YesNoEnum.YES);
            }
            if (study.expandedAccessInfo.expandedAccessTypeIntermediate != null) {
                builder.withPropertyIfNotNull("expanded_access_type_intermediate",
                                              study.expandedAccessInfo.expandedAccessTypeIntermediate == YesNoEnum.YES);
            }
            if (study.expandedAccessInfo.expandedAccessTypeTreatment != null) {
                builder.withPropertyIfNotNull("expanded_access_type_treatment",
                                              study.expandedAccessInfo.expandedAccessTypeTreatment == YesNoEnum.YES);
            }
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
            if (study.eligibility.genderBased != null) {
                builder.withPropertyIfNotNull("eligibility_gender_based",
                                              study.eligibility.genderBased == YesNoEnum.YES);
            }
            if (study.eligibility.studyPop != null && study.eligibility.studyPop.textblock != null)
                builder.withPropertyIfNotNull("eligibility_study_population", study.eligibility.studyPop.textblock);
            if (study.eligibility.criteria != null && study.eligibility.criteria.textblock != null)
                builder.withPropertyIfNotNull("eligibility_criteria", study.eligibility.criteria.textblock);
            if (study.eligibility.gender != null)
                builder.withPropertyIfNotNull("eligibility_gender", study.eligibility.gender.value);
            if (study.eligibility.samplingMethod != null)
                builder.withPropertyIfNotNull("eligibility_sampling_method", study.eligibility.samplingMethod.value);
        }
        if (study.pendingResults != null) {
            if (study.pendingResults.returned != null) {
                builder.withPropertyIfNotNull("pending_results_returned", study.pendingResults.returned.value);
                builder.withPropertyIfNotNull("pending_results_returned_type",
                                              study.pendingResults.returned.type != null ?
                                              study.pendingResults.returned.type.value : null);
            }
            if (study.pendingResults.submitted != null) {
                builder.withPropertyIfNotNull("pending_results_submitted", study.pendingResults.submitted.value);
                builder.withPropertyIfNotNull("pending_results_submitted_type",
                                              study.pendingResults.submitted.type != null ?
                                              study.pendingResults.submitted.type.value : null);
            }
            if (study.pendingResults.submissionCanceled != null) {
                builder.withPropertyIfNotNull("pending_results_submission_canceled",
                                              study.pendingResults.submissionCanceled.value);
                builder.withPropertyIfNotNull("pending_results_submission_canceled_type",
                                              study.pendingResults.submissionCanceled.type != null ?
                                              study.pendingResults.submissionCanceled.type.value : null);
            }
        }
        if (study.biospecDescr != null)
            builder.withPropertyIfNotNull("biospecimen_description", study.biospecDescr.textblock);
        if (study.biospecRetention != null)
            builder.withPropertyIfNotNull("biospecimen_retention", study.biospecRetention.value);
        // TODO: ResponsiblePartyStruct responsibleParty, ClinicalResultsStruct clinicalResults
        final Node node = builder.build();
        exportStudyReferences(graph, study, node);
        exportStudySponsors(graph, study, node);
        exportStudyPeople(graph, study, node);
        exportStudyDocuments(graph, study, node);
        exportStudyOutcomes(graph, study, node);
        exportStudyArmGroups(graph, study, node);
        exportStudyConditions(graph, study, node);
        exportStudyInterventions(graph, study, node);
        exportStudyLinks(graph, study, node);
        // TODO: ArrayList<LocationStruct> location
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
        Node node = graph.findNode("Reference", "pmid", reference.pmid);
        if (node == null) {
            if (reference.pmid != null)
                node = graph.addNode("Reference", "pmid", reference.pmid, "citation", reference.citation);
            else {
                final Long nonPmidNodeId = nonPmidCitationNodeIdMap.get(reference.citation);
                if (nonPmidNodeId != null) {
                    node = graph.getNode(nonPmidNodeId);
                } else {
                    node = graph.addNode("Reference", "citation", reference.citation);
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
                node = graph.addNode("Sponsor", "name", sponsor.agency, "agency_type", sponsor.agencyClass.value);
            else
                node = graph.addNode("Sponsor", "name", sponsor.agency);
            sponsorNameNodeIdMap.put(sponsor.agency, node.getId());
        }
        return node;
    }

    private void exportStudyPeople(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.overallContact != null) {
            final Node personNode = createPerson(graph, study.overallContact);
            graph.addEdge(studyNode, personNode, "HAS_CONTACT");
        }
        if (study.overallContactBackup != null) {
            final Node personNode = createPerson(graph, study.overallContactBackup);
            graph.addEdge(studyNode, personNode, "HAS_CONTACT", "is_backup", true);
        }
        if (study.overallOfficial != null) {
            for (final InvestigatorStruct investigator : study.overallOfficial) {
                final Node personNode = createPerson(graph, investigator);
                if (investigator.role != null)
                    graph.addEdge(studyNode, personNode, "HAS_INVESTIGATOR", "role", investigator.role.value);
                else
                    graph.addEdge(studyNode, personNode, "HAS_INVESTIGATOR");
            }
        }
    }

    private Node createPerson(final Graph graph, final PersonStruct person) {
        final NodeBuilder builder = graph.buildNode().withLabel("Person");
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
                // TODO
                //final Node documentNode = getOrCreateDocument(graph, document);
                //graph.addEdge(node, documentNode, "HAS_DOCUMENT");
            }
        }
    }

    private Node getOrCreateDocument(final Graph graph, final StudyDocStruct document) {
        Node node = graph.findNode("Document", "id", document.docId);
        if (node == null) {
            graph.addNode("Document", "id", document.docId, "url", document.docUrl, "type", document.docType, "comment",
                          document.docComment);
        }
        return node;
    }

    private void exportStudyOutcomes(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.primaryOutcome != null) {
            for (final ProtocolOutcomeStruct outcome : study.primaryOutcome) {
                // TODO
            }
        }
        if (study.secondaryOutcome != null) {
            for (final ProtocolOutcomeStruct outcome : study.secondaryOutcome) {
                // TODO
            }
        }
        if (study.otherOutcome != null) {
            for (final ProtocolOutcomeStruct outcome : study.otherOutcome) {
                // TODO
            }
        }
    }

    private void exportStudyArmGroups(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.armGroup != null) {
            for (final ArmGroupStruct group : study.armGroup) {
                // TODO
            }
        }
    }

    private void exportStudyConditions(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.condition != null) {
            for (final String condition : study.condition) {
                // TODO
            }
        }
        if (study.conditionBrowse != null && study.conditionBrowse.meshTerm != null) {
            for (final String meshTerm : study.conditionBrowse.meshTerm) {
                // TODO
            }
        }
    }

    private void exportStudyInterventions(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.intervention != null) {
            for (final InterventionStruct intervention : study.intervention) {
                // TODO
            }
        }
        if (study.interventionBrowse != null && study.interventionBrowse.meshTerm != null) {
            for (final String meshTerm : study.interventionBrowse.meshTerm) {
                // TODO
            }
        }
    }

    private void exportStudyLinks(final Graph graph, final ClinicalStudy study, final Node studyNode) {
        if (study.link != null) {
            for (final LinkStruct link : study.link) {
                // TODO
            }
        }
    }
}
