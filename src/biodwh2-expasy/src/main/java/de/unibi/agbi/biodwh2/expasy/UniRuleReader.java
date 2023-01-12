package de.unibi.agbi.biodwh2.expasy;

import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileEntry;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UniRuleReader implements Iterable<UniRuleEntry>, AutoCloseable {
    private final FlatFileReader flatFileReader;
    private UniRuleEntry lastEntry;

    @SuppressWarnings("unused")
    public UniRuleReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public UniRuleReader(final InputStream stream, final Charset charset) {
        flatFileReader = new FlatFileReader(stream, charset);
    }

    @Override
    public Iterator<UniRuleEntry> iterator() {
        final Iterator<FlatFileEntry> flatFileIterator = flatFileReader.iterator();
        // Skip the header
        if (flatFileIterator.hasNext())
            flatFileIterator.next();
        return new Iterator<UniRuleEntry>() {
            @Override
            public boolean hasNext() {
                if (lastEntry == null && flatFileIterator.hasNext())
                    lastEntry = readNextEntry(flatFileIterator.next());
                return lastEntry != null;
            }

            @Override
            public UniRuleEntry next() {
                final UniRuleEntry entry = lastEntry;
                lastEntry = null;
                return entry;
            }
        };
    }

    private UniRuleEntry readNextEntry(final FlatFileEntry entry) {
        // Remove empty blocks that sometimes occur as two consecutive XX keys
        for (int i = entry.properties.size() - 1; i >= 0; i--)
            if (entry.properties.get(i).size() == 0)
                entry.properties.remove(i);
        final UniRuleEntry result = new UniRuleEntry();
        final String[] accessions = collectSemicolonValues(entry.properties.get(0), "AC");
        result.accession = accessions[0];
        if (accessions.length > 1)
            result.secondaryAccessions = Arrays.copyOfRange(accessions, 1, accessions.length);
        final String[] dataClasses = collectSemicolonValues(entry.properties.get(0), "DC");
        result.dataClasses = new UniRuleDataClass[dataClasses.length];
        for (int i = 0; i < dataClasses.length; i++)
            result.dataClasses[i] = UniRuleDataClass.fromValue(dataClasses[i]);
        readNextEntryTriggers(entry, result);
        readNextEntryNamesAndFunctions(entry, result);
        readNextEntryComputingSection(entry, result);
        final String[] revisions = collectTypeValues(entry.properties.get(entry.properties.size() - 1), "#");
        result.revisions = revisions;
        // TODO
        return result;
    }

    private String[] collectSemicolonValues(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
        final List<String> result = new ArrayList<>();
        for (final FlatFileEntry.KeyValuePair pair : properties)
            if (key.equals(pair.key))
                for (final String part : StringUtils.split(pair.value, ';'))
                    result.add(part.trim());
        return result.toArray(new String[0]);
    }

    private String[] collectTypeValues(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
        return properties.stream().filter((p) -> key.equals(p.key)).map((p) -> p.value.trim()).toArray(String[]::new);
    }

    private void readNextEntryTriggers(final FlatFileEntry entry, final UniRuleEntry result) {
        final String[] triggers = collectTypeValues(entry.properties.get(0), "TR");
        final List<String> metaMotifs = new ArrayList<>();
        final List<UniRuleTrigger> uniRuleTriggers = new ArrayList<>();
        for (final String trigger : triggers) {
            final String[] triggerParts = StringUtils.split(trigger, ';');
            final String dbName = triggerParts[0].trim();
            if ("Metamotif".equals(dbName)) {
                // triggerParts[1] is mmSearchOptions which is now obsolete (-)
                metaMotifs.add(triggerParts[2].trim());
            } else {
                final UniRuleTrigger uniRuleTrigger = new UniRuleTrigger();
                uniRuleTrigger.dbName = dbName;
                uniRuleTrigger.identifier1 = triggerParts[1].trim();
                uniRuleTrigger.identifier2 = triggerParts[2].trim();
                uniRuleTrigger.numHits = triggerParts[3].trim();
                uniRuleTrigger.level = triggerParts[4].trim();
                uniRuleTriggers.add(uniRuleTrigger);
            }
        }
        if (metaMotifs.size() > 0)
            result.metaMotifs = metaMotifs.toArray(new String[0]);
        if (uniRuleTriggers.size() > 0)
            result.triggers = uniRuleTriggers.toArray(new UniRuleTrigger[0]);
    }

    private void readNextEntryNamesAndFunctions(final FlatFileEntry entry, final UniRuleEntry result) {
        List<FlatFileEntry.KeyValuePair> pairs = entry.properties.get(1);
        final String[] nameValues = collectTypeValues(pairs, "Names:");
        final String[] names = StringUtils.split(nameValues[0], '\n');
        result.name = "Undefined".equals(names[0]) ? null : names[0];
        if (names.length > 1)
            result.synonyms = Arrays.copyOfRange(names, 1, names.length);
        String[] functions = collectTypeValues(pairs, "Function:");
        if (functions.length == 0) {
            // In rare cases the space after "Function:" is missing, so the first word until a space is attached to
            // the key-value pair key.
            functions = pairs.stream().filter((p) -> p.key.startsWith("Function:")).map(
                    (p) -> p.key.substring("Function:".length()) + p.value).toArray(String[]::new);
        }
        if (functions.length > 0) {
            for (int i = 0; i < functions.length; i++)
                functions[i] = StringUtils.replace(functions[i], "\n", " ");
            if (functions.length > 1 || !"Undefined".equals(functions[0]))
                result.functions = functions;
        }
    }

    private void readNextEntryComputingSection(final FlatFileEntry entry, final UniRuleEntry result) {
        List<FlatFileEntry.KeyValuePair> pairs = entry.properties.get(3);
        for (int i = 0; i < pairs.size(); i++) {
            // Warn: text
            // Chop: Nter=max; Cter=max;[ Xter(motif)=max;]*
            // Size: minimal_size-maximal_size;
            // Size: fixed_size;
            // Related: None;
            // Related: Protein[!][!];[ Protein[!][!];]...
            // Repeats: value;[ no keyword;]
            // Repeats: min-max;
            // Topology: [Undefined|Cytoplasmic|Not cytoplasmic];
            // Template: accession_number;[ accession number;]...
            // Template: None;
            // Template: Undefined;
            // Example: accession_number;[ accession number;]...
            // Example: Undefined;
            // TODO Scope:
            // TODO Fusion:
            // Duplicate: None
            // Duplicate: in taxcode[, taxcode]...
            // Plasmid: None
            // Plasmid: in taxcode[, taxcode]...
            // Comments: None
            // Comments: comment_text
        }
        // TODO
    }

    @Override
    public void close() throws IOException {
        flatFileReader.close();
    }
}
