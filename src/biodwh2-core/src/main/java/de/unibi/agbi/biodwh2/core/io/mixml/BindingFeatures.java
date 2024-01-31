package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * List all the features reported in the complex that are linked to each other.
 * <pre>
 * &lt;complexType name="bindingFeatures">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="participantFeatureRef" type="int" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class BindingFeatures {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "participantFeatureRef")
    public List<Integer> participantFeatureRef;
}
