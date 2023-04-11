package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Describes data availability, e.g. through a copyright statement. If no availability is given, the data is assumed to
 * be freely available.
 * <pre>
 * &lt;complexType name="availability">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
public class Availability {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
