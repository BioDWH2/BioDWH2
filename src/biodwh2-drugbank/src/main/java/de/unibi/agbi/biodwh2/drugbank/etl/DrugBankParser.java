package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.DrugStructure;
import de.unibi.agbi.biodwh2.drugbank.model.Drugbank;
import de.unibi.agbi.biodwh2.drugbank.model.DrugbankMetaboliteId;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DrugBankParser extends Parser<DrugBankDataSource> {
    public DrugBankParser(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        return parseDrugBankXmlFile(workspace, dataSource) && parseDrugSdfFile(workspace, dataSource) &&
               parseMetaboliteSdfFile(workspace, dataSource);
    }

    private boolean parseDrugBankXmlFile(final Workspace workspace,
                                         final DrugBankDataSource dataSource) throws ParserException {
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

    private static ZipInputStream openZipInputStream(final File file) throws ParserFileNotFoundException {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return new ZipInputStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            throw new ParserFileNotFoundException(file.getName());
        }
    }

    private static boolean isZipEntryCoreXml(final String name) {
        return name.startsWith("full") && name.endsWith(".xml");
    }

    private Drugbank parseDrugBankFromZipStream(final InputStream stream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Drugbank.class);
    }

    private boolean parseDrugSdfFile(final Workspace workspace,
                                     final DrugBankDataSource dataSource) throws ParserException {
        final String fileName = "drugbank_all_structures.sdf.zip";
        SdfReader reader = getSdfReaderFromZip(workspace, dataSource, fileName);
        if (reader == null)
            return false;
        dataSource.drugStructures = new ArrayList<>();
        for (SdfEntry entry : reader)
            dataSource.drugStructures.add(drugFromSdfEntry(entry));
        return true;
    }

    private SdfReader getSdfReaderFromZip(final Workspace workspace, final DataSource dataSource,
                                          final String fileName) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ParserFileNotFoundException(fileName);
        ZipInputStream zipInputStream = openZipInputStream(zipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntrySdf(zipEntry.getName())) {
                    return new SdfReader(zipInputStream, "UTF-8");
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + fileName + "'", e);
        }
        return null;
    }

    private static boolean isZipEntrySdf(final String name) {
        return name.endsWith(".sdf");
    }

    private DrugStructure drugFromSdfEntry(final SdfEntry entry) {
        DrugStructure drug = new DrugStructure();
        drug.databaseId = entry.properties.get("DATABASE_ID");
        drug.databaseName = entry.properties.get("DATABASE_NAME");
        drug.smiles = entry.properties.get("SMILES");
        drug.inchiId = entry.properties.get("INCHI_IDENTIFIER");
        drug.inchiKey = entry.properties.get("INCHI_KEY");
        drug.formula = entry.properties.get("FORMULA");
        drug.molecularWeight = entry.properties.get("MOLECULAR_WEIGHT");
        drug.exactMass = entry.properties.get("EXACT_MASS");
        drug.iupac = entry.properties.get("JCHEM_IUPAC");
        drug.traditionalIupac = entry.properties.get("JCHEM_TRADITIONAL_IUPAC");
        drug.drugbankId = new DrugbankMetaboliteId();
        drug.drugbankId.value = entry.properties.get("DRUGBANK_ID");
        drug.drugbankId.primary = true;
        drug.name = entry.properties.get("GENERIC_NAME");
        drug.ruleOfFive = entry.properties.get("JCHEM_RULE_OF_FIVE");
        drug.ghoseFilter = entry.properties.get("JCHEM_GHOSE_FILTER");
        drug.veberRule = entry.properties.get("JCHEM_VEBER_RULE");
        drug.mddrLikeRule = entry.properties.get("JCHEM_MDDR_LIKE_RULE");
        return drug;
    }

    private boolean parseMetaboliteSdfFile(final Workspace workspace,
                                           final DrugBankDataSource dataSource) throws ParserException {
        final String fileName = "drugbank_all_metabolite-structures.sdf.zip";
        SdfReader reader = getSdfReaderFromZip(workspace, dataSource, fileName);
        if (reader == null)
            return false;
        dataSource.metaboliteStructures = new ArrayList<>();
        for (SdfEntry entry : reader)
            dataSource.metaboliteStructures.add(metaboliteFromSdfEntry(entry));
        return true;
    }

    private MetaboliteStructure metaboliteFromSdfEntry(final SdfEntry entry) {
        MetaboliteStructure metabolite = new MetaboliteStructure();
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
        metabolite.name = entry.properties.get("NAME");
        metabolite.unii = entry.properties.get("UNII");
        metabolite.ruleOfFive = entry.properties.get("JCHEM_RULE_OF_FIVE");
        metabolite.ghoseFilter = entry.properties.get("JCHEM_GHOSE_FILTER");
        metabolite.veberRule = entry.properties.get("JCHEM_VEBER_RULE");
        metabolite.mddrLikeRule = entry.properties.get("JCHEM_MDDR_LIKE_RULE");

        return metabolite;
    }
}
