package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * A value for a specific variableParameter in a specific experiment - eg - the concentration of a specific drug.
 * <pre>
 * &lt;complexType name="variableValue">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="value" type="string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *       &lt;attribute name="order" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class VariableValue {
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
    @JacksonXmlProperty(isAttribute = true)
    public Integer order;
}
