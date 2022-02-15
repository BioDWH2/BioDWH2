package de.unibi.agbi.biodwh2.core.io.mixml;

import java.util.ArrayList;

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
    public ArrayList<AbstractParticipantCandidate> interactorCandidate;
}
