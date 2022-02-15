package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * List of experiments in which this interaction has been determined.
 * <pre>
 * &lt;complexType name="experimentList">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="experimentRef" type="int"/>
 *         &lt;element name="experimentDescription" type="experimentDescription"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ExperimentList {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<Integer> experimentRef;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ExperimentDescription> experimentDescription;
}
