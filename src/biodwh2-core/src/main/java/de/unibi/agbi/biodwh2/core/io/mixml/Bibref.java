package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * Bibliographic reference.
 * <pre>
 * &lt;complexType name="bibref">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="xref" type="xref"/>
 *           &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="attributeList" type="attributeList"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Bibref {
    public Xref xref;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
}
