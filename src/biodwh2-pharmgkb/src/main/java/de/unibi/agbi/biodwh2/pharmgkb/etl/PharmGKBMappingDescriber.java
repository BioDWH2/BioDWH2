package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class PharmGKBMappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if ("Drug".equalsIgnoreCase(node.getLabel()))
            return describeDrug(node);
        if ("Chemical".equalsIgnoreCase(node.getLabel()))
            return describeChemical(node);
        if ("Haplotype".equalsIgnoreCase(node.getLabel()) || "HaplotypeSet".equalsIgnoreCase(node.getLabel()))
            return describeHaplotype(node);
        if ("Gene".equalsIgnoreCase(node.getLabel()))
            return describeGene(node);
        if ("Variant".equalsIgnoreCase(node.getLabel()))
            return describeVariant(node);
        if ("Pathway".equalsIgnoreCase(node.getLabel()))
            return describePathway(node);
        return null;
    }

    private NodeMappingDescription describeDrug(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Drug);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DrugBank, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("KEGG Drug"))
                description.addIdentifier(IdentifierType.KEGG, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("Chemical Abstracts Service"))
                description.addIdentifier(IdentifierType.CAS, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private String[] getCrossReferences(final Node node) {
        return node.hasProperty("cross_references") ? node.getProperty("cross_references") : new String[0];
    }

    private NodeMappingDescription describeChemical(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Compound);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DrugBank, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("PubChem Compound"))
                description.addIdentifier(IdentifierType.PubChemCompound, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private NodeMappingDescription describeHaplotype(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Haplotype);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        return description;
    }

    private NodeMappingDescription describeGene(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Gene);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("HGNC"))
                description.addIdentifier(IdentifierType.HGNCId, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GeneCard"))
                description.addIdentifier(IdentifierType.GeneCard, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GenAtlas"))
                description.addIdentifier(IdentifierType.GenAtlas, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private NodeMappingDescription describeVariant(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Variant);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        return description;
    }

    private NodeMappingDescription describePathway(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.Pathway);
        description.addIdentifier(IdentifierType.PharmGKB, node.getProperty("id"));
        return description;
    }

    @Override
    public EdgeMappingDescription describe(final Graph graph, final Edge edge) {
        return null;
    }
}
