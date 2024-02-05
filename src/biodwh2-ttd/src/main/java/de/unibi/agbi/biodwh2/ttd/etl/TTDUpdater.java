package de.unibi.agbi.biodwh2.ttd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.ttd.TTDDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TTDUpdater extends Updater<TTDDataSource> {
    private static final String VERSION_URL = "http://db.idrblab.net/ttd/full-data-download";
    private static final String DOWNLOAD_URL_PREFIX = "http://db.idrblab.net/ttd/sites/default/files/ttd_database/";
    static final String DRUG_SDF_FILE_NAME = "P3-01-All.sdf";
    static final String KEGG_PATHWAY_TO_TARGET_TSV = "P4-01-Target-KEGGpathway_all.txt";
    static final String WIKI_PATHWAY_TO_TARGET_TSV = "P4-06-Target-wikipathway_all.txt";
    static final String TARGET_RAW_FLAT_FILE = "P1-01-TTD_target_download.txt";
    static final String DRUG_RAW_FLAT_FILE = "P1-02-TTD_drug_download.txt";
    static final String DRUG_CROSSREF_FLAT_FILE = "P1-03-TTD_crossmatching.txt";
    static final String DRUG_SYNONYMS_FLAT_FILE = "P1-04-Drug_synonyms.txt";
    static final String TARGET_UNIPORT_FLAT_FILE = "P2-01-TTD_uniprot_all.txt";
    static final String BIOMARKER_DISEASE_TSV = "P1-08-Biomarker_disease.txt";
    static final String TARGET_DISEASE_FLAT_FILE = "P1-06-Target_disease.txt";
    static final String DRUG_DISEASE_FLAT_FILE = "P1-05-Drug_disease.txt";
    static final String TARGET_COMPOUND_ACTIVITY_TSV = "P1-09-Target_compound_activity.txt";
    static final String TARGET_COMPOUND_MAPPIN_XLSX = "P1-07-Drug-TargetMapping.xlsx";
    static final String SEQUENCE_ALL_FILE_NAME = "P2-06-TTD_sequence_all.txt";
    private static final String[] FILE_NAMES = {
            TARGET_RAW_FLAT_FILE, DRUG_RAW_FLAT_FILE, DRUG_CROSSREF_FLAT_FILE, DRUG_SYNONYMS_FLAT_FILE,
            DRUG_DISEASE_FLAT_FILE, TARGET_DISEASE_FLAT_FILE, TARGET_COMPOUND_MAPPIN_XLSX, BIOMARKER_DISEASE_TSV,
            TARGET_COMPOUND_ACTIVITY_TSV, TARGET_UNIPORT_FLAT_FILE, SEQUENCE_ALL_FILE_NAME, DRUG_SDF_FILE_NAME,
            KEGG_PATHWAY_TO_TARGET_TSV, WIKI_PATHWAY_TO_TARGET_TSV
    };

    private final Pattern versionPattern = Pattern.compile(
            "<td>\\s(" + String.join("|", TextUtils.MONTH_NAMES) + ") ([1-2]?[0-9])(st|nd|rd|th), ([0-9]{4})",
            Pattern.CASE_INSENSITIVE);

    public TTDUpdater(final TTDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL, 5);
        final Matcher matcher = versionPattern.matcher(source);
        if (matcher.find()) {
            return new Version(Integer.parseInt(matcher.group(4)),
                               TextUtils.monthNameToInt(matcher.group(1).toLowerCase()),
                               Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (String fileName : FILE_NAMES)
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
