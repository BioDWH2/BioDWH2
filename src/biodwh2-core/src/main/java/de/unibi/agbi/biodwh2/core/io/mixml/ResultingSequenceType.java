package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Description of the mutated or transformed interactor sequence portion
 * <pre>
 * &lt;complexType name="resultingSequenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;sequence>
 *             &lt;element name="originalSequence" type="anyType"/>
 *             &lt;element name="newSequence" type="anyType"/>
 *             &lt;element name="xref" type="xref" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="xref" type="xref"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ResultingSequenceType {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> originalSequence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> newSequence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Xref> xref;
}
