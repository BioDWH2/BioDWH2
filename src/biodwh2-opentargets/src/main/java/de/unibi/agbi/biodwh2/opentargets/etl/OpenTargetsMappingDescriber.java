package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class OpenTargetsMappingDescriber extends MappingDescriber {
    public OpenTargetsMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (OpenTargetsGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReference(node);
        if (OpenTargetsGraphExporter.MOLECULE_LABEL.equals(localMappingLabel))
            return describeMolecule(node);
        if (OpenTargetsGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        NodeMappingDescription description = null;
        final String source = node.getProperty("source");
        final String id = node.getProperty("id");
        if (source != null && id != null) {
            // "Other", "FDA", "DailyMed", "ISBN", "Wikipedia", "EMA", "KEGG", "PMDA", "Health Canada", "Expert",
            // "UniProt", "Patent", "IUPHAR", "PubChem", "InterPro", "USGPO", "NTP", "MEDSAFE"
            switch (source) {
                case "PubMed":
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
                    description.addIdentifier(IdentifierType.PUBMED_ID, id);
                    break;
                case "DOI":
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
                    description.addIdentifier(IdentifierType.DOI, id);
                    break;
                case "PMC":
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
                    description.addIdentifier(IdentifierType.PUBMED_CENTRAL_ID, id);
                    break;
                case "ClinicalTrials":
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.CLINICAL_TRIAL);
                    description.addIdentifier(IdentifierType.NCT_NUMBER, id);
                    break;
            }
        }
        return description != null ? new NodeMappingDescription[]{description} : null;
    }

    private NodeMappingDescription[] describeMolecule(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        description.addNames(node.<String[]>getProperty("trade_names"));
        description.addIdentifier(IdentifierType.CHEMBL, node.<String>getProperty("id"));
        // TODO: cross references
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        final String[] xrefs = node.getProperty("db_xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] parts = StringUtils.split(xref, ":", 2);
                // "DOID", "NCIT", "SCTID", "GARD", "MESH", "ICD9", "MEDDRA", "OMIMPS", "COHD", "DC", "KEGG", "EFO",
                // "HP", "ORPHANET", "GTR", "ICDO", "MEDGEN", "PMID", "HGNC", "ONCOTREE", "WIKIPEDIA", "CSP",
                // "ICD10CM", "ORDO", "EPCC", "MSH", "FYLER", "ICD-10", "SNOMEDCT_US", "BAMS", "EV", "MAT", "EMAPA",
                // "HBA", "EHDAA", "BIRNLEX", "MIAA", "GAID", "{"@ID"", "HTTP", "CALOHA", "VHOG", "FMA", "OPENCYC",
                // "BM", "AAO", "MBA", "DHBA", "ZFA", "MA", "EHDAA2", "PBA", "TAO", "BTO", "FBBT", "XAO", "GALEN",
                // "VSAO", "GO", "VFB", "RETIRED_EHDAA2", "WBBT", "AEO", "HAO", "SPD", "BILA", "TGMA", "NLXANAT",
                // "CARO", "NIFSTD", "DMBA", "TADS", "REACTOME", "SCDO", "UBERON", "MFOMD", "MEDLINEPLUS", "NCIM",
                // "FMAID", "SAEL", "NCITHESAURUS", "ABA", "MO", "MP", "BSA", "ANISEED", "EMAPA_RETIRED", "ICD9CM",
                // "NLX", "WIKIPEDIACATEGORY", "URL", "HTTPS", "SNOMEDCT_US_2018_03_01", "OAE", "CMO", "MEDRA",
                // "MODELLED ON HTTP", "PERSON", "UNIPROT", "DOI", "NCIT_C112286", "SCTID_2010_1_31", "SYMP", "NPO",
                // "DI", "ORCID", "OMIT", "NCI_THESAURUS", "OBI", "NDFRT", "TE", "ISBN", "GOC", "ZFA_RETIRED",
                // "TAO_RETIRED", "UMLS_CUI", "MFMO", "WIKIDATA", "EHDAA2_RETIRED", "OGEM", "KUPO", "CL",
                // "NIFSTD_RETIRED", "NCI", "DERMO", "MFO", "ICD11", "RESID", "ENVO", "MAP", "METACYC", "ISBN-13",
                // "ISBN-10", "PMID 32701512", "EHDA", "IDO", "MTH", " MEDDRA", "ENM"
                switch (parts[0].toUpperCase(Locale.ROOT)) {
                    case "MONDO":
                        description.addIdentifier(IdentifierType.MONDO, parts[1]);
                        break;
                    case "ICD10":
                        description.addIdentifier(IdentifierType.ICD10, parts[1]);
                        break;
                    case "OMIM":
                        description.addIdentifier(IdentifierType.OMIM, parts[1]);
                        break;
                    case "UMLS":
                        description.addIdentifier(IdentifierType.UMLS_CUI, parts[1]);
                        break;
                    case "SNOMEDCT":
                        description.addIdentifier(IdentifierType.SNOMED_CT, parts[1]);
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                OpenTargetsGraphExporter.REFERENCE_LABEL, OpenTargetsGraphExporter.MOLECULE_LABEL,
                OpenTargetsGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 2 && edges[0].getLabel().endsWith(OpenTargetsGraphExporter.INDICATES_LABEL))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(OpenTargetsGraphExporter.MOLECULE_LABEL, OpenTargetsGraphExporter.INDICATES_LABEL,
                                      OpenTargetsGraphExporter.INDICATION_LABEL, EdgeDirection.FORWARD).add(
                        OpenTargetsGraphExporter.INDICATION_LABEL, OpenTargetsGraphExporter.INDICATES_LABEL,
                        OpenTargetsGraphExporter.DISEASE_LABEL, EdgeDirection.FORWARD)
        };
    }
}
