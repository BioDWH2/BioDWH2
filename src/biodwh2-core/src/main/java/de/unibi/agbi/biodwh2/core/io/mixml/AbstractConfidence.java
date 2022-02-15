package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * A confidence value for a complex or other 'abstract' interaction. It can refer to its original publication/review.
 * <pre>
 * &lt;complexType name="abstractConfidence">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="openCvType"/>
 *         &lt;element name="value">
 *           &lt;simpleType>
 *             &lt;restriction base="string">
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="bibref" type="bibref" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class AbstractConfidence {
    public OpenCvType type;
    public String value;
    public Bibref bibref;
}
