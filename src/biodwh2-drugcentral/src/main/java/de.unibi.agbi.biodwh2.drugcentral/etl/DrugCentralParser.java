package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrugCentralParser<T> extends Parser {
    private Map<Class, String> typeVariableClasses = new HashMap<Class, String>() {{
        put(ActionType.class, "action_type.tsv");
        put(ActiveIngredient.class, "active_ingredient.tsv");
        put(ActTableFull.class, "act_table_full.tsv");
        put(Approval.class, "approval.tsv");
        put(ApprovalType.class, "approval_type.tsv");
        put(Atc.class, "atc.tsv");
        put(AtcDdd.class, "atc_ddd.tsv");
        put(AttributeType.class, "attr_type.tsv");
        put(de.unibi.agbi.biodwh2.drugcentral.model.DataSource.class, "data_source.tsv");
        put(DbVersion.class, "dbversion.tsv");
        put(Ddi.class, "ddi.tsv");
        put(DdiRisk.class, "ddi_risk.tsv");
        put(Doid.class, "doid.tsv");
        put(DoidXref.class, "doid_xref.tsv");
        put(DrugClass.class, "drug_class.tsv");
        put(Faers.class, "faers.tsv");
        put(Identifier.class, "identifier.tsv");
        put(IdType.class, "id_type.tsv");
        put(InnStem.class, "inn_stem.tsv");
        put(Label.class, "label.tsv");
        put(LincsSignature.class, "lincs_signature.tsv");
        put(ObExclusivity.class, "ob_exclusivity.tsv");
        put(ObExclusivityCode.class, "ob_exclusivity_code.tsv");
        put(ObPatent.class, "ob_patent.tsv");
        put(ObPatentUseCode.class, "ob_patent_use_code.tsv");
        put(ObProduct.class, "ob_product.tsv");
        put(OmopRelationship.class, "omop_relationship.tsv");
        put(Parentmol.class, "parentmol.tsv");
        put(Pdb.class, "pdb.tsv");
        put(PharmaClass.class, "pharma_class.tsv");
        put(Pka.class, "pka.tsv");
        put(Prd2Label.class, "prd2label.tsv");
        put(Product.class, "product.tsv");
        put(ProteinType.class, "protein_type.tsv");
        put(Reference.class, "reference.tsv");
        put(RefType.class, "ref_type.tsv");
        put(Section.class, "section.tsv");
        put(Struct2Atc.class, "struct2atc.tsv");
        put(Struct2DrgClass.class, "struct2drgclass.tsv");
        put(Struct2ObProd.class, "struct2obprod.tsv");
        put(Struct2Parent.class, "struct2parent.tsv");
        put(StructTypeDef.class, "struct_type_def.tsv");
        put(Structures.class, "structures.tsv");
        put(StructureType.class, "structure_type.tsv");
        put(Synonyms.class, "synonyms.tsv");
        put(TargetClass.class, "target_class.tsv");
        put(TargetComponent.class, "target_component.tsv");
        put(TargetDictionary.class, "target_dictionary.tsv");
        put(TargetGo.class, "target_go.tsv");
        put(TargetKeyword.class, "target_keyword.tsv");
        put(Td2Tc.class, "td2tc.tsv");
        put(Tdgo2Tc.class, "tdgo2tc.tsv");
        put(Tdkey2Tc.class, "tdkey2tc.tsv");
    }};

    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        for (Class key : typeVariableClasses.keySet()) {
            parseCsvFile(workspace, dataSource, key, typeVariableClasses.get(key));
        }
        return true;
    }

    private void parseCsvFile(Workspace workspace, DataSource dataSource, Class typeVariableClass,
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

    private void storeResults(DataSource dataSource, Class typeVariableClass, List<?> results) {
        ((DrugCentralDataSource) dataSource).drugCentralDict.put(typeVariableClass, (List<Object>) results);
    }

    private ObjectReader getFormatReader(Class typeVariableClass) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeVariableClass).withColumnSeparator('\t').withNullValue("");
        return csvMapper.readerFor(typeVariableClass).with(schema);
    }
}
