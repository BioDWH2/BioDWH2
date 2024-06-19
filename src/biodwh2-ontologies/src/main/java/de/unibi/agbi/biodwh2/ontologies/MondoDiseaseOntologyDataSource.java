package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.io.GithubUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.github.GithubAsset;
import de.unibi.agbi.biodwh2.core.model.github.GithubRelease;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MondoDiseaseOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final String FILE_NAME = "mondo.obo";

    @Override
    public String getId() {
        return "MondoDiseaseOntology";
    }

    @Override
    public String getFullName() {
        return "Mondo Disease Ontology (Mondo)";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        final GithubRelease release = GithubUtils.getLatestRelease("monarch-initiative", "mondo");
        if (release != null)
            for (final GithubAsset asset : release.assets)
                if (FILE_NAME.equals(asset.name))
                    return asset.browserDownloadUrl;
        return "https://github.com/monarch-initiative/mondo/releases/latest/download/" + FILE_NAME;
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = DATE_PATTERN.matcher(dataVersion);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        return null;
    }

    @Override
    public String getIdPrefix() {
        return "MONDO";
    }
}
