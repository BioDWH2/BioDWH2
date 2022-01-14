package de.unibi.agbi.biodwh2.adrecs.etl;

import de.unibi.agbi.biodwh2.adrecs.ADReCSDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADReCSUpdater extends Updater<ADReCSDataSource> {
    private static final Pattern UPDATE_DATE_PATTERN = Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]{2}");
    static final String FILE_NAME = "DRUG_ADR.zip";

    public ADReCSUpdater(final ADReCSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion() throws UpdaterException {
        Version version = null;
        try {
            final String source = HTTPClient.getWebsiteSource("http://bioinf.xmu.edu.cn/ADReCS/update.jsp");
            final Matcher matcher = UPDATE_DATE_PATTERN.matcher(source);
            while (matcher.find()) {
                final Version v = Version.tryParse(StringUtils.replace(matcher.group(0), "/", "."));
                if (v != null && (version == null || v.compareTo(version) > 0))
                    version = v;
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return version;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser("http://bioinf.xmu.edu.cn/ADReCS/download/" + FILE_NAME,
                                             dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + FILE_NAME + "'", e);
        }
        return true;
    }
}
