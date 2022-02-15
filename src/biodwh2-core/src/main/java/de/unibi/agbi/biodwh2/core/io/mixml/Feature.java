package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A feature, e.g. domain, on a sequence.
 * <pre>
 * &lt;complexType name="feature">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;element name="featureType" type="cvType" minOccurs="0"/>
 *         &lt;element name="featureDetectionMethod" type="cvType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="experimentRefList" type="experimentRefList" minOccurs="0"/>
 *         &lt;element name="featureRangeList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="anyType">
 *                 &lt;sequence>
 *                   &lt;element name="featureRange" type="baseLocation" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="featureRole" type="cvType" minOccurs="0"/>
 *         &lt;element name="parameterList" type="parameterList" minOccurs="0"/>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Feature {
    public Names names;
    public Xref xref;
    public CvType featureType;
    public List<CvType> featureDetectionMethod;
    @JacksonXmlElementWrapper
    public ArrayList<Integer> experimentRefList;
    @JacksonXmlElementWrapper
    public ArrayList<BaseLocation> featureRangeList;
    public CvType featureRole;
    @JacksonXmlElementWrapper
    public ArrayList<Parameter> parameterList;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
