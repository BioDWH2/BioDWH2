package de.unibi.agbi.biodwh2.redotrialsdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.redotrialsdb.ReDOTrialsDBDataSource;
import de.unibi.agbi.biodwh2.redotrialsdb.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;

public final class ReDOTrialsDBGraphExporter extends GraphExporter<ReDOTrialsDBDataSource> {
    static final String DRUG_LABEL = "Drug";
    static final String TRIAL_LABEL = "Trial";
    static final String DISEASE_LABEL = "Disease";
    static final String INVESTIGATES_LABEL = "INVESTIGATES";

    public ReDOTrialsDBGraphExporter(final ReDOTrialsDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "drugbank_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TRIAL_LABEL, "id", IndexDescription.Type.UNIQUE));
        for (final Entry entry : dataSource.entries)
            exportEntry(graph, entry);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(TRIAL_LABEL);
        builder.withProperty("id", entry.nctNumber);
        builder.withProperty("title", entry.title);
        builder.withPropertyIfNotNull("acronym", entry.acronym);
        builder.withProperty("status", entry.status);
        builder.withPropertyIfNotNull("conditions", entry.conditions);
        builder.withPropertyIfNotNull("interventions", entry.interventions);
        builder.withPropertyIfNotNull("outcome_measures", entry.outcomeMeasures);
        builder.withPropertyIfNotNull("sponsors", entry.sponsors); // TODO: list
        builder.withPropertyIfNotNull("gender", cleanGender(entry.gender));
        builder.withPropertyIfNotNull("age", entry.age);
        builder.withPropertyIfNotNull("enrollment", entry.enrollment);
        builder.withPropertyIfNotNull("funders", entry.funders); // TODO: list
        builder.withPropertyIfNotNull("study_type", cleanStudyType(entry.studyType));
        builder.withPropertyIfNotNull("study_designs", entry.studyDesigns);
        builder.withPropertyIfNotNull("other_ids", cleanOtherIds(entry.otherIds));
        builder.withPropertyIfNotNull("start_date", entry.startDate);
        builder.withPropertyIfNotNull("primary_completion_date", entry.primaryCompletionDate);
        builder.withPropertyIfNotNull("completion_date", entry.completionDate);
        builder.withPropertyIfNotNull("last_verified", entry.lastVerified);
        builder.withPropertyIfNotNull("first_submitted", entry.firstSubmitted);
        builder.withPropertyIfNotNull("first_posted", entry.firstPosted);
        builder.withPropertyIfNotNull("results_first_submitted", entry.resultsFirstSubmitted);
        builder.withPropertyIfNotNull("results_first_posted", entry.resultsFirstPosted);
        builder.withPropertyIfNotNull("last_update_submitted", entry.lastUpdateSubmitted);
        builder.withPropertyIfNotNull("last_update_posted", entry.lastUpdatePosted);
        builder.withPropertyIfNotNull("url", entry.url);
        builder.withPropertyIfNotNull("setting", createArray(entry.setting));
        builder.withPropertyIfNotNull("stage", createArray(entry.stage));
        builder.withPropertyIfNotNull("sponsor_type", entry.sponsorType);
        builder.withProperty("controlled", getBooleanValue(entry.controlled));
        builder.withProperty("multi_arm", getBooleanValue(entry.multiArm));
        builder.withProperty("pediatric", getBooleanValue(entry.pediatric));
        builder.withPropertyIfNotNull("country_pi", entry.countryPI);
        builder.withPropertyIfNotNull("primary_ep", createArray(entry.primaryEP));
        builder.withPropertyIfNotNull("phase", entry.phase);
        builder.withPropertyIfNotNull("removed", getBooleanValue(entry.removed));
        final Node node = builder.build();
        exportEntryDrugs(graph, entry, node);
        exportEntryDiseases(graph, entry, node);
    }

