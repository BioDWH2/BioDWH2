package de.unibi.agbi.biodwh2.negatome.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.negatome.NegatomeDataSource;
import de.unibi.agbi.biodwh2.negatome.model.PfamPair;
import de.unibi.agbi.biodwh2.negatome.model.ProteinPair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class NegatomeParser extends Parser<NegatomeDataSource> {
    public NegatomeParser(final NegatomeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        try {
            parseManualEntries(workspace);
            parsePDBEntries(workspace);
        } catch (IOException e) {
            throw new ParserFormatException(e);
        }
        return true;
    }

    private void parseManualEntries(final Workspace workspace) throws IOException {
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.MANUAL_FILE_NAME)) {
            if (entry[0].startsWith("#"))
                continue;
            final ProteinPair pair = getOrCreateProteinPair(entry[0], entry[1]);
            if (entry[2].startsWith("PMC"))
                pair.manualPmcid = entry[2];
            else
                pair.manualPmid = Integer.parseInt(entry[2]);
            pair.manualEvidence = entry[3];
            pair.isManual = true;
        }
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.MANUAL_STRINGENT_FILE_NAME)) {
            if (entry[0].startsWith("#"))
                continue;
            final ProteinPair pair = getOrCreateProteinPair(entry[0], entry[1]);
            if (entry[2].startsWith("PMC")) {
                if (pair.manualPmcid == null)
                    pair.manualPmcid = entry[2];
            } else {
                if (pair.manualPmid == null)
                    pair.manualPmid = Integer.parseInt(entry[2]);
            }
            if (pair.manualEvidence == null)
                pair.manualEvidence = entry[3];
            pair.isManualStringent = true;
        }
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.MANUAL_PFAM_FILE_NAME))
            getOrCreatePfamPair(entry[0], entry[1]).isManual = true;
    }

    private Iterable<String[]> parseTsvFile(final Workspace workspace, final String fileName) throws IOException {
        final MappingIterator<String[]> entries = FileUtils.openTsv(workspace, dataSource, fileName, String[].class);
        return () -> entries;
    }

    private ProteinPair getOrCreateProteinPair(final String a, final String b) {
        final String key = buildProteinPairKey(a, b);
        if (!dataSource.proteinPairs.containsKey(key)) {
            final ProteinPair pair = new ProteinPair();
            pair.uniProtId1 = a;
            pair.uniProtId2 = b;
            dataSource.proteinPairs.put(key, pair);
        }
        return dataSource.proteinPairs.get(key);
    }

    private String buildProteinPairKey(final String a, final String b) {
        return a.compareTo(b) <= 0 ? a + ";" + b : b + ";" + a;
    }

    private PfamPair getOrCreatePfamPair(final String a, final String b) {
        final String key = buildPfamPairKey(a, b);
        if (!dataSource.pfamPairs.containsKey(key)) {
            final PfamPair pair = new PfamPair();
            pair.pfamId1 = a;
            pair.pfamId2 = b;
            dataSource.pfamPairs.put(key, pair);
        }
        return dataSource.pfamPairs.get(key);
    }

    private String buildPfamPairKey(final String a, final String b) {
        return a.compareTo(b) <= 0 ? a + ";" + b : b + ";" + a;
    }

    private void parsePDBEntries(final Workspace workspace) throws IOException {
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.PDB_FILE_NAME)) {
            if (entry[0].startsWith("#"))
                continue;
            final ProteinPair pair = getOrCreateProteinPair(entry[0], entry[1]);
            pair.pdbCodes = StringUtils.split(entry[2], ',');
            pair.pdbEvidence = entry[3];
            pair.isPDB = true;
        }
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.PDB_STRINGENT_FILE_NAME)) {
            if (entry[0].startsWith("#"))
                continue;
            final ProteinPair pair = getOrCreateProteinPair(entry[0], entry[1]);
            if (pair.pdbCodes == null)
                pair.pdbCodes = StringUtils.split(entry[2], ',');
            if (pair.pdbEvidence == null)
                pair.pdbEvidence = entry[3];
            pair.isPDBStringent = true;
        }
        for (final String[] entry : parseTsvFile(workspace, NegatomeUpdater.PDB_PFAM_FILE_NAME))
            getOrCreatePfamPair(entry[0], entry[1]).isPDB = true;
    }
}
