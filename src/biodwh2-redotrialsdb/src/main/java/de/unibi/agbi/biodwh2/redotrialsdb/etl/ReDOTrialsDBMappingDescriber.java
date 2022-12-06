package de.unibi.agbi.biodwh2.redotrialsdb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public final class ReDOTrialsDBMappingDescriber extends MappingDescriber {
    public static final Pattern NCT_NUMBER_PATTERN = Pattern.compile("NCT[0-9]+");
    public static final Pattern JPRN_PATTERN = Pattern.compile("JPRN-(jRCTs?|UMIN|C)[0-9]+");
    public static final Pattern UMIN_PATTERN = Pattern.compile("UMIN[0-9]+");
    public static final Pattern CTRI_PATTERN = Pattern.compile("CTRI/[0-9]{4}/[0-9]{2,3}/[0-9]+");
    public static final Pattern NCI_PATTERN = Pattern.compile("NCI-[0-9]{4}-[0-9]+");
    public static final Pattern EMA_CTR_PATTERN = Pattern.compile("[0-9]{4}-[0-9]+-[0-9]{2}");
    public static final Pattern CHI_CTR_PATTERN = Pattern.compile("ChiCTR(-I[IOP]R-)?[0-9]+");
    public static final Pattern ACTRN_PATTERN = Pattern.compile("ACTRN[0-9]+");
    public static final Pattern PACTR_PATTERN = Pattern.compile("PACTR[0-9]+");
    public static final Pattern DRKS_PATTERN = Pattern.compile("DRKS[0-9]+");
    public static final Pattern NTR_PATTERN = Pattern.compile("NTR[0-9]+");
    public static final Pattern NTR_NL_PATTERN = Pattern.compile("NL[0-9]+");
    public static final Pattern IRCT_PATTERN = Pattern.compile("IRCT[0-9]+N[0-9]");
    public static final Pattern ISRCTN_PATTERN = Pattern.compile("ISRCTN[0-9]+");
    public static final Pattern KCT_PATTERN = Pattern.compile("KCT[0-9]+");
    public static final Pattern TCTR_PATTERN = Pattern.compile("TCTR[0-9]+");
    public static final Pattern RPCEC_PATTERN = Pattern.compile("RPCEC[0-9]+");
    public static final Pattern REBEC_PATTERN = Pattern.compile("RBR-[0-9a-z]+");

    public ReDOTrialsDBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (ReDOTrialsDBGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (ReDOTrialsDBGraphExporter.TRIAL_LABEL.equals(localMappingLabel))
            return describeTrial(node);
        if (ReDOTrialsDBGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.INN, node.<String>getProperty("name"));
        description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTrial(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.CLINICAL_TRIAL);
        final String[] otherIds = node.getProperty("other_ids");
        if (otherIds != null)
            for (final String id : otherIds)
                processTrialId(description, id);
        processTrialId(description, node.getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private void processTrialId(final NodeMappingDescription description, final String id) {
        if (StringUtils.isEmpty(id))
            return;
        if (NCT_NUMBER_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.NCT_NUMBER, id);
        else if (JPRN_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.JPRN_TRIAL, id);
        else if (UMIN_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.JPRN_TRIAL, "JPRN-" + id);
        else if (CTRI_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.CTRI_TRIAL, id);
        else if (NCI_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.NCI_TRIAL, id);
        else if (EMA_CTR_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.EMA_CTR, id);
        else if (CHI_CTR_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.CHI_CTR, id);
        else if (ACTRN_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.ACTRN_TRIAL, id);
        else if (PACTR_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.PACTR_TRIAL, id);
        else if (DRKS_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.DRKS_TRIAL, id);
        else if (NTR_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.NTR_TRIAL, id);
        else if (NTR_NL_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.NTR_TRIAL, id);
        else if (IRCT_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.IRCT_TRIAL, id);
        else if (ISRCTN_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.ISRCTN_TRIAL, id);
        else if (KCT_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.KDCA_KCT, id);
        else if (TCTR_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.TCTR_TRIAL, id);
        else if (RPCEC_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.RPCEC_TRIAL, id);
        else if (REBEC_PATTERN.matcher(id).matches())
            description.addIdentifier(IdentifierType.REBEC_TRIAL, id);
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        // TODO: no id provided
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith(ReDOTrialsDBGraphExporter.INVESTIGATES_LABEL))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INVESTIGATES);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                ReDOTrialsDBGraphExporter.DRUG_LABEL, ReDOTrialsDBGraphExporter.TRIAL_LABEL,
                ReDOTrialsDBGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(ReDOTrialsDBGraphExporter.TRIAL_LABEL,
                                      ReDOTrialsDBGraphExporter.INVESTIGATES_LABEL,
                                      ReDOTrialsDBGraphExporter.DISEASE_LABEL, EdgeDirection.FORWARD),
                new PathMapping().add(ReDOTrialsDBGraphExporter.TRIAL_LABEL,
                                      ReDOTrialsDBGraphExporter.INVESTIGATES_LABEL,
                                      ReDOTrialsDBGraphExporter.DRUG_LABEL, EdgeDirection.FORWARD)
        };
    }
}
