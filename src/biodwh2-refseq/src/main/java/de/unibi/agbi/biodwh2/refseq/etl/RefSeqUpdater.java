package de.unibi.agbi.biodwh2.refseq.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.refseq.RefSeqDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefSeqUpdater extends MultiFileFTPWebUpdater<RefSeqDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("GCF_\\d+\\.(\\d+)_.+");
    private static final Pattern FEATURE_TABLE_PATTERN = Pattern.compile(".+_feature_table\\.txt\\.gz");
    private static final String HOMO_SAPIENS_ASSEMBLIES_PATH = "vertebrate_mammalian/Homo_sapiens/all_assembly_versions/";

    public RefSeqUpdater(final RefSeqDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ncbi.nlm.nih.gov/genomes/refseq/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) throws UpdaterException {
        final String assemblyPath = findAssemblyPath(workspace);
        try {
            final HTTPFTPClient.Entry[] assemblyFiles = client.listDirectory(assemblyPath);
            for (final HTTPFTPClient.Entry file : assemblyFiles) {
                final Matcher matcher = FEATURE_TABLE_PATTERN.matcher(file.name);
                if (matcher.find())
                    return new String[]{assemblyPath + '/' + file.name};
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        throw new UpdaterConnectionException("Could not find files on RefSeq FTP");
    }

    private String findAssemblyPath(final Workspace workspace) throws UpdaterException {
        String assemblyPath;
        final String desiredAssembly = dataSource.getProperties(workspace).get("assembly");
        try {
            final HTTPFTPClient.Entry[] assemblies = client.listDirectory(HOMO_SAPIENS_ASSEMBLIES_PATH);
            if (desiredAssembly != null)
                assemblyPath = findDesiredAssembly(assemblies, desiredAssembly);
            else
                assemblyPath = findNewestAssembly(assemblies);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        if (assemblyPath == null) {
            if (desiredAssembly == null)
                throw new UpdaterConnectionException("Could not find newest assembly on RefSeq FTP");
            else
                throw new UpdaterConnectionException("Could not find desired assembly on RefSeq FTP");
        }
        return assemblyPath;
    }

    private String findDesiredAssembly(final HTTPFTPClient.Entry[] assemblies, final String desiredAssembly) {
        for (final HTTPFTPClient.Entry assembly : assemblies)
            if (assembly.name.contains(desiredAssembly))
                return HOMO_SAPIENS_ASSEMBLIES_PATH + assembly.name;
        return null;
    }

    private String findNewestAssembly(final HTTPFTPClient.Entry[] assemblies) {
        Integer maxVersion = null;
        HTTPFTPClient.Entry maxAssembly = null;
        for (final HTTPFTPClient.Entry assembly : assemblies) {
            final Matcher matcher = VERSION_PATTERN.matcher(assembly.name);
            if (matcher.find()) {
                final int version = Integer.parseInt(matcher.group(1));
                if (maxVersion == null || version > maxVersion) {
                    maxVersion = version;
                    maxAssembly = assembly;
                }
            }
        }
        return maxAssembly == null ? null : HOMO_SAPIENS_ASSEMBLIES_PATH + maxAssembly.name;
    }
}
