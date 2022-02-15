package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * <pre>
 * &lt;complexType name="hostOrganism">
 *   &lt;complexContent>
 *     &lt;extension base="bioSource">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="experimentRefList" type="experimentRefList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class HostOrganism extends BioSource {
    @JacksonXmlElementWrapper
    public List<Integer> experimentRefList;
}
