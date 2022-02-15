package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * List of interactions
 * <pre>
 * &lt;complexType name="interactionList">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="interaction" type="interaction"/>
 *           &lt;element name="abstractInteraction" type="abstractInteraction"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class InteractionList {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<Interaction> interaction;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<AbstractInteraction> abstractInteraction;
}
