package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * A set of experimental parameter/conditions values applied together and for which this interaction occurs.
 * <pre>
 * &lt;complexType name="experimentalVariableValues">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="variableValueRef" type="int" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ExperimentalVariableValues {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "variableValueRef")
    public List<Integer> variableValueRef;
}
