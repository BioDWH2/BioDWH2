package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * The mean value for the participant stoichiometry.
 * <pre>
 * &lt;complexType name="stoichiometryType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;attribute name="value" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class StoichiometryType {
    @JacksonXmlProperty(isAttribute = true)
    public int value;
}
