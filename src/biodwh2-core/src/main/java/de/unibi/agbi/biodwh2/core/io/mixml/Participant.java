package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * A molecule participating in an interaction.
 * <pre>
 * &lt;complexType name="participant">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="interactorRef" type="int"/>
 *           &lt;element name="interactor" type="interactor"/>
 *           &lt;element name="interactionRef" type="int"/>
 *           &lt;element name="interactorCandidateList" type="interactorCandidateList"/>
 *         &lt;/choice>
 *         &lt;element name="participantIdentificationMethodList" type="participantIdentificationMethodList" minOccurs="0"/>
 *         &lt;element name="biologicalRole" type="cvType" minOccurs="0"/>
 *         &lt;element name="experimentalRoleList" type="experimentalRoleList" minOccurs="0"/>
 *         &lt;element name="experimentalPreparationList" type="experimentalPreparationList" minOccurs="0"/>
 *         &lt;element name="experimentalInteractorList" type="experimentalInteractorList" minOccurs="0"/>
 *         &lt;element name="featureList" type="featureList" minOccurs="0"/>
 *         &lt;element name="hostOrganismList" type="hostOrganismList" minOccurs="0"/>
 *         &lt;element name="confidenceList" type="confidenceList" minOccurs="0"/>
 *         &lt;element name="parameterList" type="parameterList" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="stoichiometry" type="stoichiometryType"/>
 *           &lt;element name="stoichiometryRange" type="stoichiometryRangeType"/>
 *         &lt;/choice>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Participant {
    public Names names;
    public Xref xref;
    public Integer interactorRef;
    public Interactor interactor;
    public Integer interactionRef;
    public InteractorCandidateList interactorCandidateList;
    @JacksonXmlElementWrapper
    public List<ParticipantIdentificationMethod> participantIdentificationMethodList;
    public CvType biologicalRole;
    @JacksonXmlElementWrapper
    public List<ExperimentalRole> experimentalRoleList;
    @JacksonXmlElementWrapper
    public List<ExperimentalPreparation> experimentalPreparationList;
    @JacksonXmlElementWrapper
    public List<ExperimentalInteractor> experimentalInteractorList;
    @JacksonXmlElementWrapper
    public List<Feature> featureList;
    @JacksonXmlElementWrapper
    public List<HostOrganism> hostOrganismList;
    @JacksonXmlElementWrapper
    public List<Confidence> confidenceList;
    @JacksonXmlElementWrapper
    public List<Parameter> parameterList;
    public StoichiometryType stoichiometry;
    public StoichiometryRangeType stoichiometryRange;
    @JacksonXmlElementWrapper
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
