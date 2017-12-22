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
import java.util.UUID;

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

    UUID uuid;

    public Compound() {
        uuid = UUID.randomUUID();
    }

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
            try {
                System.out.println("(" + atoms.get(bond.atom1 - 1).symbol + "[" + bond.atom1 + "])--" + bond.type + "--(" + atoms.get(bond.atom2 - 1).symbol + "[" + bond.atom2 + "])");
            } catch (Exception e) {
                System.err.println("Error in printBonds(): " + e.toString());
            }

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
     * Print main compound data in Cypher
     *
     */
    void printCypherCompound() {
        String val_tmp = "";
        String query_str = "CREATE (c" + addUUID() + ":Compound {";

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            query_str += key.replaceAll("\\s+", "") + ": ";

            if (values.size() > 1) {
                query_str += "[";
                for (String value : values) {
                    val_tmp += printValueAsNumberOrStringInCypher(value);
                }
                val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                query_str += val_tmp + "], ";
                val_tmp = "";
            } else {
                String value = values.get(0);
                query_str += printValueAsNumberOrStringInCypher(value);
            }
        }

        query_str = query_str.substring(0, query_str.length() - 2) + "})";

        System.out.println(query_str);
    }

    /**
     * Detect if value is number and use this in Cypher output
     *
     * @param value Value to check
     * @return value, if number and 'value', if not
     */
    private String printValueAsNumberOrStringInCypher(String value) {
        if (isNumber(value)) {
            return value + ", ";
        } else {
            return "'" + value + "', ";
        }
    }

    /**
     * Print atoms data and Compound-Atom relations in Cypher
     *
     */
    void printCypherAtoms() {
        int it = 1;
        for (Atom atom : atoms) {
            System.out.println("CREATE (a" + it + addUUID() + ":Atom {symbol: '" + atom.symbol + "', x: " + atom.x + ", y: " + atom.y + ", z: " + atom.z + "})");
            it++;
        }

        printCypherCompoundAtomRelation();
    }

    /**
     * Print Compound-Atom relations in Cypher
     *
     */
    void printCypherCompoundAtomRelation() {
        String query_str = "CREATE";

        for (int i = 1; i <= atoms.size(); i++) {
            query_str += "\n(c" + addUUID() + ")-[:RELATED]->(a" + i + addUUID() + "),";
        }
        query_str = query_str.substring(0, query_str.length() - 1);
        System.out.println(query_str);
    }

    /**
     * Print bonds data in Cypher
     *
     */
    void printCypherBonds() {
        String query_str = "CREATE";
        for (Bond bond : bonds) {
            query_str += "\n(a" + bond.atom1 + addUUID() + ")-[:BOND_WITH {type: " + bond.type + ", stereo: " + bond.stereo + "}]->(a" + bond.atom2 + addUUID() + "),";
        }
        query_str = query_str.substring(0, query_str.length() - 1);
        System.out.println(query_str);
    }

    /**
     * Prepare UUID to use in Cypher output
     *
     */
    String addUUID() {
        return "_" + uuid.toString().replace('-', '_');
    }

    /**
     * Prepare program structures for new compound
     *
     */
    void clearAll() {
        properties.clear();
        atoms.clear();
        bonds.clear();
        uuid = UUID.randomUUID();
    }

    /**
     * Detect if String is number
     *
     * @param s String to check
     * @return true if number, false if not
     */
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
