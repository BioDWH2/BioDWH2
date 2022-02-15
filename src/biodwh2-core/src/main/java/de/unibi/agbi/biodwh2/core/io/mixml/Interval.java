package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * A interval on a sequence.
 * <pre>
 * &lt;complexType name="interval">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;attribute name="begin" use="required" type="long" />
 *       &lt;attribute name="end" use="required" type="long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Interval {
    @JacksonXmlProperty(isAttribute = true)
    public long begin;
    @JacksonXmlProperty(isAttribute = true)
    public long end;
}
