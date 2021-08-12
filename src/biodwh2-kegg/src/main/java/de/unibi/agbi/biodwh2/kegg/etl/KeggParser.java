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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeggParser extends Parser<KeggDataSource> {
    private static class ChunkLine {
        final String keyword;
        String value;

        public ChunkLine(final String keyword, final String value) {
            this.keyword = keyword;
            this.value = value;
        }
    }

    private static class ParsedReference {
        Reference reference;
        int lookAheadPosition;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(KeggParser.class);
    private static final Pattern REFERENCE_PATTERN = Pattern.compile(
            "PMID:([0-9]+)(([ \n\r]+\\(\\(?[a-zA-Z0-9/\n\r. ,_\\-]+\\)?\\))*)");
    private static final Pattern TARGET_IDS_PATTERN = Pattern.compile("\\[([A-Za-z]+:)([0-9A-Za-z]+( [0-9A-Za-z]+)*)]");
    private static final String END_OF_CHUNK = "///";

    private final List<String> loggedUnknownKeywords = new ArrayList<>();

    public KeggParser(final KeggDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        dataSource.variants = parseKeggFile(workspace, dataSource, Variant.class, KeggUpdater.VARIANT_FILE_NAME);
        dataSource.drugGroups = parseKeggFile(workspace, dataSource, DrugGroup.class, KeggUpdater.DGROUP_FILE_NAME);
        dataSource.drugs = parseKeggFile(workspace, dataSource, Drug.class, KeggUpdater.DRUG_FILE_NAME);
        dataSource.diseases = parseKeggFile(workspace, dataSource, Disease.class, KeggUpdater.DISEASE_FILE_NAME);
        dataSource.networks = parseKeggFile(workspace, dataSource, Network.class, KeggUpdater.NETWORK_FILE_NAME);
        return true;
    }

    private <T extends KeggEntry> List<T> parseKeggFile(final Workspace workspace, final DataSource dataSource,
                                                        final Class<T> entryClass,
                                                        final String fileName) throws ParserFormatException {
        final List<T> result = new ArrayList<>();
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(filePath));
            final List<ChunkLine> chunk = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.stripEnd(line, null);
                if (line.startsWith(END_OF_CHUNK)) {
                    result.add(processChunk(entryClass, chunk.toArray(new ChunkLine[0])));
                    chunk.clear();
                    continue;
                }
                final String keyword = StringUtils.stripEnd(line.substring(0, Math.min(12, line.length())), null);
                final String value = line.length() > 12 ? StringUtils.stripEnd(line.substring(12), null) : "";
                if (keyword.length() > 0)
                    chunk.add(new ChunkLine(keyword, value));
                else
                    chunk.get(chunk.size() - 1).value += '\n' + value;
            }
            reader.close();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse kegg file '" + filePath + "'", e);
        }
        return result;
    }

    private <T extends KeggEntry> T processChunk(final Class<T> entryClass, final ChunkLine[] chunk) {
        final T entry = createInstance(entryClass);
        if (entry == null)
            return null;
        for (int i = 0; i < chunk.length; i++) {
            final ChunkLine line = chunk[i];
            final boolean lineNotEmpty = line.value.trim().length() > 0;
            switch (line.keyword) {
                case "ENTRY":
                    final String[] parts = StringUtils.split(line.value, ' ');
                    entry.id = parts[0];
                    entry.tags.addAll(Arrays.asList(parts).subList(1, parts.length));
                    break;
                case "REFERENCE":
                    final ParsedReference reference = parseReference(chunk, i, line);
                    entry.references.add(reference.reference);
                    i = reference.lookAheadPosition;
                    break;
                case "DBLINKS":
                    if (lineNotEmpty)
                        processEntryDBLinks(entry, line.value);
                    break;
                case "COMMENT":
                    if (lineNotEmpty)
                        entry.comments.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                    break;
                case "REMARK":
                    if (lineNotEmpty)
                        entry.remarks.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                    break;
                default:
                    if (entryClass == Drug.class)
                        i = processDrugLine(chunk, line, i, (Drug) entry);
                    else if (entryClass == Variant.class)
                        i = processVariantLine(chunk, line, i, (Variant) entry);
                    else if (entryClass == Disease.class)
                        i = processDiseaseLine(chunk, line, i, (Disease) entry);
                    else if (entryClass == DrugGroup.class)
                        i = processDrugGroupLine(chunk, line, i, (DrugGroup) entry);
                    else if (entryClass == Network.class)
                        i = processNetworkGroupLine(chunk, line, i, (Network) entry);
                    break;
            }
        }
        moveATCRemarksToExternalIds(entry);
        return entry;
    }

    private static <T extends KeggEntry> T createInstance(final Class<T> entryClass) {
        try {
            return entryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to create instance of " + entryClass.getSimpleName() + " for parsing", e);
        }
        return null;
    }

    private <T extends KeggEntry> void processEntryDBLinks(final T entry, final String links) {
        for (final String link : StringUtils.split(links, "\n")) {
            final String[] linkParts = StringUtils.splitByWholeSeparator(link, ": ", 2);
            for (final String id : StringUtils.split(linkParts[1], ' '))
                entry.externalIds.add(linkParts[0] + ':' + id);
        }
    }

    private <T extends KeggEntry> void moveATCRemarksToExternalIds(final T entry) {
        for (int i = entry.remarks.size() - 1; i >= 0; i--) {
            final String remark = entry.remarks.get(i);
            if (remark.startsWith("ATC code:")) {
                final String codes = StringUtils.split(remark, ':')[1].trim();
                for (final String code : StringUtils.split(codes, ' '))
                    entry.externalIds.add("ATC:" + code);
                entry.remarks.remove(i);
            }
        }
    }

    private int processDrugLine(final ChunkLine[] chunk, final ChunkLine line, int i, final Drug entry) {
        final boolean lineNotEmpty = line.value.trim().length() > 0;
        switch (line.keyword) {
            case "NAME":
                for (String name : StringUtils.split(line.value, '\n'))
                    entry.names.add(StringUtils.stripEnd(name, ";"));
                for (int j = i + 1; j < chunk.length; j++) {
                    if (chunk[j].keyword.equals("  ABBR")) {
                        entry.nameAbbreviation = chunk[j].value.trim();
                        i++;
                    } else
                        break;
                }
                break;
            case "FORMULA":
                if (lineNotEmpty)
                    entry.formula = line.value.trim();
                break;
            case "EXACT_MASS":
                if (lineNotEmpty)
                    entry.exactMass = line.value.trim();
                break;
            case "MOL_WEIGHT":
                if (lineNotEmpty)
                    entry.molecularWeight = line.value.trim();
                break;
            case "ATOM":
                entry.atoms = line.value;
                break;
            case "BOND":
                entry.bonds = line.value;
                break;
            case "INTERACTION":
                if (lineNotEmpty)
                    entry.interactions.addAll(parseInteractions(line));
                break;
            case "TARGET":
                if (lineNotEmpty)
                    entry.targets.addAll(parseTargets(line));
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  NETWORK")) {
                        entry.networkTargets.add(parseIdNamePair(chunk[j].value));
                        i++;
                    } else
                        break;
                break;
            case "EFFICACY":
                if (lineNotEmpty)
                    entry.efficacy = line.value.trim();
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  DISEASE")) {
                        entry.efficacyDiseases.addAll(parseMultilineNameIdsPairs(chunk[j]));
                        i++;
                    } else
                        break;
                break;
            case "COMPONENT":
                if (lineNotEmpty)
                    entry.mixtures.addAll(parseDrugComponents(line));
                break;
            case "CLASS":
                if (lineNotEmpty)
                    entry.classes.addAll(parseMemberClassHierarchy(line));
                break;
            case "SEQUENCE":
                final Sequence sequence = new Sequence();
                sequence.sequence = line.value;
                if (chunk[i + 1].keyword.equals("  TYPE")) {
                    sequence.type = chunk[i + 1].value;
                    i++;
                }
                entry.sequences.add(sequence);
                break;
            case "BRACKET":
                // TODO: as bracket structures are not that easy to parse and low priority just store the FLAT-FILE info
                final StringBuilder bracket = new StringBuilder("BRACKET     ").append(
                        line.value.replace("\n", "\n            "));
                for (int j = i + 1; j < chunk.length; j++) {
                    if (chunk[j].keyword.equals("  ORIGINAL")) {
                        bracket.append("\n  ORIGINAL  ").append(chunk[j].value.replace("\n", "\n            "));
                        i++;
                    } else if (chunk[j].keyword.equals("  REPEAT")) {
                        bracket.append("\n  REPEAT    ").append(chunk[j].value.replace("\n", "\n            "));
                        i++;
                    } else
                        break;
                }
                entry.bracket = bracket.toString();
                break;
            case "METABOLISM":
                if (lineNotEmpty)
                    entry.metabolisms.addAll(parseMetabolisms(line));
                break;
            case "SOURCE":
                if (lineNotEmpty)
                    entry.sources.addAll(parseNameIdsPairs(line.value));
                break;
            default:
                logUnknownKeyword("Drug", line.keyword);
                break;
        }
        return i;
    }

    private void logUnknownKeyword(final String type, final String keyword) {
        if (LOGGER.isInfoEnabled() && !loggedUnknownKeywords.contains(type + ":" + keyword)) {
            LOGGER.info("Skipping unknown keyword '" + keyword + "' for " + type + " entry");
            loggedUnknownKeywords.add(type + ":" + keyword);
        }
    }

    private int processVariantLine(final ChunkLine[] chunk, final ChunkLine line, int i, final Variant entry) {
        final boolean lineNotEmpty = line.value.trim().length() > 0;
        switch (line.keyword) {
            case "NAME":
                entry.names.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                break;
            case "GENE":
                String[] geneRestParts = StringUtils.splitByWholeSeparator(line.value, "  ", 2);
                entry.genes.put(geneRestParts[0], parseNameIdsPair(geneRestParts[1]));
                break;
            case "ORGANISM":
                if (lineNotEmpty)
                    entry.organism = line.value;
                break;
            case "NETWORK":
                // TODO: validate
                final NetworkLink network = new NetworkLink();
                entry.networks.add(network);
                if (lineNotEmpty)
                    network.network = parseIdNamePair(line.value);
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  ELEMENT")) {
                        network.elements.addAll(parseMultilineIdNamePairs(chunk[j]));
                        i++;
                    } else
                        break;
                break;
            case "VARIATION":
                final NameIdsPair variation = new NameIdsPair();
                final String[] lines = StringUtils.split(line.value, '\n');
                variation.name = lines[0];
                for (int j = 1; j < lines.length; j++) {
                    final String[] idParts = StringUtils.split(lines[j], ' ');
                    for (int k = 1; k < idParts.length; k++)
                        variation.ids.add(idParts[0] + idParts[k]);
                }
                entry.variations.add(variation);
                break;
            case "DRUG_TARGET":
                // TODO: drug targets
                // DRUG_TARGET Gilteritinib (DG01948): D10800<JP/US>
                //             Midostaurin: D05029<US>
                break;
            default:
                logUnknownKeyword("Variant", line.keyword);
                break;
        }
        return i;
    }

    private int processDrugGroupLine(final ChunkLine[] chunk, final ChunkLine line, int i, final DrugGroup entry) {
        final boolean lineNotEmpty = line.value.trim().length() > 0;
        switch (line.keyword) {
            case "NAME":
                entry.names.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                for (int j = i + 1; j < chunk.length; j++) {
                    if (chunk[j].keyword.equals("  STEM")) {
                        entry.nameStems.addAll(Arrays.asList(StringUtils.splitByWholeSeparator(chunk[j].value, ", ")));
                        i++;
                    } else if (chunk[j].keyword.equals("  ABBR")) {
                        entry.nameAbbreviation = chunk[j].value.trim();
                        i++;
                    } else
                        break;
                }
                break;
            case "CLASS":
                if (lineNotEmpty)
                    entry.classes.addAll(parseMemberClassHierarchy(line));
                break;
            case "MEMBER":
                if (lineNotEmpty)
                    entry.members.addAll(parseMemberClassHierarchy(line));
                break;
            default:
                logUnknownKeyword("DrugGroup", line.keyword);
                break;
        }
        return i;
    }

    private int processDiseaseLine(final ChunkLine[] chunk, final ChunkLine line, int i, final Disease entry) {
        final boolean lineNotEmpty = line.value.trim().length() > 0;
        switch (line.keyword) {
            case "NAME":
                entry.names.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  SUPERGRP")) {
                        entry.superGroups.addAll(parseMultilineNameIdsPairs(chunk[j]));
                        i++;
                    } else if (chunk[j].keyword.equals("  SUBGROUP")) {
                        entry.subGroups.addAll(parseMultilineNameIdsPairs(chunk[j]));
                        i++;
                    } else
                        break;
                break;
            case "DESCRIPTION":
                if (lineNotEmpty)
                    entry.description = line.value;
                break;
            case "DRUG":
                if (lineNotEmpty)
                    entry.drugs.addAll(parseMultilineNameIdsPairs(line));
                break;
            case "ENV_FACTOR":
                if (lineNotEmpty)
                    entry.envFactors.addAll(parseMultilineNameIdsPairs(line));
                break;
            case "CARCINOGEN":
                if (lineNotEmpty)
                    entry.carcinogens.addAll(parseMultilineNameIdsPairs(line));
                break;
            case "PATHOGEN":
                if (lineNotEmpty)
                    entry.pathogens.addAll(parseMultilineNameIdsPairs(line));
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  MODULE")) {
                        entry.pathogenModules.addAll(parseMultilineIdNamePairs(chunk[j]));
                        i++;
                    } else
                        break;
                break;
            case "NETWORK":
                NetworkLink network = new NetworkLink();
                entry.networks.add(network);
                if (lineNotEmpty)
                    network.network = parseIdNamePair(line.value);
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  ELEMENT")) {
                        network.elements.addAll(parseMultilineIdNamePairs(chunk[j]));
                        i++;
                    } else
                        break;
                break;
            case "REL_PATHWAY":
                entry.relPathways.addAll(parseMultilineIdNamePairs(line));
                break;
            case "CATEGORY":
                if (lineNotEmpty)
                    for (String category : StringUtils.split(line.value, ';'))
                        entry.categories.add(category.trim());
                break;
            case "GENE":
                if (lineNotEmpty)
                    entry.genes.addAll(parseMultilineNameIdsPairs(line));
                break;
            default:
                logUnknownKeyword("Disease", line.keyword);
                break;
        }
        return i;
    }

    private int processNetworkGroupLine(final ChunkLine[] chunk, final ChunkLine line, int i, final Network entry) {
        final boolean lineNotEmpty = line.value.trim().length() > 0;
        switch (line.keyword) {
            case "NAME":
                entry.names.addAll(Arrays.asList(StringUtils.split(line.value, '\n')));
                break;
            case "TYPE":
                entry.type = line.value;
                break;
            case "GENE":
                if (lineNotEmpty)
                    entry.genes.addAll(parseMultilineIdNamePairs(line));
                break;
            case "CLASS":
                if (lineNotEmpty)
                    entry.classes.addAll(parseMultilineIdNamePairs(line));
                break;
            case "PERTURBANT":
                if (lineNotEmpty)
                    entry.perturbants.addAll(parseMultilineIdNamePairs(line));
                break;
            case "VARIANT":
                if (lineNotEmpty)
                    entry.variants.addAll(parseMultilineIdNamePairs(line));
                break;
            case "METABOLITE":
                if (lineNotEmpty)
                    entry.metabolites.addAll(parseMultilineIdNamePairs(line));
                break;
            case "DEFINITION":
                if (lineNotEmpty)
                    entry.definition = line.value;
                for (int j = i + 1; j < chunk.length; j++)
                    if (chunk[j].keyword.equals("  EXPANDED")) {
                        entry.expandedDefinition = chunk[j].value;
                        i++;
                    } else
                        break;
                break;
            case "DISEASE":
                if (lineNotEmpty)
                    entry.diseases.addAll(parseMultilineIdNamePairs(line));
                break;
            case "MEMBER":
                if (lineNotEmpty)
                    entry.members.addAll(parseMultilineIdNamePairs(line));
                break;
            case "MAP":
                // TODO: always referencing itself
                break;
            default:
                logUnknownKeyword("Network", line.keyword);
                break;
        }
        return i;
    }

    private static ParsedReference parseReference(final ChunkLine[] chunk, final int i, final ChunkLine line) {
        final ParsedReference parsedReference = new ParsedReference();
        final Reference reference = new Reference();
        if (line.value.trim().length() > 0) {
            final Matcher matcher = REFERENCE_PATTERN.matcher(line.value);
            if (matcher.matches()) {
                reference.pmid = Integer.parseInt(matcher.group(1));
                final String remarks = matcher.group(2).trim().replace('\n', ' ');
                if (remarks.length() > 0) {
                    reference.remarks = remarks;
                }
            } else
                reference.remarks = line.value;
        }
        for (int j = i + 1; j < chunk.length; j++) {
            final ChunkLine nextLine = chunk[j];
            if (!nextLine.keyword.startsWith("  "))
                break;
            parsedReference.lookAheadPosition = j;
            switch (nextLine.keyword) {
                case "  AUTHORS":
                    reference.authors = nextLine.value;
                    break;
                case "  TITLE":
                    reference.title = nextLine.value;
                    break;
                case "  JOURNAL":
                    final String[] parts = StringUtils.split(nextLine.value, '\n');
                    reference.journal = parts[0];
                    if (parts.length > 1) {
                        for (int k = 1; k < parts.length; k++) {
                            if (parts[k].startsWith("DOI:"))
                                reference.doi = parts[k].substring(4).trim();
                            else
                                LOGGER.warn("Unknown journal line in: " + nextLine.value);
                        }
                    }
                    break;
            }
        }
        parsedReference.reference = reference;
        return parsedReference;
    }

    private static List<Interaction> parseInteractions(final ChunkLine line) {
        final List<Interaction> result = new ArrayList<>();
        for (final String subLine : StringUtils.split(line.value.trim(), '\n')) {
            final String[] typeRestParts = StringUtils.split(subLine, ":", 2);
            for (final NameIdsPair target : parseNameIdsPairs(typeRestParts[1]))
                result.add(new Interaction(typeRestParts[0], target));
        }
        return result;
    }

    private static List<NameIdsPair> parseNameIdsPairs(final String line) {
        final List<NameIdsPair> result = new ArrayList<>();
        final String[] parts = StringUtils.split(line, ";,");
        for (final String pair : parts)
            result.add(parseNameIdsPair(pair));
        return result;
    }

    private static NameIdsPair parseNameIdsPair(final String line) {
        final NameIdsPair pair = new NameIdsPair();
        pair.name = line;
        int replaceOffset = 0;
        final Matcher matcher = TARGET_IDS_PATTERN.matcher(line);
        while (matcher.find()) {
            pair.name = pair.name.substring(0, matcher.start() - replaceOffset) + pair.name.substring(
                    matcher.end() - replaceOffset);
            replaceOffset += matcher.end() - matcher.start();
            final String prefix = matcher.group(1);
            final String[] ids = StringUtils.split(matcher.group(2), ' ');
            for (final String id : ids)
                pair.ids.add(prefix + id);
        }
        pair.name = pair.name.trim().replace("  ", " ");
        // Fix for data error in gene id
        if (pair.ids.size() == 0 && pair.name.equals("GST [K00799]")) {
            pair.name = "GST";
            pair.ids.add("KO:K00799");
        }
        return pair;
    }

    private static List<NameIdsPair> parseTargets(final ChunkLine line) {
        final List<NameIdsPair> result = new ArrayList<>();
        for (final String subLine : StringUtils.split(line.value.trim(), '\n'))
            result.add(parseNameIdsPair(subLine));
        return result;
    }

    private static List<Metabolism> parseMetabolisms(final ChunkLine line) {
        final List<Metabolism> result = new ArrayList<>();
        for (final String subLine : StringUtils.split(line.value.trim(), '\n')) {
            final String[] typeRestParts = StringUtils.split(subLine, ":", 2);
            for (final NameIdsPair target : parseNameIdsPairs(typeRestParts[1]))
                result.add(new Metabolism(typeRestParts[0], target));
        }
        return result;
    }

    private static List<NameIdsPair> parseMultilineNameIdsPairs(final ChunkLine line) {
        final List<NameIdsPair> result = new ArrayList<>();
        for (final String subLine : StringUtils.split(line.value.trim(), '\n'))
            result.add(parseNameIdsPair(subLine));
        return result;
    }

    private static List<NameIdsPair> parseMultilineIdNamePairs(final ChunkLine line) {
        final List<NameIdsPair> result = new ArrayList<>();
        for (final String subLine : StringUtils.split(line.value.trim(), '\n'))
            result.add((parseIdNamePair(subLine)));
        return result;
    }

    private static NameIdsPair parseIdNamePair(final String line) {
        String[] idRestParts = StringUtils.splitByWholeSeparator(line, "  ", 2);
        if (idRestParts.length == 1)
            idRestParts = StringUtils.split(line, " ", 2);
        final NameIdsPair pair = new NameIdsPair();
        pair.ids.add(idRestParts[0]);
        if (idRestParts.length > 1)
            pair.name = idRestParts[1];
        return pair;
    }

    private static List<ParentChildRelation> parseMemberClassHierarchy(final ChunkLine line) {
        final List<ParentChildRelation> result = new ArrayList<>();
        final Stack<NameIdsPair> depthStack = new Stack<>();
        int lastDepth = -1;
        for (String branch : StringUtils.split(line.value, '\n')) {
            final int depth = branch.length() - StringUtils.stripStart(branch, null).length();
            branch = branch.trim();
            final String[] idRestParts = StringUtils.splitByWholeSeparator(branch, "  ", 2);
            final NameIdsPair pair = new NameIdsPair();
            if (idRestParts.length > 1) {
                pair.name = idRestParts[1];
                pair.ids.add(idRestParts[0]);
            } else
                pair.name = branch;
            if (depth > lastDepth) {
                depthStack.push(pair);
            } else if (depth < lastDepth)
                while (depthStack.size() - 2 > depth)
                    depthStack.pop();
            lastDepth = depth;
            depthStack.set(depthStack.size() - 1, pair);
            final ParentChildRelation relation = new ParentChildRelation();
            if (depthStack.size() > 1) {
                relation.parent = depthStack.get(depthStack.size() - 2);
            }
            relation.child = pair;
            result.add(relation);
        }
        return result;
    }

    private static List<List<NameIdsPair>> parseDrugComponents(final ChunkLine line) {
        final List<List<NameIdsPair>> result = new ArrayList<>();
        final List<String[]> parts = new ArrayList<>();
        for (final String andPart : StringUtils.splitByWholeSeparator(line.value, ", "))
            parts.add(StringUtils.splitByWholeSeparator(StringUtils.strip(andPart, "()"), " | "));
        while (parts.size() > 1) {
            final String[] newPart = new String[parts.get(0).length * parts.get(1).length];
            int index = 0;
            for (final String a : parts.get(0)) {
                for (final String b : parts.get(1)) {
                    newPart[index] = a + ", " + b;
                    index++;
                }
            }
            parts.remove(1);
            parts.set(0, newPart);
        }
        for (final String mixture : parts.get(0)) {
            final List<NameIdsPair> components = new ArrayList<>();
            for (final String andPart : StringUtils.splitByWholeSeparator(mixture, ", "))
                components.add(parseNameIdsPair(andPart));
            result.add(components);
        }
        return result;
    }
}
