package de.unibi.agbi.biodwh2.core.io.mixml;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

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
    @XmlElement(type = Integer.class)
    public ArrayList<Integer> participantFeatureRef;
}
