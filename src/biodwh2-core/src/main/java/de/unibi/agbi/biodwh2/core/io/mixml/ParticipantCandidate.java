package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * <pre>
 * &lt;complexType name="participantCandidate">
 *   &lt;complexContent>
 *     &lt;extension base="participantCandidateParent">
 *       &lt;sequence>
 *         &lt;element name="featureList" type="featureList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ParticipantCandidate extends ParticipantCandidateParent {
    @JacksonXmlElementWrapper
    public List<Feature> featureList;
}
