package de.unibi.agbi.biodwh2.mir2disease.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.mir2disease.Mir2diseaseDataSource;
import de.unibi.agbi.biodwh2.mir2disease.model.AllEntries;
import de.unibi.agbi.biodwh2.mir2disease.model.Disease;
import de.unibi.agbi.biodwh2.mir2disease.model.MiRNATarget;

import java.io.IOException;

public class Mir2diseaseParser extends Parser<Mir2diseaseDataSource> {
    static final String MI_RNA_TARGET = Mir2diseaseUpdater.FILE_NAMES[0];
    static final String DISEASE = Mir2diseaseUpdater.FILE_NAMES[1];
    static final String ALL_ENTRIES = Mir2diseaseUpdater.FILE_NAMES[2];

    public Mir2diseaseParser(final Mir2diseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        try {
            try (final MappingIterator<MiRNATarget> iterator = FileUtils.openTsvWithHeader(workspace, dataSource,
                                                                                           MI_RNA_TARGET,
                                                                                           MiRNATarget.class)) {
                dataSource.miRNATarget = iterator.readAll(); // some quotes in file
            }
            for (MiRNATarget target : dataSource.miRNATarget) {
                target.miRNA = target.miRNA.replaceAll("\"", "");
                if (target.validatedTarget != null) {
                    target.validatedTarget = target.validatedTarget.replaceAll("\"", "");
                }
                if (target.pubDate != null) {
                    target.pubDate = target.pubDate.replaceAll("\"", "");
                }
                if (target.reference != null) {
                    target.reference = target.reference.replaceAll("\"", "");
                }
            }
            dataSource.miRNATarget.remove(0); // delete header
            try (final MappingIterator<Disease> iterator = FileUtils.openTsvWithHeader(workspace, dataSource, DISEASE,
                                                                                       Disease.class)) {
                dataSource.disease = iterator.readAll();
            }
            try (final MappingIterator<AllEntries> iterator = FileUtils.openTsv(workspace, dataSource, ALL_ENTRIES,
                                                                                AllEntries.class)) {
                dataSource.allEntries = iterator.readAll();
            }
            for (int i = 1; i < dataSource.allEntries.size(); i++) { // reference title on two lines in file
                final AllEntries previousEntry = dataSource.allEntries.get(i - 1);
                final AllEntries entry = dataSource.allEntries.get(i);
                if (entry.reference == null && entry.effect == null && entry.diseaseNameInOriginalPaper == null &&
                    entry.pubDate == null && entry.sequencingMethod == null) {
                    previousEntry.reference = previousEntry.reference + entry.miRNA;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException(e);
        }
        return true;
    }
}
