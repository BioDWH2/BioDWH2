package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * Participant of the inferred interaction.
 * <pre>
 * &lt;complexType name="inferredInteractionParticipant">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;choice>
 *         &lt;element name="participantRef" type="int"/>
 *         &lt;element name="participantFeatureRef" type="int"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class InferredInteractionParticipant {
    public Integer participantRef;
    public Integer participantFeatureRef;
}
