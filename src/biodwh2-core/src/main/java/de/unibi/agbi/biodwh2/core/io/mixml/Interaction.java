package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;
import java.util.List;

/**
 * A molecular interaction described with some experimental context.
 * <pre>
 * &lt;complexType name="interaction">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="availabilityRef" type="int"/>
 *           &lt;element name="availability" type="availability"/>
 *         &lt;/choice>
 *         &lt;element name="experimentList" type="experimentList"/>
 *         &lt;element name="participantList" type="participantList"/>
 *         &lt;element name="inferredInteractionList" type="inferredInteractionList" minOccurs="0"/>
 *         &lt;element name="interactionType" type="cvType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="modelled" type="boolean" minOccurs="0"/>
 *         &lt;element name="intraMolecular" type="boolean" minOccurs="0"/>
 *         &lt;element name="negative" type="boolean" minOccurs="0"/>
 *         &lt;element name="confidenceList" type="confidenceList" minOccurs="0"/>
 *         &lt;element name="parameterList" type="parameterList" minOccurs="0"/>
 *         &lt;element name="experimentalVariableValueList" type="experimentalVariableValueList" minOccurs="0"/>
 *         &lt;element name="causalRelationshipList" type="causalRelationshipList" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="imexId" type="string" />
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Interaction {
    public Names names;
    public Xref xref;
    public Integer availabilityRef;
    public Availability availability;
    public ExperimentList experimentList;
    @JacksonXmlElementWrapper
    public List<Participant> participantList;
    @JacksonXmlElementWrapper
    public List<InferredInteraction> inferredInteractionList;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "interactionType")
    public List<CvType> interactionType;
    public Boolean modelled;
    public Boolean intraMolecular;
    public Boolean negative;
    @JacksonXmlElementWrapper
    public List<Confidence> confidenceList;
    @JacksonXmlElementWrapper
    public List<Parameter> parameterList;
    @JacksonXmlElementWrapper
    public List<ExperimentalVariableValues> experimentalVariableValueList;
    @JacksonXmlElementWrapper
    public List<CausalRelationship> causalRelationshipList;
    @JacksonXmlElementWrapper
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public String imexId;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
