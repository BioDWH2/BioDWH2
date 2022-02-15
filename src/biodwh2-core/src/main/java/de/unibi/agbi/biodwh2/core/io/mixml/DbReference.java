package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Refers to a unique object in an external database.
 * <pre>
 * &lt;complexType name="dbReference">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="attributeList" type="attributeList"/>
 *       &lt;/sequence>
 *       &lt;attribute name="db" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="dbAc">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="secondary">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="version">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="refType">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="refTypeAc">
 *         &lt;simpleType>
 *           &lt;restriction base="string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class DbReference {
    @JacksonXmlElementWrapper
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public String db;
    @JacksonXmlProperty(isAttribute = true)
    public String dbAc;
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String secondary;
    @JacksonXmlProperty(isAttribute = true)
    public String version;
    @JacksonXmlProperty(isAttribute = true)
    public String refType;
    @JacksonXmlProperty(isAttribute = true)
    public String refTypeAc;
}
