package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Crossreference to an external database. Crossreferences to literature databases, e.g. PubMed, should not be put into
 * this structure, but into the bibRef element where possible.
 * <pre>
 * &lt;complexType name="xref">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="primaryRef" type="dbReference"/>
 *         &lt;element name="secondaryRef" type="dbReference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Xref {
    public DbReference primaryRef;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "secondaryRef")
    public List<DbReference> secondaryRef;
}
