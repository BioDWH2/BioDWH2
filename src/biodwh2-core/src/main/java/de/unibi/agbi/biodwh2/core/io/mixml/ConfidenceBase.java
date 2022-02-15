package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * A confidence value.
 * <pre>
 * &lt;complexType name="confidenceBase">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="unit" type="openCvType"/>
 *         &lt;element name="value">
 *           &lt;simpleType>
 *             &lt;restriction base="string">
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ConfidenceBase {
    public OpenCvType unit;
    public String value;
}
