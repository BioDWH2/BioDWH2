package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

/**
 * A numeric parameter, e.g. for a kinetic value
 * <pre>
 * &lt;complexType name="parameterBase">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;attribute name="term" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="termAc">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unit">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unitAc">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="base" type="short" default="10" />
 *       &lt;attribute name="exponent" type="short" default="0" />
 *       &lt;attribute name="factor" use="required" type="decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ParameterBase {
    @JacksonXmlProperty(isAttribute = true)
    public String term;
    @JacksonXmlProperty(isAttribute = true)
    public String termAc;
    @JacksonXmlProperty(isAttribute = true)
    public String unit;
    @JacksonXmlProperty(isAttribute = true)
    public String unitAc;
    @JacksonXmlProperty(isAttribute = true)
    public Short base;
    @JacksonXmlProperty(isAttribute = true)
    public Short exponent;
    @JacksonXmlProperty(isAttribute = true)
    public BigDecimal factor;
}
