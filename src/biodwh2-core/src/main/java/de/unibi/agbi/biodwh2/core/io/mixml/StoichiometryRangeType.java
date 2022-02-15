package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * The stoichiometry range of a participant.
 * <pre>
 * &lt;complexType name="stoichiometryRangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;attribute name="minValue" use="required" type="int" />
 *       &lt;attribute name="maxValue" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class StoichiometryRangeType {
    @JacksonXmlProperty(isAttribute = true)
    public int minValue;
    @JacksonXmlProperty(isAttribute = true)
    public int maxValue;
}
