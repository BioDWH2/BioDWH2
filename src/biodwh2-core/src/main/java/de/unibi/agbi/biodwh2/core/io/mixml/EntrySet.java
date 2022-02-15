package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * <pre>
 * &lt;complexType name="entrySet">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" type="entry" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="level" use="required" type="int" fixed="3" />
 *       &lt;attribute name="version" use="required" type="int" fixed="0" />
 *       &lt;attribute name="minorVersion" type="int" fixed="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@JsonIgnoreProperties({"schemaLocation"})
public class EntrySet {
    public List<Entry> entry;
    @JacksonXmlProperty(isAttribute = true)
    public int level;
    @JacksonXmlProperty(isAttribute = true)
    public int version;
    @JacksonXmlProperty(isAttribute = true)
    public Integer minorVersion;
}
