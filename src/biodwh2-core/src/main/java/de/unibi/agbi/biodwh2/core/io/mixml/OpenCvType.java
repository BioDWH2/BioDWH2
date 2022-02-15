package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * Allows to reference an external controlled vocabulary, or to directly include a value if no suitable external
 * definition is available.
 * <pre>
 * &lt;complexType name="openCvType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class OpenCvType {
    public Names names;
    public Xref xref;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
}
