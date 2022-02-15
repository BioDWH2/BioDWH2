package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

/**
 * <pre>
 * &lt;complexType name="entry">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="source" minOccurs="0"/>
 *         &lt;element name="availabilityList" type="availabilityList" minOccurs="0"/>
 *         &lt;element name="experimentList" type="experimentDescriptionList" minOccurs="0"/>
 *         &lt;element name="interactorList" type="interactorList" minOccurs="0"/>
 *         &lt;element name="interactionList" type="interactionList"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Entry {
    @JacksonXmlProperty(isAttribute = true)
    public String releaseDate;
    public Names names;
    public Source source;
    public Bibref bibref;
    public Xref xref;
    @JacksonXmlElementWrapper
    public ArrayList<Availability> availabilityList;
    @JacksonXmlElementWrapper
    public ArrayList<ExperimentDescription> experimentList;
    @JacksonXmlElementWrapper
    public ArrayList<Interactor> interactorList;
    public InteractionList interactionList;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
}
