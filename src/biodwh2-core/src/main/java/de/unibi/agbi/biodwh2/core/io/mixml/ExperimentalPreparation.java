package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * This element is controlled by the PSI-MI controlled vocabulary "experimentalPreparation", root term id MI:0346.
 * <pre>
 * &lt;complexType name="experimentalPreparation">
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
public class ExperimentalPreparation extends CvType {
    @JacksonXmlElementWrapper
    public ArrayList<Integer> experimentRefList;
}
