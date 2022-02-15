package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

/**
 * A cooperative effect an interaction has on a subsequent interaction.
 * <pre>
 * &lt;complexType name="cooperativeEffectType">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="cooperativityEvidenceList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cooperativityEvidenceDescription" type="evidenceType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="affectedInteractionList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="anyType">
 *                 &lt;sequence>
 *                   &lt;element name="affectedInteractionRef" type="int" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="cooperativeEffectOutcome" type="cvType"/>
 *         &lt;element name="cooperativeEffectResponse" type="cvType" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class CooperativeEffectType {
    @JacksonXmlElementWrapper
    public ArrayList<EvidenceType> cooperativityEvidenceList;
    @JacksonXmlElementWrapper
    public ArrayList<Integer> affectedInteractionList;
    public CvType cooperativeEffectOutcome;
    public CvType cooperativeEffectResponse;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
}
