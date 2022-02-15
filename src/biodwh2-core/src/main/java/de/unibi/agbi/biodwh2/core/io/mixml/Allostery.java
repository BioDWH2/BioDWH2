package de.unibi.agbi.biodwh2.core.io.mixml;

/**
 * In case the cooperative mechanism is allostery.
 * <pre>
 * &lt;complexType name="allostery">
 *   &lt;complexContent>
 *     &lt;extension base="cooperativeEffectType">
 *       &lt;sequence>
 *         &lt;element name="allostericMoleculeRef" type="int"/>
 *         &lt;choice>
 *           &lt;element name="allostericEffectorRef" type="int"/>
 *           &lt;element name="allostericModificationRef" type="int"/>
 *         &lt;/choice>
 *         &lt;element name="allostericMechanism" type="cvType" minOccurs="0"/>
 *         &lt;element name="allosteryType" type="cvType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Allostery extends CooperativeEffectType {
    public int allostericMoleculeRef;
    public Integer allostericEffectorRef;
    public Integer allostericModificationRef;
    public CvType allostericMechanism;
    public CvType allosteryType;
}
