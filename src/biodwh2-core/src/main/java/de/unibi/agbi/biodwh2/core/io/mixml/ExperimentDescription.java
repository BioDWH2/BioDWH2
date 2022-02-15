package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

/**
 * Describes one set of experimental parameters.
 * <pre>
 * &lt;complexType name="experimentDescription">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="bibref" type="bibref"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;element name="hostOrganismList" type="hostOrganismList" minOccurs="0"/>
 *         &lt;element name="interactionDetectionMethod" type="cvType"/>
 *         &lt;element name="participantIdentificationMethod" type="cvType" minOccurs="0"/>
 *         &lt;element name="featureDetectionMethod" type="cvType" minOccurs="0"/>
 *         &lt;element name="confidenceList" type="confidenceList" minOccurs="0"/>
 *         &lt;element name="variableParameterList" type="variableParameterList" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ExperimentDescription {
    public Names names;
    public Bibref bibref;
    public Xref xref;
    @JacksonXmlElementWrapper
    public ArrayList<HostOrganism> hostOrganismList;
    public CvType interactionDetectionMethod;
    public CvType participantIdentificationMethod;
    public CvType featureDetectionMethod;
    @JacksonXmlElementWrapper
    public ArrayList<Confidence> confidenceList;
    @JacksonXmlElementWrapper
    public ArrayList<VariableParameter> variableParameterList;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
