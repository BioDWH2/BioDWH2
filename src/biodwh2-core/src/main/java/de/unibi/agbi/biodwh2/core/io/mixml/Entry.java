package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes one or more interactions as a self-contained unit. Multiple entries from different files can be
 * concatenated into a single entrySet.
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
    public Source source;
    @JacksonXmlElementWrapper
    public List<Availability> availabilityList;
    @JacksonXmlElementWrapper(localName = "experimentList")
    @JacksonXmlProperty(localName = "experimentDescription")
    public List<ExperimentDescription> experimentList;
    @JacksonXmlElementWrapper
    public List<Interactor> interactorList;
    public InteractionList interactionList;
    @JacksonXmlElementWrapper
    public List<Attribute> attributeList;
}
