package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

/**
 * <pre>
 * &lt;complexType name="abstractParameter">
 *   &lt;complexContent>
 *     &lt;extension base="parameterBase">
 *       &lt;sequence>
 *         &lt;element name="bibref" type="bibref" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uncertainty" type="decimal" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class AbstractParameter extends ParameterBase {
    public Bibref bibref;
    @JacksonXmlProperty(isAttribute = true)
    public BigDecimal uncertainty;
}
