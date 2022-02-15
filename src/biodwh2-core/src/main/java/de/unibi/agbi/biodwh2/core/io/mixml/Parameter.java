package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

/**
 * <pre>
 * &lt;complexType name="parameter">
 *   &lt;complexContent>
 *     &lt;extension base="parameterBase">
 *       &lt;sequence>
 *         &lt;element name="experimentRef" type="int" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uncertainty" type="decimal" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Parameter extends ParameterBase {
    public Integer experimentRef;
    @JacksonXmlProperty(isAttribute = true)
    public BigDecimal uncertainty;
}
