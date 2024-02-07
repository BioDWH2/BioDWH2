package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;
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
        final String source = node.getProperty("source");
        final String id = node.getProperty("id");
        if (source != null && id != null) {
            // "Other", "FDA", "DailyMed", "ISBN", "Wikipedia", "EMA", "KEGG", "PMDA", "Health Canada", "Expert",
            // "UniProt", "Patent", "IUPHAR", "PubChem", "InterPro", "USGPO", "NTP", "MEDSAFE"
            switch (source) {
                case "PubMed": {
                    PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
                    description.pubmedId = Integer.parseInt(id);
                    description.addIdentifier(IdentifierType.PUBMED_ID, description.pubmedId);
                    return new NodeMappingDescription[]{description};
                }
                case "DOI": {
                    PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
                    description.doi = id;
                    description.addIdentifier(IdentifierType.DOI, id);
                    return new NodeMappingDescription[]{description};
                }
                case "PMC": {
                    PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
                    description.pmcId = id;
                    description.addIdentifier(IdentifierType.PUBMED_CENTRAL_ID, id);
                    return new NodeMappingDescription[]{description};
                }
                case "ClinicalTrials": {
                    NodeMappingDescription description = new NodeMappingDescription(
                            NodeMappingDescription.NodeType.CLINICAL_TRIAL);
                    description.addIdentifier(IdentifierType.NCT_NUMBER, id);
                    return new NodeMappingDescription[]{description};
                }
            }
        }
        return null;
    }

    private NodeMappingDescription[] describeMolecule(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        description.addNames(node.<String[]>getProperty("trade_names"));
        description.addIdentifier(IdentifierType.CHEMBL, node.<String>getProperty("id"));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] parts = StringUtils.split(xref, ":", 2);
                switch (parts[0].trim().toUpperCase(Locale.ROOT)) {
                    // "WIKIPEDIA", "DAILYMED", "TG-GATES"
                    case "DRUGCENTRAL":
                        description.addIdentifier(IdentifierType.DRUG_CENTRAL, Integer.parseInt(parts[1]));
                        break;
                    case "DRUGBANK":
                        description.addIdentifier(IdentifierType.DRUG_BANK, parts[1]);
                        break;
                    case "PUBCHEM":
                        description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(parts[1]));
                        break;
                    case "CHEBI":
                        description.addIdentifier(IdentifierType.CHEBI, Integer.parseInt(parts[1]));
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("exact_synonyms"));
        final String[] idParts = StringUtils.split(node.<String>getProperty("id"), "_", 2);
        switch (idParts[0].trim().toUpperCase(Locale.ROOT)) {
            // "Orphanet", "HP", "NCIT", "DOID", "OBA", "OTAR", "GO", "OBI", "OGMS", "MP"
            case "EFO":
                description.addIdentifier(IdentifierType.EFO, idParts[1]);
                break;
            case "MONDO":
                description.addIdentifier(IdentifierType.MONDO, idParts[1]);
                break;
        }
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] parts = StringUtils.split(xref, ":", 2);
                switch (parts[0].trim().toUpperCase(Locale.ROOT)) {
                    // "PMID", "NCIT", "SCTID", "ICD10EXP", "MESH", "UMLS", "ICD9", "Orphanet", "DOID", "ICDO",
                    // "MEDDRA", "ICD10", "DECIPHER", "http", "ONCOTREE", "MetaCyc", "MeSH", "MSH", "SNOMEDCT_US",
                    // "SNOMEDCT", "HP", "ICD10CM", "Wikipedia", "OMIMPS", "snomedct", "KEGG COMPOUND", "HMDB",
                    // "PubChem", "CAS", "Fyler", "MP", "ORDO", "GARD", "https", "NIFSTD", "ICD10WHO", "ICD9CM", "GTR",
                    // "COHD", "CSP", "HGNC", "GO", "CMO", "PERSON", "MEDGEN", "MFOMD", "MO", "modelled on http", "OAE",
                    // "RESID", "REACTOME", "ORCID", "T3DB", "ENM", "OMIT", "ICD-10", "ICD11", "NCIm",
                    // "Quantification of ethanolamine in a sample.", "Wikidata", "VT", "KEGG", "DERMO", "PR", "doi",
                    // "OBI", "DC", "EPCC", "SYMP", "UniProt", "MedlinePlus", "SCDO", "SCTID_2010_1_31", "NDFRT",
                    // "TXPO", "NPO", "CRISP Thesaurus 2006, Term Number 2000-0386, http", "MTH", "ISBN-13", "ISBN-10",
                    // "DI", "ISBN", "IDO"
                    case "OMIM":
                        description.addIdentifier(IdentifierType.OMIM, Integer.parseInt(parts[1]));
                        break;
                    case "EFO":
                        description.addIdentifier(IdentifierType.EFO, parts[1]);
                        break;
                    case "MONDO":
                        description.addIdentifier(IdentifierType.MONDO, parts[1]);
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
