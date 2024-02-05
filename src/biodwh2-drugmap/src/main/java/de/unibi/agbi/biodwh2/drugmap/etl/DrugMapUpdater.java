package de.unibi.agbi.biodwh2.drugmap.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.drugmap.DrugMapDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrugMapUpdater extends Updater<DrugMapDataSource> {
    private static final String VERSION_URL = "http://drugmap.idrblab.net/full-data-download";
    private static final String DOWNLOAD_URL_PREFIX = "http://drugmap.idrblab.net/sites/default/files/download/";
    static final String DRUGS_FILE_NAME = "01-General-Information-of-Drug.txt";
    static final String THERAPEUTIC_TARGETS_FILE_NAME = "02-General-Information-of-Drug-Therapeutic-Target.txt";
    static final String TRANSPORTERS_FILE_NAME = "03-General-Information-of-Drug-Transporter.txt";
    static final String ENZYMES_FILE_NAME = "04-General-Information-of-Drug-Metabolizing-Enzyme.txt";
    static final String DRUG_DTT_FILE_NAME = "06-Drug-to-DTT-Mapping-Information.txt";
    static final String DRUG_DTP_FILE_NAME = "07-Drug-to-DTP-Mapping-Information.txt";
    static final String DRUG_DME_FILE_NAME = "08-Drug-to-DME-Mapping-Information.txt";

    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "(" + String.join("|", TextUtils.THREE_LETTER_MONTH_NAMES) + ") ([1-2]?[0-9])(st|nd|rd|th), ([0-9]{4})",
            Pattern.CASE_INSENSITIVE);

    public DrugMapUpdater(final DrugMapDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL, 5);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            return new Version(Integer.parseInt(matcher.group(4)),
                               TextUtils.threeLetterMonthNameToInt(matcher.group(1).toLowerCase()),
                               Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + StringUtils.replace(fileName, " ", "%20"), fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                DRUGS_FILE_NAME, THERAPEUTIC_TARGETS_FILE_NAME, TRANSPORTERS_FILE_NAME, ENZYMES_FILE_NAME,
                DRUG_DTT_FILE_NAME, DRUG_DTP_FILE_NAME, DRUG_DME_FILE_NAME
        };
    }
}
