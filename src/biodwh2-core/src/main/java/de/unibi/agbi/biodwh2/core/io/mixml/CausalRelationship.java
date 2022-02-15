package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * The causal relationship between a participant source and a participant target.
 * <pre>
 * &lt;complexType name="causalRelationship">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceParticipantRef" type="int"/>
 *         &lt;element name="causalityStatement" type="openCvType"/>
 *         &lt;element name="targetParticipantRef" type="int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class CausalRelationship {
    public int sourceParticipantRef;
    public OpenCvType causalityStatement;
    public int targetParticipantRef;
}
