package de.unibi.agbi.biodwh2.iig.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.iig.IIGDataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IIGUpdater extends Updater<IIGDataSource> {
    private static final String VERSION_URL = "https://www.fda.gov/drugs/drug-approvals-and-databases/inactive-ingredients-database-download";
    static final String FILE_NAME = "iig.zip";
    private static Pattern VERSION_PATTERN = Pattern.compile(
            "(" + String.join("|", TextUtils.MONTH_NAMES) + ") ([0-9]{4}): Inactive Ingredient Database File",
            Pattern.CASE_INSENSITIVE);

    private String downloadUrl;

    public IIGUpdater(final IIGDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Document document = Jsoup.parse(source);
        downloadUrl = null;
        Version newestVersion = null;
        for (final Element link : document.getElementsByTag("a")) {
            if (!link.hasAttr("title"))
                continue;
            final Matcher matcher = VERSION_PATTERN.matcher(link.attr("title"));
            if (matcher.find()) {
                final var version = new Version(Integer.parseInt(matcher.group(2)),
                                                TextUtils.monthNameToInt(matcher.group(1).toLowerCase()));
                if (newestVersion == null || newestVersion.compareTo(version) < 0) {
                    newestVersion = version;
                    downloadUrl = "https://www.fda.gov/" + link.attr("href");
                }
            }
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        if (downloadUrl == null)
            return false;
        downloadFileAsBrowser(workspace, downloadUrl, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
