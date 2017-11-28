/* 
 * The MIT License
 *
 * Copyright 2017 Łukasz Szeremeta.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sdfparser;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that stores information about chemical compound
 *
 * @author Łukasz Szeremeta 2017
 */
public class Compound {

    /**
     * Stores all properties of the chemical compound
     */
    private final Map<String, List<String>> properties = new HashMap<>();

    /**
     * Stores atoms data
     *
     */
    List<Atom> atoms = new ArrayList<>();

    /**
     * Stores bonds data
     *
     */
    List<Bond> bonds = new ArrayList<>();

    /**
     * Set compound property name
     *
     * @param propertyName property name (key)
     */
    void setPropertyName(String propertyName) {
        properties.put(propertyName, new ArrayList<>());
    }

    /**
     * Add properties values by property name
     *
     * @param propertyName property name (key)
     * @param pList properties list (values)
     */
    void addPropertiesByName(String propertyName, List<String> pList) {
        properties.put(propertyName, pList);
    }

    /**
     * Add single property value by property name
     *
     * @param propertyName property name (key)
     * @param propertyValue properties list (values)
     */
    void addPropertyByName(String propertyName, String propertyValue) {
        if (properties.get(propertyName) == null) {
            setPropertyName(propertyName);  // create new ArrayList if no ArrayList assigned
        }
        properties.get(propertyName).add(propertyValue); // adds property value to list
    }

    /**
     * Print properties keys and its values
     *
     */
    void printProperties() {
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Key = " + key);
            System.out.println("Values = " + values);
        }
    }

    /**
     * Print atoms data
     *
     */
    void printAtoms() {
        for (Atom atom : atoms) {
            System.out.println(atom.toString());
        }
    }

    /**
     * Print bonds data
     *
     */
    void printBonds() {
        for (Bond bond : bonds) {
            System.out.println("(" + atoms.get(bond.atom1 - 1).name + "[" + bond.atom1 + "])--" + bond.type + "--(" + atoms.get(bond.atom2 - 1).name + "[" + bond.atom2 + "])");
        }
    }

    /**
     * Get property values by property name
     *
     * @param propertyName property name (key)
     * @return list of property values
     */
    List getPropertiesByName(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Removes all stored properties, atoms and bonds data
     *
     */
    void clearAll() {
        properties.clear();
        atoms.clear();
        bonds.clear();
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
