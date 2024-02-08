package de.unibi.agbi.biodwh2.negatome.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.negatome.NegatomeDataSource;

import java.util.HashMap;
import java.util.Map;

public class NegatomeUpdater extends Updater<NegatomeDataSource> {
    // private static final Pattern VERSION_PATTERN = Pattern.compile("<h1>The Negatome Database (\\d)\\.(\\d)</h1>");
    private static final String DOWNLOAD_URL_PREFIX = "http://mips.helmholtz-muenchen.de/proj/ppi/negatome/";
    static final String MANUAL_FILE_NAME = "manual.txt";
    static final String MANUAL_STRINGENT_FILE_NAME = "manual_stringent.txt";
    static final String MANUAL_PFAM_FILE_NAME = "manual_pfam.txt";
    static final String PDB_FILE_NAME = "pdb.txt";
    static final String PDB_STRINGENT_FILE_NAME = "pdb_stringent.txt";
    static final String PDB_PFAM_FILE_NAME = "pdb_pfam.txt";
    static final String COMBINED_FILE_NAME = "combined.txt";
    static final String COMBINED_STRINGENT_FILE_NAME = "combined_stringent.txt";
    static final String COMBINED_PFAM_FILE_NAME = "combined_pfam.txt";

    private final Map<String, String> fileNameWebArchivePrefixMap = new HashMap<>();

    public NegatomeUpdater(final NegatomeDataSource dataSource) {
        super(dataSource);
        fileNameWebArchivePrefixMap.put(MANUAL_FILE_NAME, "https://web.archive.org/web/20210928013259if_/");
        fileNameWebArchivePrefixMap.put(MANUAL_STRINGENT_FILE_NAME, "https://web.archive.org/web/20210928020542if_/");
        fileNameWebArchivePrefixMap.put(MANUAL_PFAM_FILE_NAME, "https://web.archive.org/web/20210928004434if_/");
        fileNameWebArchivePrefixMap.put(PDB_FILE_NAME, "https://web.archive.org/web/20210928022300if_/");
        fileNameWebArchivePrefixMap.put(PDB_STRINGENT_FILE_NAME, "https://web.archive.org/web/20210928003326if_/");
        fileNameWebArchivePrefixMap.put(PDB_PFAM_FILE_NAME, "https://web.archive.org/web/20210928011553if_/");
        fileNameWebArchivePrefixMap.put(COMBINED_FILE_NAME, "https://web.archive.org/web/20210928013752if_/");
        fileNameWebArchivePrefixMap.put(COMBINED_STRINGENT_FILE_NAME, "https://web.archive.org/web/20210928002854if_/");
        fileNameWebArchivePrefixMap.put(COMBINED_PFAM_FILE_NAME, "https://web.archive.org/web/20210928020359if_/");
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        // final String html = getWebsiteSource(DOWNLOAD_URL_PREFIX);
        // final Matcher matcher = VERSION_PATTERN.matcher(html);
        // if (!matcher.find())
        //     return null;
        // return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        // As long as Negatome is unavailable we use the fixed version 2.0 and the web archive files
        return new Version(2, 0);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames()) {
            final String webArchiveUrl = fileNameWebArchivePrefixMap.get(fileName) + DOWNLOAD_URL_PREFIX + fileName;
            downloadFileAsBrowser(workspace, webArchiveUrl, fileName);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                MANUAL_FILE_NAME, MANUAL_STRINGENT_FILE_NAME, MANUAL_PFAM_FILE_NAME, PDB_FILE_NAME,
                PDB_STRINGENT_FILE_NAME, PDB_PFAM_FILE_NAME
        };
    }
}
