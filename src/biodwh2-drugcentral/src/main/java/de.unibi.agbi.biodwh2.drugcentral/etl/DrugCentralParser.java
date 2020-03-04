package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DrugCentralParser extends Parser<DrugCentralDataSource> {
    @Override
    public boolean parse(Workspace workspace, DrugCentralDataSource dataSource) throws ParserException {
        parseCsvFile(workspace, dataSource, ActionType.class, "action_type.tsv");
        parseCsvFile(workspace, dataSource, ActiveIngredient.class, "active_ingredient.tsv");
        parseCsvFile(workspace, dataSource, ActTableFull.class, "act_table_full.tsv");
        parseCsvFile(workspace, dataSource, Approval.class, "approval.tsv");
        parseCsvFile(workspace, dataSource, ApprovalType.class, "approval_type.tsv");
        parseCsvFile(workspace, dataSource, Atc.class, "atc.tsv");
        parseCsvFile(workspace, dataSource, AtcDdd.class, "atc_ddd.tsv");
        parseCsvFile(workspace, dataSource, AttributeType.class, "attr_type.tsv");
        parseCsvFile(workspace, dataSource, de.unibi.agbi.biodwh2.drugcentral.model.DataSource.class,
                     "data_source.tsv");
        parseCsvFile(workspace, dataSource, DbVersion.class, "dbversion.tsv");
        parseCsvFile(workspace, dataSource, Ddi.class, "ddi.tsv");
        parseCsvFile(workspace, dataSource, DdiRisk.class, "ddi_risk.tsv");
        parseCsvFile(workspace, dataSource, Doid.class, "doid.tsv");
        parseCsvFile(workspace, dataSource, DoidXref.class, "doid_xref.tsv");
        parseCsvFile(workspace, dataSource, DrugClass.class, "drug_class.tsv");
        parseCsvFile(workspace, dataSource, Faers.class, "faers.tsv");
        parseCsvFile(workspace, dataSource, Identifier.class, "identifier.tsv");
        parseCsvFile(workspace, dataSource, IdType.class, "id_type.tsv");
        parseCsvFile(workspace, dataSource, InnStem.class, "inn_stem.tsv");
        parseCsvFile(workspace, dataSource, Label.class, "label.tsv");
        parseCsvFile(workspace, dataSource, LincsSignature.class, "lincs_signature.tsv");
        parseCsvFile(workspace, dataSource, ObExclusivity.class, "ob_exclusivity.tsv");
        parseCsvFile(workspace, dataSource, ObExclusivityCode.class, "ob_exclusivity_code.tsv");
        parseCsvFile(workspace, dataSource, ObPatent.class, "ob_patent.tsv");
        parseCsvFile(workspace, dataSource, ObPatentUseCode.class, "ob_patent_use_code.tsv");
        parseCsvFile(workspace, dataSource, ObProduct.class, "ob_product.tsv");
        parseCsvFile(workspace, dataSource, OmopRelationship.class, "omop_relationship.tsv");
        parseCsvFile(workspace, dataSource, Parentmol.class, "parentmol.tsv");
        parseCsvFile(workspace, dataSource, Pdb.class, "pdb.tsv");
        parseCsvFile(workspace, dataSource, PharmaClass.class, "pharma_class.tsv");
        parseCsvFile(workspace, dataSource, Pka.class, "pka.tsv");
        parseCsvFile(workspace, dataSource, Prd2Label.class, "prd2label.tsv");
        parseCsvFile(workspace, dataSource, Product.class, "product.tsv");
        parseCsvFile(workspace, dataSource, ProteinType.class, "protein_type.tsv");
        parseCsvFile(workspace, dataSource, Reference.class, "reference.tsv");
        parseCsvFile(workspace, dataSource, RefType.class, "ref_type.tsv");
        parseCsvFile(workspace, dataSource, Section.class, "section.tsv");
        parseCsvFile(workspace, dataSource, Struct2Atc.class, "struct2atc.tsv");
        parseCsvFile(workspace, dataSource, Struct2DrgClass.class, "struct2drgclass.tsv");
        parseCsvFile(workspace, dataSource, Struct2ObProd.class, "struct2obprod.tsv");
        parseCsvFile(workspace, dataSource, Struct2Parent.class, "struct2parent.tsv");
        parseCsvFile(workspace, dataSource, StructTypeDef.class, "struct_type_def.tsv");
        parseCsvFile(workspace, dataSource, Structures.class, "structures.tsv");
        parseCsvFile(workspace, dataSource, StructureType.class, "structure_type.tsv");
        parseCsvFile(workspace, dataSource, Synonyms.class, "synonyms.tsv");
        parseCsvFile(workspace, dataSource, TargetClass.class, "target_class.tsv");
        parseCsvFile(workspace, dataSource, TargetComponent.class, "target_component.tsv");
        parseCsvFile(workspace, dataSource, TargetDictionary.class, "target_dictionary.tsv");
        parseCsvFile(workspace, dataSource, TargetGo.class, "target_go.tsv");
        parseCsvFile(workspace, dataSource, TargetKeyword.class, "target_keyword.tsv");
        parseCsvFile(workspace, dataSource, Td2Tc.class, "td2tc.tsv");
        parseCsvFile(workspace, dataSource, Tdgo2Tc.class, "tdgo2tc.tsv");
        parseCsvFile(workspace, dataSource, Tdkey2Tc.class, "tdkey2tc.tsv");
        return true;
    }

    private <T> void parseCsvFile(Workspace workspace, DrugCentralDataSource dataSource, Class<T> typeVariableClass,
                                  String fileName) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
            throw new ParserFileNotFoundException(fileName);
        ObjectReader reader = getFormatReader(typeVariableClass);
        try {
            MappingIterator<T> iterator = reader.readValues(sourceFile);
            iterator.next();
            storeResults(dataSource, typeVariableClass, iterator.readAll());
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private <T> void storeResults(DrugCentralDataSource dataSource, Class<?> typeVariableClass, List<T> results) {
        dataSource.drugCentralDict.put(typeVariableClass, results);
    }

    private <T> ObjectReader getFormatReader(Class<T> typeVariableClass) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeVariableClass).withColumnSeparator('\t').withNullValue("");
        return csvMapper.readerFor(typeVariableClass).with(schema);
    }
}
