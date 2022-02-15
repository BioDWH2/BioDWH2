package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Experimental method to determine the interactors involved in the interaction. This element is controlled by the
 * PSI-MI controlled vocabulary "participant identification method", root term id MI:0002.
 * <pre>
 * &lt;complexType name="participantIdentificationMethod">
 *   &lt;complexContent>
 *     &lt;extension base="cvType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="experimentRefList" type="experimentRefList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class ParticipantIdentificationMethod extends CvType {
    @JacksonXmlElementWrapper
    public List<Integer> experimentRefList;
}
