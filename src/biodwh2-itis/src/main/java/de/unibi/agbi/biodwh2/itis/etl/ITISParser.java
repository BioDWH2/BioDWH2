package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import de.unibi.agbi.biodwh2.itis.model.*;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://www.itis.gov/pdf/ITIS_ConceptualModelEntityDefinition.pdf
 */
public class ITISParser extends Parser<ITISDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITISParser.class);

    public ITISParser(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        try (TarArchiveInputStream stream = FileUtils.openTarGzip(workspace, dataSource, ITISUpdater.FILE_NAME)) {
            ArchiveEntry entry;
            while ((entry = stream.getNextTarEntry()) != null)
                parseArchiveEntry(stream, getArchiveFileName(entry.getName()));
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse table files from '" + ITISUpdater.FILE_NAME + "'", e);
        }
        return true;
    }

    private String getArchiveFileName(final String filePath) {
        return filePath.contains("/") && !filePath.endsWith("/") ? filePath.split("/")[1] : null;
    }

    private void parseArchiveEntry(final InputStream stream, final String fileName) throws ParserFormatException {
        // "strippedauthor" is ignored due to information redundancy
        if ("comments".equals(fileName))
            dataSource.comments = readModelFromFile(fileName, Comment.class, stream);
        else if ("experts".equals(fileName))
            dataSource.experts = readModelFromFile(fileName, Expert.class, stream);
        else if ("geographic_div".equals(fileName))
            dataSource.geographicDivisions = readModelFromFile(fileName, GeographicDivision.class, stream);
        else if ("hierarchy".equals(fileName))
            dataSource.hierarchies = readModelFromFile(fileName, Hierarchy.class, stream);
        else if ("jurisdiction".equals(fileName))
            dataSource.jurisdictions = readModelFromFile(fileName, Jurisdiction.class, stream);
        else if ("kingdoms".equals(fileName))
            dataSource.kingdoms = readModelFromFile(fileName, Kingdom.class, stream);
        else if ("longnames".equals(fileName))
            dataSource.longNames = readLongNamesFromFile(fileName, stream);
        else if ("nodc_ids".equals(fileName))
            dataSource.nodcIds = readNodcIdsFromFile(fileName, stream);
        else if ("other_sources".equals(fileName))
            dataSource.otherSources = readModelFromFile(fileName, OtherSource.class, stream);
        else if ("publications".equals(fileName))
            dataSource.publications = readModelFromFile(fileName, Publication.class, stream);
        else if ("reference_links".equals(fileName))
            dataSource.referenceLinks = readModelFromFile(fileName, ReferenceLink.class, stream);
        else if ("synonym_links".equals(fileName))
            dataSource.synonymLinks = readSynonymLinksFromFile(fileName, stream);
        else if ("taxon_authors_lkp".equals(fileName))
            dataSource.taxonAuthorsLkps = readModelFromFile(fileName, TaxonAuthorLkp.class, stream);
        else if ("taxon_unit_types".equals(fileName))
            dataSource.taxonUnitTypes = readModelFromFile(fileName, TaxonUnitType.class, stream);
        else if ("taxonomic_units".equals(fileName))
            dataSource.taxonomicUnits = readModelFromFile(fileName, TaxonomicUnit.class, stream);
        else if ("tu_comments_links".equals(fileName))
            dataSource.taxonomicUnitCommentLinks = readModelFromFile(fileName, TaxonomicUnitCommentLink.class, stream);
        else if ("vern_ref_links".equals(fileName))
            dataSource.vernacularReferenceLinks = readModelFromFile(fileName, VernacularReferenceLink.class, stream);
        else if ("vernaculars".equals(fileName))
            dataSource.vernaculars = readModelFromFile(fileName, Vernacular.class, stream);
    }

    private <T> List<T> readModelFromFile(final String fileName, final Class<T> typeClass,
                                          final InputStream stream) throws ParserFormatException {
        try {
            return FileUtils.openSeparatedValuesFile(stream, typeClass, '|', false, false).readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private Map<Integer, String> readLongNamesFromFile(final String fileName,
                                                       final InputStream stream) throws ParserFormatException {
        final Map<Integer, String> tsnLongNameMap = new HashMap<>();
        for (final String[] row : readModelFromFile(fileName, String[].class, stream)) {
            final int tsn = Integer.parseInt(row[0]);
            if (tsnLongNameMap.containsKey(tsn))
                LOGGER.warn("Duplicate LongName entry for ITIS tsn '" + tsn + "'");
            tsnLongNameMap.put(tsn, row[1]);
        }
        return tsnLongNameMap;
    }

    private Map<Integer, String> readNodcIdsFromFile(final String fileName,
                                                     final InputStream stream) throws ParserFormatException {
        final Map<Integer, String> tsnNodcIdMap = new HashMap<>();
        for (final String[] row : readModelFromFile(fileName, String[].class, stream)) {
            final int tsn = Integer.parseInt(row[2]);
            if (tsnNodcIdMap.containsKey(tsn))
                LOGGER.warn("Duplicate NodcId entry for ITIS tsn '" + tsn + "'");
            tsnNodcIdMap.put(tsn, row[0]);
        }
        return tsnNodcIdMap;
    }

    private Map<Integer, Integer> readSynonymLinksFromFile(final String fileName,
                                                           final InputStream stream) throws ParserFormatException {
        final Map<Integer, Integer> tsnAcceptedTsnMap = new HashMap<>();
        for (final String[] row : readModelFromFile(fileName, String[].class, stream))
            tsnAcceptedTsnMap.put(Integer.parseInt(row[0]), Integer.parseInt(row[1]));
        return tsnAcceptedTsnMap;
    }
}
