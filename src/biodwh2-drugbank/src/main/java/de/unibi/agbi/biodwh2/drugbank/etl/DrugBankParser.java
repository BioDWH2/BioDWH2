package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.Drugbank;
import de.unibi.agbi.biodwh2.drugbank.model.DrugbankMetaboliteId;
import de.unibi.agbi.biodwh2.drugbank.model.Metabolite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DrugBankParser extends Parser<DrugBankDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(DrugBankParser.class);

    @Override
    public boolean parse(Workspace workspace, DrugBankDataSource dataSource) throws ParserException {
        return parseDrugBankXmlFile(workspace, dataSource) && parseMetaboliteSdfFile(workspace, dataSource);
    }

    private boolean parseDrugBankXmlFile(Workspace workspace, DrugBankDataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_full_database.xml.zip");
        File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ParserFileNotFoundException("drugbank_all_full_database.xml.zip");
        ZipInputStream zipInputStream = openZipInputStream(zipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryCoreXml(zipEntry.getName())) {
                    dataSource.drugBankData = parseDrugBankFromZipStream(zipInputStream);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'drugbank_all_full_database.xml.zip'", e);
        }
        return false;
    }

    private static ZipInputStream openZipInputStream(File file) throws ParserFileNotFoundException {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return new ZipInputStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            throw new ParserFileNotFoundException(file.getName());
        }
    }

    private static boolean isZipEntryCoreXml(String name) {
        return name.startsWith("full") && name.endsWith(".xml");
    }

    private Drugbank parseDrugBankFromZipStream(InputStream stream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Drugbank.class);
    }

    private boolean parseMetaboliteSdfFile(Workspace workspace, DrugBankDataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_metabolite-structures.sdf.zip");
        File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ParserFileNotFoundException("drugbank_all_metabolite-structures.sdf.zip");
        ZipInputStream zipInputStream = openZipInputStream(zipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryMetaboliteSdf(zipEntry.getName())) {
                    dataSource.metabolites = parseMetabolitesFromZipStream(zipInputStream);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'drugbank_all_metabolite-structures.sdf.zip'", e);
        }
        return false;
    }

    private static boolean isZipEntryMetaboliteSdf(String name) {
        return name.endsWith(".sdf");
    }

    private List<Metabolite> parseMetabolitesFromZipStream(InputStream stream) throws IOException {
        List<Metabolite> metabolites = new ArrayList<>();
        SdfReader reader = new SdfReader(stream, "UTF-8");
        for (SdfEntry entry : reader)
            metabolites.add(metaboliteFromSdfEntry(entry));
        logger.info("NUM METABOLITES " + metabolites.size());
        return metabolites;
    }

    private Metabolite metaboliteFromSdfEntry(final SdfEntry entry) {
        Metabolite metabolite = new Metabolite();
        metabolite.databaseId = entry.properties.get("DATABASE_ID");
        metabolite.databaseName = entry.properties.get("DATABASE_NAME");
        metabolite.smiles = entry.properties.get("SMILES");
        metabolite.inchiId = entry.properties.get("INCHI_IDENTIFIER");
        metabolite.inchiKey = entry.properties.get("INCHI_KEY");
        metabolite.formula = entry.properties.get("FORMULA");
        metabolite.molecularWeight = entry.properties.get("MOLECULAR_WEIGHT");
        metabolite.exactMass = entry.properties.get("EXACT_MASS");
        metabolite.iupac = entry.properties.get("JCHEM_IUPAC");
        metabolite.traditionalIupac = entry.properties.get("JCHEM_TRADITIONAL_IUPAC");
        metabolite.drugbankId = new DrugbankMetaboliteId();
        metabolite.drugbankId.value = entry.properties.get("DRUGBANK_ID");
        metabolite.drugbankId.primary = true;
        metabolite.name = entry.properties.get("GENERIC_NAME");
        return metabolite;
    }
}
