package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * A molecule participating in an interaction (complex, allsoteric interaction, ...). This participant is abstracted
 * from its experimental context.
 * <pre>
 * &lt;complexType name="abstractParticipant">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="interactorRef" type="int"/>
 *           &lt;element name="interactor" type="interactor"/>
 *           &lt;element name="interactionRef" type="int"/>
 *           &lt;element name="interactorCandidateList" type="abstractInteractorCandidateList"/>
 *         &lt;/choice>
 *         &lt;element name="biologicalRole" type="cvType" minOccurs="0"/>
 *         &lt;element name="featureList" type="abstractFeatureList" minOccurs="0"/>
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
public class AbstractParticipant {
    public Names names;
    public Xref xref;
    public Integer interactorRef;
    public Interactor interactor;
    public Integer interactionRef;
    public AbstractInteractorCandidateList interactorCandidateList;
    public CvType biologicalRole;
    @JacksonXmlElementWrapper(localName = "featureList")
    @JacksonXmlProperty(localName = "feature")
    public List<AbstractFeature> featureList;
    public StoichiometryType stoichiometry;
    public StoichiometryRangeType stoichiometryRange;
    @JacksonXmlElementWrapper(localName = "attributeList")
    @JacksonXmlProperty(localName = "attribute")
    public List<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
