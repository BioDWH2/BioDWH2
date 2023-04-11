package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Description of the source of the entry, usually an organisation
 * <pre>
 * &lt;complexType name="source">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="bibref" type="bibref" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="release">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="releaseDate" type="date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Source {
    public Names names;
    public Bibref bibref;
    public Xref xref;
    @JacksonXmlElementWrapper
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public String release;
    @JacksonXmlProperty(isAttribute = true)
    public String releaseDate;
}