    private String cleanGender(final String gender) {
        if (StringUtils.isEmpty(gender) || "-".equals(gender.trim()))
            return null;
        switch (gender.toLowerCase(Locale.ROOT)) {
            case "both":
            case "both males and females":
            case "both, male and female":
            case "female|male":
            case "male and female":
                return "Male and Female";
            case "male":
            case "males":
                return "Male";
            case "female":
            case "females":
                return "Female";
            case "all":
                return "All";
        }
        return gender;
    }

    private String cleanStudyType(final String studyType) {
        if (StringUtils.isEmpty(studyType))
            return null;
        switch (studyType.toLowerCase(Locale.ROOT)) {
            case "intervention":
            case "interventional":
            case "interventional study":
                return "Interventional Study";
            case "treatment study":
                return "Treatment Study";
        }
        return studyType;
    }

    private String[] cleanOtherIds(final String otherIds) {
        if (StringUtils.isEmpty(otherIds))
            return null;
        if ("nil".equalsIgnoreCase(otherIds) || "nil known".equalsIgnoreCase(otherIds) || "nil known.".equalsIgnoreCase(
                otherIds) || "none".equalsIgnoreCase(otherIds) || "not applicable".equalsIgnoreCase(otherIds) ||
            "no id yet".equalsIgnoreCase(otherIds))
            return null;
        // TODO: list
        return new String[]{otherIds};
    }

    /**
     * Split the text by semicolon + space and return the elements trimmed as an array.
     */
    private String[] createArray(final String value) {
        if (StringUtils.isEmpty(value))
            return null;
        return Arrays.stream(StringUtils.splitByWholeSeparator(value, "; ")).map(String::trim).filter(
                v -> v.length() > 0).toArray(String[]::new);
    }

    private Boolean getBooleanValue(final String value) {
        return StringUtils.isEmpty(value) ? null : "Y".equalsIgnoreCase(value);
    }

    private void exportEntryDrugs(final Graph graph, final Entry entry, final Node node) {
        final String[] drugNames = createArray(entry.drugINN);
        final String[] drugBankIds = createArray(entry.drugBank);
        if (drugNames != null)
            for (int i = 0; i < drugNames.length; i++) {
                final String drugBankId =
                        drugBankIds != null && i < drugBankIds.length && !"Not found in DrugBank".equalsIgnoreCase(
                                drugBankIds[i]) ? drugBankIds[i] : null;
                graph.addEdge(node, findOrCreateDrug(graph, drugNames[i], drugBankId), INVESTIGATES_LABEL);
            }
    }

    private Node findOrCreateDrug(final Graph graph, final String drugName, final String drugBankId) {
        Node node;
        if (drugBankId != null)
            node = graph.findNode(DRUG_LABEL, "drugbank_id", drugBankId);
        else
            node = graph.findNode(DRUG_LABEL, "name", drugName);
        if (node == null) {
            if (drugBankId != null)
                node = graph.addNode(DRUG_LABEL, "drugbank_id", drugBankId, "name", drugName);
            else
                node = graph.addNode(DRUG_LABEL, "name", drugName);
        }
        return node;
    }

    private void exportEntryDiseases(final Graph graph, final Entry entry, final Node node) {
        final String[] cancerGroups = createArray(entry.cancerGroup);
        final String[] cancerTypes = createArray(entry.cancerType);
        if (cancerTypes != null)
            for (final String cancerType : cancerTypes)
                graph.addEdge(node, findOrCreateDisease(graph, cancerGroups, cancerType), INVESTIGATES_LABEL);
    }

    private Node findOrCreateDisease(final Graph graph, final String[] cancerGroups, final String cancerType) {
        final String name = cancerType.replace("&#39;", "'");
        Node node = graph.findNode(DISEASE_LABEL, "name", name);
        if (node == null) {
            if (cancerGroups != null)
                node = graph.addNode(DISEASE_LABEL, "name", name, "groups", cancerGroups);
            else
                node = graph.addNode(DISEASE_LABEL, "name", name);
        }
        return node;
    }
}
