package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * <pre>
 * &lt;complexType name="abstractParticipantCandidate">
 *   &lt;complexContent>
 *     &lt;extension base="participantCandidateParent">
 *       &lt;sequence>
 *         &lt;element name="featureList" type="abstractFeatureList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class AbstractParticipantCandidate extends ParticipantCandidateParent {
    @JacksonXmlElementWrapper
    public ArrayList<AbstractFeature> featureList;
}
