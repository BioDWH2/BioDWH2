package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * A short alphanumeric label identifying an object. Not necessarily unique.
 * <pre>
 * &lt;complexType name="label">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
public class Label {
    @JacksonXmlText
    public String value;
}
