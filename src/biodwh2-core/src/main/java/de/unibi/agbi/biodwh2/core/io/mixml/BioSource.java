package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes the biological source of an object, in simple form only the NCBI taxid.
 * <pre>
 * &lt;complexType name="bioSource">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="cellType" type="openCvType" minOccurs="0"/>
 *         &lt;element name="compartment" type="openCvType" minOccurs="0"/>
 *         &lt;element name="tissue" type="openCvType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ncbiTaxId" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class BioSource {
    public Names names;
    public OpenCvType cellType;
    public OpenCvType compartment;
    public OpenCvType tissue;
    @JacksonXmlProperty(isAttribute = true)
    public int ncbiTaxId;
}
