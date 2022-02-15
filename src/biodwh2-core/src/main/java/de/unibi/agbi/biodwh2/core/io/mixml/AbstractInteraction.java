package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * An 'abstract' molecular interaction - e.g - stable complexes, allosteric interaction, .... These interactions are
 * abstracted from their experimental context and represent biological entities.
 * <pre>
 * &lt;complexType name="abstractInteraction">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;element name="participantList" type="abstractParticipantList"/>
 *         &lt;element name="bindingFeatureList" type="bindingFeatureList" minOccurs="0"/>
 *         &lt;element name="interactionType" type="cvType" minOccurs="0"/>
 *         &lt;element name="intraMolecular" type="boolean" minOccurs="0"/>
 *         &lt;element name="confidenceList" type="abstractConfidenceList" minOccurs="0"/>
 *         &lt;element name="parameterList" type="abstractParameterList" minOccurs="0"/>
 *         &lt;element name="organism" type="bioSource" minOccurs="0"/>
 *         &lt;element name="interactorType" type="cvType" minOccurs="0"/>
 *         &lt;element name="evidenceType" type="cvType" minOccurs="0"/>
 *         &lt;element name="cooperativeEffectList" type="cooperativeEffectList" minOccurs="0"/>
 *         &lt;element name="causalRelationshipList" type="causalRelationshipList" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class AbstractInteraction {
    public Names names;
    public Xref xref;
    @JacksonXmlElementWrapper(localName = "participantList")
    @JacksonXmlProperty(localName = "participant")
    public List<AbstractParticipant> participantList;
    @JacksonXmlElementWrapper(localName = "bindingFeatureList")
    @JacksonXmlProperty(localName = "bindingFeature")
    public List<BindingFeatures> bindingFeatureList;
    public CvType interactionType;
    @JsonProperty(defaultValue = "false")
    public Boolean intraMolecular;
    @JacksonXmlElementWrapper(localName = "confidenceList")
    @JacksonXmlProperty(localName = "confidence")
    public List<AbstractConfidence> confidenceList;
    @JacksonXmlElementWrapper(localName = "parameterList")
    @JacksonXmlProperty(localName = "parameter")
    public List<AbstractParameter> parameterList;
    public BioSource organism;
    public CvType interactorType;
    public CvType evidenceType;
    public CooperativeEffectList cooperativeEffectList;
    @JacksonXmlElementWrapper(localName = "causalRelationshipList")
    @JacksonXmlProperty(localName = "causalRelationship")
    public List<CausalRelationship> causalRelationshipList;
    @JacksonXmlElementWrapper(localName = "attributeList")
    @JacksonXmlProperty(localName = "attribute")
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
