package de.unibi.agbi.biodwh2.core.io.mixml;

import java.util.ArrayList;

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
    public ArrayList<Integer> variableValueRef;
}
