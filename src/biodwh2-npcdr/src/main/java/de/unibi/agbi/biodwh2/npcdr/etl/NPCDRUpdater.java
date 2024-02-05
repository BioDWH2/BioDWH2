package de.unibi.agbi.biodwh2.npcdr.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.npcdr.NPCDRDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPCDRUpdater extends Updater<NPCDRDataSource> {
    private static final String VERSION_URL = "http://npcdr.idrblab.net/drnpc-download";
    private static final String DOWNLOAD_URL_PREFIX = "http://npcdr.idrblab.net/sites/default/files/downloads/";
    static final String PAIR_INFO_FILE_NAME = "Pair_information.txt";
    static final String EFFECT_EXPERIMENT_FILE_NAME = "Effect_and_Experiment_Model_of_Combination.txt";
    static final String MOLECULE_REGULATION_TYPE_FILE_NAME = "Molecule_Regulation_Type_and_Infomation.txt";
    static final String CELL_LINE_FILE_NAME = "Cell_Line.txt";
    static final String MOLECULE_REGULATED_FILE_NAME = "Molecule_Regulated_by_Combination.txt";
    static final String CLINICAL_ICD_FILE_NAME = "Clinical_and_ICD_of_Combination.txt";
    static final String NP_FILE_NAME = "NP_Information.txt";
    static final String NP_SOURCE_FILE_NAME = "Natural_Product_Source.txt";
    static final String NP_TARGET_FILE_NAME = "NP_Target.txt";
    static final String NP_CLINICAL_ICD_FILE_NAME = "Clinical_and_ICD_of_NP.txt";
    static final String DRUG_FILE_NAME = "Drug_Information.txt";
    static final String DRUG_TARGET_FILE_NAME = "Drug_Target.txt";
    static final String DRUG_CLINICAL_ICD_FILE_NAME = "Clinical_and_ICD_of_Drug.txt";
    private static final String[] FILE_NAMES = {
            PAIR_INFO_FILE_NAME, EFFECT_EXPERIMENT_FILE_NAME, MOLECULE_REGULATION_TYPE_FILE_NAME, CELL_LINE_FILE_NAME,
            MOLECULE_REGULATED_FILE_NAME, CLINICAL_ICD_FILE_NAME, NP_FILE_NAME, NP_SOURCE_FILE_NAME,
            NP_TARGET_FILE_NAME, NP_CLINICAL_ICD_FILE_NAME, DRUG_FILE_NAME, DRUG_TARGET_FILE_NAME,
            DRUG_CLINICAL_ICD_FILE_NAME
    };

    private final Pattern versionPattern = Pattern.compile(
            "\\s(" + String.join("|", TextUtils.MONTH_NAMES) + ") ([1-2]?[0-9])(?:st|nd|rd|th), ([0-9]{4})",
            Pattern.CASE_INSENSITIVE);

    public NPCDRUpdater(final NPCDRDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = versionPattern.matcher(source);
        if (matcher.find()) {
            return new Version(Integer.parseInt(matcher.group(3)),
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
