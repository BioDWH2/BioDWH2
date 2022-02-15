package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * The list of interactor candidates.
 * <pre>
 * &lt;complexType name="abstractInteractorCandidateList">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="moleculeSetType" type="cvType"/>
 *         &lt;element name="interactorCandidate" type="abstractParticipantCandidate" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class AbstractInteractorCandidateList {
    public CvType moleculeSetType;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "interactorCandidate")
    public List<AbstractParticipantCandidate> interactorCandidates;
}
