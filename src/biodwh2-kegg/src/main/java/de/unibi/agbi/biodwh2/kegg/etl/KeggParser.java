package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KeggParser extends Parser<KeggDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(KeggParser.class);
    private static final List<String> Ignored_Keywords = Arrays.asList("INTERACTION", "ATOM", "BOND");

    @Override
    public boolean parse(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        boolean success = parseDrugGroups(workspace, dataSource);
        success = success && parseDiseases(workspace, dataSource);
        success = success && parseDrugs(workspace, dataSource);
        success = success && parseNetworks(workspace, dataSource);
        success = success && parseVariants(workspace, dataSource);
        return success;
    }

    private boolean parseDrugGroups(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.drugGroups = new ArrayList<>();
        dataSource.drugGroupChildMap = new HashMap<>();
        List<KeggEntry> drugGroups = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "dgroup"));
        /*
        for (Map<String, List<String>> entry : drugGroups) {
            DrugGroup drugGroup = new DrugGroup();
            drugGroup.id = entry.get("id").get(0);
            drugGroup.externalIds.add("KEGG:" + drugGroup.id);
            drugGroup.tags = entry.get("tags");
            drugGroup.names = entry.get("NAME");
            if (entry.containsKey("REMARK")) {
                for (String remark : entry.get("REMARK")) {
                    if (remark.startsWith("ATC code:")) {
                        String atcCodes = remark.split(":")[1].trim();
                        List<String> atcIds = Arrays.stream(atcCodes.split(" ")).filter(x -> x.length() > 0).map(
                                x -> "ATC:" + x).collect(Collectors.toList());
                        drugGroup.externalIds.addAll(atcIds);
                    }
                }
            }
            if (entry.containsKey("COMMENT")) {
                for (String comment : entry.get("COMMENT")) {
                    List<String> comments = Arrays.stream(comment.split(",")).map(String::trim).collect(
                            Collectors.toList());
                    drugGroup.comments.addAll(comments);
                }
            }
            if (entry.containsKey("  STEM")) {
                for (String nameStem : entry.get("  STEM")) {
                    List<String> nameStems = Arrays.stream(nameStem.split(",")).map(String::trim).collect(
                            Collectors.toList());
                    drugGroup.nameStems.addAll(nameStems);
                }
            }
            if (entry.containsKey("MEMBER")) {
                Stack<String> depthStack = new Stack<>();
                depthStack.add(entry.get("id").get(0));
                int lastDepth = -1;
                for (String member : entry.get("MEMBER")) {
                    int depth = member.length() - StringUtils.stripStart(member, null).length();
                    member = member.trim();
                    String id = member.substring(0, member.indexOf(' '));
                    if (depth > lastDepth)
                        depthStack.push(id);
                    else if (depth < lastDepth)
                        while (depthStack.size() - 2 > depth)
                            depthStack.pop();
                    lastDepth = depth;
                    depthStack.set(depthStack.size() - 1, id);
                    String parentId = depthStack.get(depthStack.size() - 2);
                    if (!dataSource.drugGroupChildMap.containsKey(parentId))
                        dataSource.drugGroupChildMap.put(parentId, new HashSet<>());
                    dataSource.drugGroupChildMap.get(parentId).add(id);
                }
            }
            // TODO: CLASS
            dataSource.drugGroups.add(drugGroup);
        }
        */
        return true;
    }

    private List<KeggEntry> parseKeggFile(String filePath) throws ParserException {
        List<KeggEntry> result = new ArrayList<>();
        KeggEntry currentEntry = new KeggEntry();
        String lastKeyword = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.stripEnd(line, null);
                if (line.startsWith("///")) {
                    result.add(currentEntry);
                    currentEntry = new KeggEntry();
                    lastKeyword = null;
                } else {
                    String keyword = StringUtils.stripEnd(line.substring(0, Math.min(12, line.length())), null);
                    if (keyword.length() > 0)
                        lastKeyword = keyword;
                    else
                        keyword = lastKeyword;
                    String value = line.length() > 12 ? StringUtils.stripEnd(line.substring(12), null) : "";
                    if (keyword.equals("ENTRY")) {
                        String[] parts = Arrays.stream(value.split(" ")).filter(x -> x.length() > 0).toArray(
                                String[]::new);
                        currentEntry.id = parts[0];
                        currentEntry.tags = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                    } else if (keyword.equals("REFERENCE")) {
                        Reference reference = new Reference();
                        currentEntry.references.add(reference);
                        reference.pmid = value.trim();
                    } else if (keyword.equals("  AUTHORS")) {
                        Reference reference = currentEntry.references.get(currentEntry.references.size() - 1);
                        if (reference.authors != null)
                            logger.warn("Overwriting authors for reference '" + reference.pmid + "'");
                        reference.authors = value.trim();
                    } else if (keyword.equals("  TITLE")) {
                        Reference reference = currentEntry.references.get(currentEntry.references.size() - 1);
                        if (reference.title != null)
                            logger.warn("Overwriting title for reference '" + reference.pmid + "'");
                        reference.title = value.trim();
                    } else if (keyword.equals("  JOURNAL")) {
                        Reference reference = currentEntry.references.get(currentEntry.references.size() - 1);
                        if (reference.journal == null)
                            reference.journal = value.trim();
                        else if (value.toLowerCase(Locale.US).startsWith("doi:"))
                            reference.doi = value.split(":")[1].trim();
                    } else if (keyword.equals("DBLINKS")) {
                        String dbName = value.split(":")[0].trim();
                        String idValues = value.split(":")[1].trim();
                        List<String> ids = Arrays.stream(idValues.split(" ")).filter(x -> x.length() > 0).map(
                                x -> dbName + ":" + x).collect(Collectors.toList());
                        currentEntry.externalIds.addAll(ids);
                    } else if (!Ignored_Keywords.contains(keyword)) {
                        System.out.println(keyword);
                        //if (!currentEntry.containsKey(keyword))
                        //    currentEntry.put(keyword, new ArrayList<>());
                        //currentEntry.get(keyword).add(value);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse kegg file '" + filePath + "'", e);
        }
        return result;
    }

    private boolean parseDiseases(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.diseases = new ArrayList<>();
        List<KeggEntry> diseases = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "disease"));
        /*
        for (Map<String, List<Object>> entry : diseases) {
            Disease disease = new Disease();
            disease.id = (String) entry.get("id").get(0);
            disease.externalIds.add("KEGG:" + disease.id);
            disease.tags = entry.get("tags");
            disease.names = entry.get("NAME");
            if (entry.containsKey("DBLINKS")) {
                for (String remark : entry.get("DBLINKS")) {
                    String dbName = remark.split(":")[0].trim();
                    String idValues = remark.split(":")[1].trim();
                    List<String> ids = Arrays.stream(idValues.split(" ")).filter(x -> x.length() > 0).map(
                            x -> dbName + ":" + x).collect(Collectors.toList());
                    disease.externalIds.addAll(ids);
                }
            }
            if (entry.containsKey("DRUG"))
                for (String drug : entry.get("DRUG"))
                    // TODO: consider factors after [DR:D00000]
                    disease.indicatedDrugIds.add(drug.split("\\[")[1].split("]")[0].split(":")[1]);
            // [ENV_FACTOR,   SUBGROUP, NETWORK, PATHOGEN,   MODULE, CARCINOGEN, COMMENT,   SUPERGRP, GENE,   JOURNAL, DESCRIPTION, CATEGORY, REFERENCE,   TITLE,   ELEMENT,   AUTHORS]
            dataSource.diseases.add(disease);
        }
        */
        return true;
    }

    private boolean parseDrugs(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.drugs = new ArrayList<>();
        List<KeggEntry> drugs = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "drug"));
        /*
        for (Map<String, List<String>> entry : drugs) {
            Drug drug = new Drug();
            drug.id = entry.get("id").get(0);
            drug.externalIds.add("KEGG:" + drug.id);
            drug.tags = entry.get("tags");
            drug.names = entry.get("NAME");
            if (entry.containsKey("DBLINKS")) {
                for (String remark : entry.get("DBLINKS")) {
                    String dbName = remark.split(":")[0].trim();
                    String idValues = remark.split(":")[1].trim();
                    List<String> ids = Arrays.stream(idValues.split(" ")).filter(x -> x.length() > 0).map(
                            x -> dbName + ":" + x).collect(Collectors.toList());
                    drug.externalIds.addAll(ids);
                }
            }
            if (entry.containsKey("FORMULA")) {
                List<String> formulas = entry.get("FORMULA");
                if (formulas.size() > 1)
                    logger.warn(
                            "KEGG drug with id '" + drug.id + "' has multiple formula '" + formulas.toString() + "'");
                drug.formula = formulas.get(0).trim();
            }
            if (entry.containsKey("SEQUENCE"))
                drug.sequences = entry.get("SEQUENCE");
            if (entry.containsKey("EXACT_MASS"))
                drug.exactMass = entry.get("EXACT_MASS").get(0).trim();
            if (entry.containsKey("MOL_WEIGHT"))
                drug.molecularWeight = entry.get("MOL_WEIGHT").get(0).trim();
            entry.remove("id");
            entry.remove("tags");
            entry.remove("NAME");
            entry.remove("DBLINKS");
            entry.remove("FORMULA");
            entry.remove("EXACT_MASS");
            entry.remove("MOL_WEIGHT");
            entry.remove("SEQUENCE");
            // EFFICACY
            //   DISEASE
            // TARGET
            // COMPONENT
            // METABOLISM
            //   TYPE
            // REMARK
            // COMMENT
            // CLASS
            // INTERACTION
            // SOURCE
            //   REPEAT
            entry.remove("BOND");
            entry.remove("ATOM");
            entry.remove("BRACKET");
            dataSource.drugs.add(drug);
        }
        */
        return true;
    }

    private boolean parseNetworks(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.networks = new ArrayList<>();
        List<KeggEntry> networks = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "network"));
        /*
        for (Map<String, List<String>> entry : networks) {
            Network network = new Network();
            network.id = entry.get("id").get(0);
            network.externalIds.add("KEGG:" + network.id);
            network.tags = entry.get("tags");
            network.names = entry.get("NAME");
            dataSource.networks.add(network);
        }
        */
        return true;
    }

    private boolean parseVariants(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.variants = new ArrayList<>();
        List<KeggEntry> variants = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "variant"));
        /*
        for (Map<String, List<String>> entry : variants) {
            Variant variant = new Variant();
            variant.id = entry.get("id").get(0);
            variant.externalIds.add("KEGG:" + variant.id);
            variant.tags = entry.get("tags");
            variant.names = entry.get("NAME");
            dataSource.variants.add(variant);
        }
        */
        return true;
    }
}
