package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
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
        return name.equals("metabolite-structures.sdf");
    }

    private List<Metabolite> parseMetabolitesFromZipStream(InputStream stream) throws IOException {
        List<Metabolite> metabolites = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Metabolite lastMetabolite = new Metabolite();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("> ")) {
                parseMetaboliteProperty(reader, lastMetabolite, line.substring(2).trim());
            } else if (line.trim().equals("$$$$")) {
                metabolites.add(lastMetabolite);
                lastMetabolite = new Metabolite();
            }
        }
        logger.info("NUM METABOLITES " + metabolites.size());
        return metabolites;
    }

    private void parseMetaboliteProperty(BufferedReader reader, Metabolite metabolite, String line) throws IOException {
        switch (line) {
            case "<DATABASE_ID>":
                metabolite.databaseId = reader.readLine();
                break;
            case "<DATABASE_NAME>":
                metabolite.databaseName = reader.readLine();
                break;
            case "<SMILES>":
                metabolite.smiles = reader.readLine();
                break;
            case "<INCHI_IDENTIFIER>":
                metabolite.inchiId = reader.readLine();
                break;
            case "<INCHI_KEY>":
                metabolite.inchiKey = reader.readLine();
                break;
            case "<FORMULA>":
                metabolite.formula = reader.readLine();
                break;
            case "<MOLECULAR_WEIGHT>":
                metabolite.molecularWeight = reader.readLine();
                break;
            case "<EXACT_MASS>":
                metabolite.exactMass = reader.readLine();
                break;
            case "<JCHEM_IUPAC>":
                metabolite.iupac = reader.readLine();
                break;
            case "<JCHEM_TRADITIONAL_IUPAC>":
                metabolite.traditionalIupac = reader.readLine();
                break;
            case "<DRUGBANK_ID>":
                metabolite.drugbankId = new DrugbankMetaboliteId();
                metabolite.drugbankId.value = reader.readLine();
                metabolite.drugbankId.primary = true;
                break;
            case "<NAME>":
                metabolite.name = reader.readLine();
                break;
            case "<UNII>":
                metabolite.unii = reader.readLine();
                break;
            default:
                reader.readLine();
        }
    }
}
