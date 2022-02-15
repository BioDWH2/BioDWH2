package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Describes one variable parameter and its values in this experiment  - eg - variable concentration of a specific
 * drug.
 * <pre>
 * &lt;complexType name="variableParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="string"/>
 *         &lt;element name="unit" type="openCvType" minOccurs="0"/>
 *         &lt;element name="variableValueList" type="variableValueList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class VariableParameter {
    public String description;
    public OpenCvType unit;
    @JacksonXmlElementWrapper
    public List<VariableValue> variableValueList;
}
