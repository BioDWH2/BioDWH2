package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * A list of cooperative effects this interaction has on subsequent interactions, either through an allosteric or
 * pre-assembly effect.
 * <pre>
 * &lt;complexType name="cooperativeEffectList">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="allostery" type="allostery"/>
 *           &lt;element name="preassembly" type="cooperativeEffectType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class CooperativeEffectList {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Allostery> allostery;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<CooperativeEffectType> preassembly;
}
