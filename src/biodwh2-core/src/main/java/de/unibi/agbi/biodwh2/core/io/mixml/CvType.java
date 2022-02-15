package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * Reference to an external controlled vocabulary.
 * <pre>
 * &lt;complexType name="cvType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names"/>
 *         &lt;element name="xref" type="xref"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class CvType {
    public Names names;
    public Xref xref;
}
