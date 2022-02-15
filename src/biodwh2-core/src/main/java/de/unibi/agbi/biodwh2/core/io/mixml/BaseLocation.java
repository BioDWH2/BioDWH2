package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * A location on a sequence. Both begin and end can be a defined position, a fuzzy position, or undetermined.
 * <pre>
 * &lt;complexType name="baseLocation">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="startStatus" type="cvType"/>
 *           &lt;choice minOccurs="0">
 *             &lt;element name="begin" type="position"/>
 *             &lt;element name="beginInterval" type="interval"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="endStatus" type="cvType"/>
 *           &lt;choice minOccurs="0">
 *             &lt;element name="end" type="position"/>
 *             &lt;element name="endInterval" type="interval"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;element name="isLink" type="boolean" minOccurs="0"/>
 *         &lt;element name="resultingSequence" type="resultingSequenceType" minOccurs="0"/>
 *         &lt;element name="participantRef" type="int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class BaseLocation {
    public CvType startStatus;
    public Position begin;
    public Interval beginInterval;
    public CvType endStatus;
    public Position end;
    public Interval endInterval;
    public Boolean isLink;
    public ResultingSequenceType resultingSequence;
    public Integer participantRef;
}
