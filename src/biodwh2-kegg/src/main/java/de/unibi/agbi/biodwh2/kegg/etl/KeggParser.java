package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.Drug;
import de.unibi.agbi.biodwh2.kegg.model.DrugGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KeggParser extends Parser {
    private static final Logger logger = LoggerFactory.getLogger(KeggParser.class);

    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        KeggDataSource keggDataSource = (KeggDataSource) dataSource;
        parseDrugGroups(workspace, keggDataSource);
        keggDataSource.diseases = new ArrayList<>();
        parseDrugs(workspace, keggDataSource);
        keggDataSource.networks = new ArrayList<>();
        keggDataSource.variants = new ArrayList<>();
        return false;
    }

    private boolean parseDrugGroups(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.drugGroups = new ArrayList<>();
        dataSource.drugGroupChildMap = new HashMap<>();
        List<Map<String, List<String>>> drugGroups = parseKeggFile(
                dataSource.resolveSourceFilePath(workspace, "dgroup"));
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
            entry.remove("id");
            entry.remove("tags");
            entry.remove("NAME");
            entry.remove("REMARK");
            entry.remove("COMMENT");
            entry.remove("  STEM");
            entry.remove("MEMBER");
            // TODO: CLASS
            dataSource.drugGroups.add(drugGroup);
        }
        return true;
    }

    private List<Map<String, List<String>>> parseKeggFile(String filePath) throws ParserException {
        List<Map<String, List<String>>> result = new ArrayList<>();
        Map<String, List<String>> currentEntry = new HashMap<>();
        String lastKeyword = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.stripEnd(line, null);
                if (line.startsWith("///")) {
                    result.add(currentEntry);
                    currentEntry = new HashMap<>();
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
                        currentEntry.put("id", Arrays.stream(parts).limit(1).collect(Collectors.toList()));
                        currentEntry.put("tags", Arrays.stream(parts).skip(1).collect(Collectors.toList()));
                    } else {
                        if (!currentEntry.containsKey(keyword))
                            currentEntry.put(keyword, new ArrayList<>());
                        currentEntry.get(keyword).add(value);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse kegg file '" + filePath + "'", e);
        }
        return result;
    }

    private boolean parseDrugs(Workspace workspace, KeggDataSource dataSource) throws ParserException {
        dataSource.drugs = new ArrayList<>();
        List<Map<String, List<String>>> drugs = parseKeggFile(dataSource.resolveSourceFilePath(workspace, "drug"));
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
        return true;
    }
}
