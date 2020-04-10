package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeggParser extends Parser<KeggDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(KeggParser.class);
    private static final Pattern referencePattern = Pattern.compile(
            "PMID:([0-9]+)(([ \n\r]+\\(\\(?[a-zA-Z0-9/\n\r. ,_\\-]+\\)?\\))*)");
    private static final Pattern targetIdsPattern = Pattern.compile("\\[([A-Za-z]+:)([0-9A-Za-z]+( [0-9A-Za-z]+)*)]");

    private static class ChunkLine {
        String keyword;
        String value;
    }

    @Override
    public boolean parse(final Workspace workspace, final KeggDataSource dataSource) throws ParserException {
        parseKeggFile(workspace, dataSource, Variant.class, "variant");
        parseKeggFile(workspace, dataSource, DrugGroup.class, "dgroup");
        parseKeggFile(workspace, dataSource, Drug.class, "drug");
        parseKeggFile(workspace, dataSource, Disease.class, "disease");
        parseKeggFile(workspace, dataSource, Network.class, "network");
        return true;
    }

    private <T extends KeggEntry> List<T> parseKeggFile(final Workspace workspace, final DataSource dataSource,
                                                        final Class<T> entryClass,
                                                        final String fileName) throws ParserFormatException {
        List<T> result = new ArrayList<>();
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            List<ChunkLine> chunk = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.stripEnd(line, null);
                if (line.startsWith("///")) {
                    result.add(processChunk(entryClass, chunk.toArray(new ChunkLine[0])));
                    chunk.clear();
                    continue;
                }
                String keyword = StringUtils.stripEnd(line.substring(0, Math.min(12, line.length())), null);
                String value = line.length() > 12 ? StringUtils.stripEnd(line.substring(12), null) : "";

                if (keyword.length() > 0) {
                    ChunkLine chunkLine = new ChunkLine();
                    chunkLine.keyword = keyword;
                    chunkLine.value = value;
                    chunk.add(chunkLine);
                } else {
                    //noinspection StringConcatenationInLoop
                    chunk.get(chunk.size() - 1).value += "\n" + value;
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse kegg file '" + filePath + "'", e);
        }
        return result;
    }

    private <T extends KeggEntry> T processChunk(final Class<T> entryClass, final ChunkLine[] chunk) {
        T entry = createInstance(entryClass);
        if (entry == null)
            return null;
        for (int i = 0; i < chunk.length; i++) {
            ChunkLine line = chunk[i];
            final boolean lineNotEmpty = line.value.trim().length() > 0;
            switch (line.keyword) {
                case "ENTRY":
                    String[] parts = StringUtils.split(line.value, " ");
                    entry.id = parts[0];
                    entry.tags.addAll(Arrays.asList(parts).subList(1, parts.length));
                    break;
                case "NAME":
                    entry.names.addAll(Arrays.asList(StringUtils.split(line.value, "\n")));
                    //   SUPERGRP
                    //   SUBGROUP
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  SUPERGRP") || chunk[j].keyword.equals("  SUBGROUP"))
                            i++; // TODO
                        else
                            break;
                    break;
                case "  STEM":
                    entry.nameStems.addAll(Arrays.asList(StringUtils.splitByWholeSeparator(line.value, ", ")));
                    break;
                case "REFERENCE":
                    Reference reference = parseReference(chunk, i, line);
                    entry.references.add(reference);
                    i = reference.lookAheadPosition;
                    break;
                case "DBLINKS":
                    for (String link : StringUtils.split(line.value, "\n")) {
                        String[] linkParts = StringUtils.split(link, ": ", 2);
                        for (String id : StringUtils.split(linkParts[1], " "))
                            entry.externalIds.add(linkParts[0] + ":" + id);
                    }
                    break;
                case "FORMULA":
                    if (lineNotEmpty)
                        ((Drug) entry).formula = line.value.trim();
                    break;
                case "EXACT_MASS":
                    if (lineNotEmpty)
                        ((Drug) entry).exactMass = line.value.trim();
                    break;
                case "MOL_WEIGHT":
                    if (lineNotEmpty)
                        ((Drug) entry).molecularWeight = line.value.trim();
                    break;
                case "COMMENT":
                    entry.comment = line.value;
                    break;
                case "REMARK":
                    entry.remark = line.value;
                    break;
                case "ATOM":
                    ((Drug) entry).atoms = line.value;
                    break;
                case "BOND":
                    ((Drug) entry).bonds = line.value;
                    break;
                case "INTERACTION":
                    if (lineNotEmpty)
                        ((Drug) entry).interactions.addAll(parseInteractions(line));
                    break;
                case "TARGET":
                    if (lineNotEmpty)
                        ((Drug) entry).targets.addAll(parseTargets(line));
                    //   NETWORK
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  NETWORK"))
                            i++; // TODO
                        else
                            break;
                    break;
                case "EFFICACY":
                    if (lineNotEmpty)
                        ((Drug) entry).efficacy = line.value.trim();
                    //   DISEASE
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  DISEASE"))
                            i++; // TODO
                        else
                            break;
                    break;
                case "COMPONENT":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "SEQUENCE":
                    Sequence sequence = new Sequence();
                    sequence.sequence = line.value;
                    //   TYPE
                    if (chunk[i + 1].keyword.equals("  TYPE")) {
                        sequence.type = chunk[i + 1].value;
                        i++;
                    }
                    ((Drug) entry).sequences.add(sequence);
                    break;
                case "BRACKET":
                    Bracket bracket = new Bracket();
                    bracket.value = line.value;
                    for (int j = i + 1; j < chunk.length; j++) {
                        if (chunk[j].keyword.equals("  ORIGINAL")) {
                            bracket.original = line.value;
                            i++;
                        } else if (chunk[j].keyword.equals("  REPEAT")) {
                            bracket.repeat = line.value;
                            i++;
                        } else
                            break;
                    }
                    ((Drug) entry).bracket = bracket;
                    break;
                case "METABOLISM":
                    if (lineNotEmpty)
                        ((Drug) entry).metabolisms.addAll(parseMetabolisms(line));
                    break;
                case "CLASS":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "SOURCE":
                    if (lineNotEmpty)
                        ((Drug) entry).sources.addAll(parseNameIdsPairs(line.value));
                    break;
                case "NETWORK":
                    //   ELEMENT
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  ELEMENT"))
                            i++; // TODO
                        else
                            break;
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "GENE":
                    if (entryClass == Variant.class) {
                        String[] geneRestParts = StringUtils.splitByWholeSeparator(line.value, "  ", 2);
                        ((Variant) entry).genes.put(geneRestParts[0], parseNameIdsPair(geneRestParts[1]));
                    } else if (entryClass == Disease.class) {
                        //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    } else if (entryClass == Network.class) {
                        //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    }
                    break;
                case "ORGANISM":
                    if (lineNotEmpty)
                        ((Variant) entry).organism = line.value;
                    break;
                case "VARIATION":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "DESCRIPTION":
                    if (lineNotEmpty)
                        ((Disease) entry).description = line.value;
                    break;
                case "PATHOGEN":
                    //   MODULE
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  MODULE"))
                            i++; // TODO
                        else
                            break;
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "DRUG":
                    if (lineNotEmpty)
                        ((Disease) entry).drugs.addAll(parseMultilineNameIdsPairs(line));
                    break;
                case "CATEGORY":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "ENV_FACTOR":
                    if (lineNotEmpty)
                        ((Disease) entry).envFactors.addAll(parseMultilineNameIdsPairs(line));
                    break;
                case "CARCINOGEN":
                    if (lineNotEmpty)
                        ((Disease) entry).carcinogens.addAll(parseMultilineNameIdsPairs(line));
                    break;
                case "DISEASE":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "MEMBER":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "MAP":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "TYPE":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "PERTURBANT":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "VARIANT":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "METABOLITE":
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                case "DEFINITION":
                    //   EXPANDED
                    for (int j = i + 1; j < chunk.length; j++)
                        if (chunk[j].keyword.equals("  EXPANDED"))
                            i++; // TODO
                        else
                            break;
                    //System.out.println(entryClass.getSimpleName() + "> " + line.value); // TODO
                    break;
                default:
                    System.out.println(line.keyword);
                    break;
            }
        }
        return entry;
    }

    private static <T extends KeggEntry> T createInstance(final Class<T> entryClass) {
        try {
            return entryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Reference parseReference(final ChunkLine[] chunk, final int i, final ChunkLine line) {
        Reference reference = new Reference();
        if (line.value.trim().length() > 0) {
            Matcher matcher = referencePattern.matcher(line.value);
            if (matcher.matches()) {
                reference.pmid = matcher.group(1);
                String remarks = matcher.group(2).trim().replace("\n", " ");
                if (remarks.length() > 0) {
                    reference.remarks = remarks;
                }
            } else
                reference.remarks = line.value;
        }
        for (int j = i + 1; j < chunk.length; j++) {
            ChunkLine nextLine = chunk[j];
            if (!nextLine.keyword.startsWith("  "))
                break;
            reference.lookAheadPosition = j;
            switch (nextLine.keyword) {
                case "  AUTHORS":
                    reference.authors = nextLine.value;
                    break;
                case "  TITLE":
                    reference.title = nextLine.value;
                    break;
                case "  JOURNAL":
                    String[] parts = StringUtils.split(nextLine.value, "\n");
                    reference.journal = parts[0];
                    if (parts.length > 1) {
                        for (int k = 1; k < parts.length; k++) {
                            if (parts[k].startsWith("DOI:"))
                                reference.doi = parts[k].substring(4);
                            else
                                logger.warn("Unknown journal line in: " + nextLine.value);
                        }
                    }
                    break;
            }
        }
        return reference;
    }

    private static List<Interaction> parseInteractions(final ChunkLine line) {
        List<Interaction> result = new ArrayList<>();
        for (String subLine : StringUtils.split(line.value.trim(), "\n")) {
            String[] typeRestParts = StringUtils.split(subLine, ":", 2);
            for (NameIdsPair target : parseNameIdsPairs(typeRestParts[1])) {
                Interaction interaction = new Interaction();
                interaction.type = typeRestParts[0];
                interaction.target = target;
                result.add(interaction);
            }
        }
        return result;
    }

    private static List<NameIdsPair> parseNameIdsPairs(final String line) {
        List<NameIdsPair> result = new ArrayList<>();
        String[] parts = StringUtils.split(line, ";,");
        for (String pair : parts)
            result.add(parseNameIdsPair(pair));
        return result;
    }

    private static NameIdsPair parseNameIdsPair(String line) {
        NameIdsPair pair = new NameIdsPair();
        pair.name = line;
        int replaceOffset = 0;
        Matcher matcher = targetIdsPattern.matcher(line);
        while (matcher.find()) {
            pair.name = pair.name.substring(0, matcher.start() - replaceOffset) + pair.name.substring(
                    matcher.end() - replaceOffset);
            replaceOffset += matcher.end() - matcher.start();
            String prefix = matcher.group(1);
            String[] ids = StringUtils.split(matcher.group(2), " ");
            for (String id : ids)
                pair.ids.add(prefix + id);
        }
        pair.name = pair.name.trim().replace("  ", " ");
        return pair;
    }

    private static List<NameIdsPair> parseTargets(final ChunkLine line) {
        List<NameIdsPair> result = new ArrayList<>();
        for (String subLine : StringUtils.split(line.value.trim(), "\n"))
            result.add(parseNameIdsPair(subLine));
        return result;
    }

    private static List<Metabolism> parseMetabolisms(final ChunkLine line) {
        List<Metabolism> result = new ArrayList<>();
        for (String subLine : StringUtils.split(line.value.trim(), "\n")) {
            String[] typeRestParts = StringUtils.split(subLine, ":", 2);
            for (NameIdsPair target : parseNameIdsPairs(typeRestParts[1])) {
                Metabolism metabolism = new Metabolism();
                metabolism.type = typeRestParts[0];
                metabolism.target = target;
                result.add(metabolism);
            }
        }
        return result;
    }

    private static List<NameIdsPair> parseMultilineNameIdsPairs(final ChunkLine line) {
        List<NameIdsPair> result = new ArrayList<>();
        for (String subLine : StringUtils.split(line.value.trim(), "\n"))
            result.add(parseNameIdsPair(subLine));
        return result;
    }

    private static void printChunk(final ChunkLine[] chunk) {
        for (ChunkLine line : chunk)
            System.out.println(
                    StringUtils.rightPad(line.keyword, 11, " ") + " " + line.value.replace("\n", "\n            "));
        System.out.println("///");
    }
}
