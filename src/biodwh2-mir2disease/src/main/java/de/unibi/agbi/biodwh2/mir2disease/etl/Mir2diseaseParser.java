package de.unibi.agbi.biodwh2.mir2disease.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.mir2disease.Mir2diseaseDataSource;
import de.unibi.agbi.biodwh2.mir2disease.model.AllEntries;
import de.unibi.agbi.biodwh2.mir2disease.model.Disease;
import de.unibi.agbi.biodwh2.mir2disease.model.MiRNATarget;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class Mir2diseaseParser extends Parser<Mir2diseaseDataSource> {
    public Mir2diseaseParser(final Mir2diseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        parseMiRNATargets(workspace);
        parseDiseases(workspace);
        parseEntries(workspace);
        return true;
    }

    private void parseMiRNATargets(final Workspace workspace) throws ParserFormatException {
        try (final InputStream stream = FileUtils.openInput(
                dataSource.resolveSourceFilePath(workspace, Mir2diseaseUpdater.MI_RNA_TARGET_FILE_NAME), 2);
             final var iterator = FileUtils.openTsvWithHeaderWithoutQuoting(stream, MiRNATarget.class)) {
            dataSource.miRNATarget = iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse file '" + Mir2diseaseUpdater.MI_RNA_TARGET_FILE_NAME + "'",
                                            e);
        }
        for (int i = dataSource.miRNATarget.size() - 1; i >= 0; i--) {
            final MiRNATarget target = dataSource.miRNATarget.get(i);
            if (i > 0 && target.pubDate == null && target.reference == null && target.validatedTarget == null) {
                final MiRNATarget previous = dataSource.miRNATarget.get(i - 1);
                previous.reference += target.miRNA;
                dataSource.miRNATarget.remove(i);
            } else {
                target.validatedTarget = StringUtils.strip(target.validatedTarget, "\"");
                target.pubDate = StringUtils.strip(target.pubDate, "\"");
                target.reference = StringUtils.strip(target.reference, "\"");
            }
        }
    }

    private void parseDiseases(final Workspace workspace) throws ParserFormatException {
        try (final var iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource,
                                                                            Mir2diseaseUpdater.DISEASE_FILE_NAME,
                                                                            Disease.class)) {
            dataSource.disease = iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse file '" + Mir2diseaseUpdater.DISEASE_FILE_NAME + "'", e);
        }
        for (int i = dataSource.disease.size() - 1; i >= 0; i--)
            if (dataSource.disease.get(i).diseaseOntologyID == null)
                dataSource.disease.remove(i);
    }

    private void parseEntries(final Workspace workspace) throws ParserFormatException {
        try (final var iterator = FileUtils.openTsvWithoutQuoting(workspace, dataSource,
                                                                  Mir2diseaseUpdater.ALL_ENTRIES_FILE_NAME,
                                                                  AllEntries.class)) {
            dataSource.allEntries = iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse file '" + Mir2diseaseUpdater.ALL_ENTRIES_FILE_NAME + "'",
                                            e);
        }
        for (int i = 1; i < dataSource.allEntries.size(); i++) { // reference title on two lines in file
            final AllEntries previousEntry = dataSource.allEntries.get(i - 1);
            final AllEntries entry = dataSource.allEntries.get(i);
            if (entry.reference == null && entry.effect == null && entry.diseaseNameInOriginalPaper == null &&
                entry.pubDate == null && entry.sequencingMethod == null) {
                previousEntry.reference = previousEntry.reference + entry.miRNA;
            }
        }
    }
}
