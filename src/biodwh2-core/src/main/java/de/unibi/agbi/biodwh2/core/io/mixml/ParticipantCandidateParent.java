package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * A molecule which is part of a molecule set (MI:1304) participating in an interaction. This molecule does not
 * interacts with the other participant candidates. A molecule set is a group of molecules linked by a high degree of
 * similarity of sequence and/or function and not easily separated by participant identification methods. It means that
 * we cannot determine for sure which molecules of the molecule set is the participant of this interaction.
 * <pre>
 * &lt;complexType name="participantCandidateParent">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="interactorRef" type="int"/>
 *           &lt;element name="interactor" type="interactor"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ParticipantCandidateParent {
    public Integer interactorRef;
    public Interactor interactor;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
