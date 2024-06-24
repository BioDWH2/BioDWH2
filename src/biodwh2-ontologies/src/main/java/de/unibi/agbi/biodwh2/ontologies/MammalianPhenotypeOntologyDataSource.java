package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.io.GithubUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.github.GithubAsset;
import de.unibi.agbi.biodwh2.core.model.github.GithubRelease;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class MammalianPhenotypeOntologyDataSource extends SingleOBOOntologyDataSource {
    static final String FILE_NAME = "mp-full.obo";

    @Override
    public String getId() {
        return "MammalianPhenotypeOntology";
    }

    @Override
    public String getFullName() {
        return "Mammalian Phenotype Ontology (MP)";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        final GithubRelease release = GithubUtils.getLatestRelease("mgijax", "mammalian-phenotype-ontology");
        if (release != null)
            for (final GithubAsset asset : release.assets)
                if (FILE_NAME.equals(asset.name))
                    return asset.browserDownloadUrl;
        return null;
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = DASHED_YYYY_MM_DD_VERSION_PATTERN.matcher(dataVersion);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        return null;
    }

    @Override
    public String getIdPrefix() {
        return "MP";
    }
}
