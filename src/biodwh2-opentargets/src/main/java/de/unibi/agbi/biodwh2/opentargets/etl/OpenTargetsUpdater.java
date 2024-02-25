package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenTargetsUpdater extends Updater<OpenTargetsDataSource> {
    private static final String FTP_VERSIONS_URL = "http://ftp.ebi.ac.uk/pub/databases/opentargets/platform/";
    private static final String FTP_BASE_URL = "https://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/";
    static final String BASELINE_EXPRESSION_FILE_NAME = "baselineExpression.parquet.zip";
    static final String DISEASE_TO_PHENOTYPE_FILE_NAME = "diseaseToPhenotype.parquet.zip";
    static final String DISEASES_FILE_NAME = "diseases.parquet.zip";
    static final String DRUG_WARNINGS_FILE_NAME = "drugWarnings.parquet.zip";
    static final String FDA_ADR_FILE_NAME = "fda_significantAdverseDrugReactions.parquet.zip";
    static final String FDA_ATR_FILE_NAME = "fda_significantAdverseTargetReactions.parquet.zip";
    static final String EPMC_COOCCURRENCES_FILE_NAME = "epmcCooccurrences.parquet.zip";
    static final String EXPRESSION_SPECIFICITY_FILE_NAME = "expressionSpecificity.parquet.zip";
    static final String PHARMACOGENOMICS_FILE_NAME = "pharmacogenomics.parquet.zip";
    static final String TARGET_ESSENTIALITY_FILE_NAME = "targetEssentiality.parquet.zip";
    static final String TARGET_PRIORITISATION_FILE_NAME = "targetPrioritisation.parquet.zip";
    static final String GO_FILE_NAME = "go.parquet.zip";
    static final String HPO_FILE_NAME = "hpo.parquet.zip";
    static final String INDICATION_FILE_NAME = "indication.parquet.zip";
    static final String INTERACTION_FILE_NAME = "interaction.parquet.zip";
    static final String INTERACTION_EVIDENCE_FILE_NAME = "interactionEvidence.parquet.zip";
    static final String MECHANISM_OF_ACTION_FILE_NAME = "mechanismOfAction.parquet.zip";
    static final String MOLECULE_FILE_NAME = "molecule.parquet.zip";
    static final String MOUSE_PHENOTYPES_FILE_NAME = "mousePhenotypes.parquet.zip";
    static final String REACTOME_FILE_NAME = "reactome.parquet.zip";
    static final String TARGETS_FILE_NAME = "targets.parquet.zip";

    private static final Pattern VERSION_PATTERN = Pattern.compile("([1-9][0-9]*)\\.([0-9]+)(?:\\.([0-9]+))?/");

    public OpenTargetsUpdater(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(FTP_VERSIONS_URL);
        try {
            Version latestVersion = null;
            for (final var entry : client.listDirectory()) {
                final Matcher matcher = VERSION_PATTERN.matcher(entry.name);
                if (matcher.find()) {
                    final Version version;
                    if (matcher.groupCount() == 4)
                        version = new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                              Integer.parseInt(matcher.group(3)));
                    else
                        version = new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    if (latestVersion == null || version.compareTo(latestVersion) > 0)
                        latestVersion = version;
                }
            }
            return latestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(FTP_BASE_URL);
        // Not used: AOTFClickhouse, AOTFElasticsearch, errors, ebisearchAssociations, ebisearchEvidence,
        // searchDisease, searchDrug, searchTarget, associationByDatasourceDirect,
        // associationByDatasourceIndirect, associationByDatatypeDirect, associationByDatatypeIndirect,
        // associationByOverallDirect, associationByOverallIndirect, evidence
        downloadFiles(workspace, client, "baselineExpression");
        downloadFiles(workspace, client, "diseaseToPhenotype");
        downloadFiles(workspace, client, "diseases");
        downloadFiles(workspace, client, "drugWarnings");
        downloadFiles(workspace, client, "fda/significantAdverseDrugReactions");
        downloadFiles(workspace, client, "fda/significantAdverseTargetReactions");
        downloadFiles(workspace, client, "go");
        downloadFiles(workspace, client, "hpo");
        downloadFiles(workspace, client, "indication");
        downloadFiles(workspace, client, "interaction");
        downloadFiles(workspace, client, "interactionEvidence");
        downloadFiles(workspace, client, "knownDrugsAggregated");
        downloadFiles(workspace, client, "mechanismOfAction");
        downloadFiles(workspace, client, "molecule");
        downloadFiles(workspace, client, "mousePhenotypes");
        downloadFiles(workspace, client, "reactome");
        downloadFiles(workspace, client, "targets");
        downloadFiles(workspace, client, "epmcCooccurrences");
        downloadFiles(workspace, client, "expressionSpecificity");
        downloadFiles(workspace, client, "pharmacogenomics");
        downloadFiles(workspace, client, "targetEssentiality");
        downloadFiles(workspace, client, "targetPrioritisation");
        return true;
    }

    private void downloadFiles(final Workspace workspace, final HTTPFTPClient client,
                               final String directoryName) throws UpdaterConnectionException {
        final String fileName = StringUtils.replace(directoryName, "/", "_") + ".parquet.zip";
        final HTTPFTPClient.Entry[] entries;
        try {
            entries = client.listDirectory(directoryName);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
        }
        final Path targetFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
        final int maxPartNumber = Arrays.stream(entries).map(e -> e.name).filter(n -> n.startsWith("part-")).map(
                n -> Integer.parseInt(StringUtils.split(n, '-')[1])).max(Integer::compareTo).orElse(0) + 1;
        final int maxPartNumberLength = String.valueOf(maxPartNumber).length();
        final var backspaceString = "\b".repeat(maxPartNumberLength * 2 + 10);
        final int[] rotateIndex = {0};
        final int[] partIndex = {1};
        final long[] lastTime = {System.currentTimeMillis()};
        System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        System.out.print(" [INFO ] " + getClass().getName() + " - Downloading file '" + fileName + "' ");
        System.out.print("in " + maxPartNumber + " parts " + ROTATE_CHARS[0] + ' ' +
                         String.format("%1$" + maxPartNumberLength + "s", 1) + "/" + maxPartNumber + " [  0%]");
        try (final var outputStream = new ZipArchiveOutputStream(FileUtils.openOutput(targetFilePath))) {
            for (final HTTPFTPClient.Entry entry : entries) {
                if (entry.name.endsWith(".parquet")) {
                    final var stream = HTTPClient.getUrlInputStream(entry.fullUrl);
                    final var zipEntry = new ZipArchiveEntry(entry.name);
                    zipEntry.setSize(stream.contentLength);
                    outputStream.putArchiveEntry(zipEntry);
                    HTTPClient.downloadStream(stream, outputStream, (position, length) -> {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTime[0] > 500) {
                            rotateIndex[0] = (rotateIndex[0] + 1) % ROTATE_CHARS.length;
                            lastTime[0] += 1000;
                        }
                        System.out.print(backspaceString);
                        System.out.print(ROTATE_CHARS[rotateIndex[0]]);
                        System.out.print(' ');
                        System.out.printf("%1$" + maxPartNumberLength + "s", partIndex[0]);
                        System.out.print('/');
                        System.out.print(maxPartNumber);
                        if (length == null) {
                            System.out.print(" [  ?%]");
                        } else {
                            String percentage = String.format("%1$3s", (int) (position * 100.0 / length));
                            System.out.print(" [" + percentage + "%]");
                        }
                    });
                    outputStream.closeArchiveEntry();
                    partIndex[0]++;
                }
            }
        } catch (IOException e) {
            System.out.print(backspaceString);
            System.out.println("X [  ?%]");
            throw new UpdaterConnectionException("Failed to download file '" + directoryName + "'", e);
        }
        System.out.print(backspaceString);
        System.out.println("\u2713 [100%]");
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                BASELINE_EXPRESSION_FILE_NAME, DISEASE_TO_PHENOTYPE_FILE_NAME, DISEASES_FILE_NAME,
                DRUG_WARNINGS_FILE_NAME, GO_FILE_NAME, HPO_FILE_NAME, INDICATION_FILE_NAME, INTERACTION_FILE_NAME,
                INTERACTION_EVIDENCE_FILE_NAME, MECHANISM_OF_ACTION_FILE_NAME, MOLECULE_FILE_NAME,
                MOUSE_PHENOTYPES_FILE_NAME, REACTOME_FILE_NAME, TARGETS_FILE_NAME, FDA_ADR_FILE_NAME, FDA_ATR_FILE_NAME,
                EPMC_COOCCURRENCES_FILE_NAME, EXPRESSION_SPECIFICITY_FILE_NAME, PHARMACOGENOMICS_FILE_NAME,
                TARGET_ESSENTIALITY_FILE_NAME, TARGET_PRIORITISATION_FILE_NAME
        };
    }
}
