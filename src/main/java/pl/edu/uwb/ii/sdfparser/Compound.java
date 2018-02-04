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
package pl.edu.uwb.ii.sdfparser;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that stores information about chemical compound
 *
 * @author Łukasz Szeremeta 2017-2018
 * @author Dominik Tomaszuk 2017-2018
 */
public class Compound {
    /**
     * Consts for UUID
     */
    public static final byte STRIKE = 0;
    public static final byte UNDERLINE = 1;

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
        String query_str = "CREATE (c" + addUUID(UNDERLINE) + ":Compound {";

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            query_str += key.replaceAll("\\s+|-", "") + ": ";

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
     * Print main compound data in CVME
     *
     */
    void printChemSKOSCompound() {
        String val_tmp = "";
        String query_str = "";

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            //query_str += key.replaceAll("\\s+", "");
            if ("SMILES".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> skos:notation " + printValueAsNumberOrString(value) + "^^chemskos:SMILES .\n";
            } else if ("Formulae".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> skos:hiddenLabel " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> skos:definition " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("InChIKey".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> dbp:inchikey " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("InChI".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> dbo:inchi " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("Mass".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> dbo:molecularWeight " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("IUPAC Names".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> skos:prefLabel " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("CAS Registry Numbers".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> dbo:casNumber " + printValueAsNumberOrString(value) + "@en .\n";
            } else if ("Synonyms".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> skos:altLabel ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + "@en, ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ "@en .\n";
                }
            } else if ("PubMed Citation Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("PubMed Citation Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("KNApSAcK Database Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("LIPID MAPS instance Database Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("UniProt Database Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("Rhea Database Links".equals(key)) {
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso ";
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp += printValueAsNumberOrString(value) + ", ";
                    }
                    val_tmp = val_tmp.substring(0, val_tmp.length() - 2);
                    query_str += val_tmp + " .\n";
                    val_tmp = "";
                } else {
                    String value = values.get(0);
                    query_str += printValueAsNumberOrString(value)+ " .\n";
                }
            } else if ("KEGG COMPOUND Database Links".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso " + printValueAsNumberOrString(value) + " .\n";
            } else if ("Patent Database Links".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> cvme:patent " + printValueAsNumberOrString(value) + " .\n";
            } else if ("PubChem Database Compound Links".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso " + printValueAsNumberOrString(value) + " .\n";
            } else if ("PubChem Database Substance Links".equals(key)) {
                String value = values.get(0);
                query_str += "<urn:uuid:" + addUUID(STRIKE) + "> rdfs:seeAlso " + printValueAsNumberOrString(value) + " .\n";
            }
            
        }
        System.out.println(query_str);
    }

    /**
     * Detect if value is number, URL or String
     *
     * @param value Value to check
     * @return value, if number, 'value', if string, <value> if URL
     */
    private String printValueAsNumberOrString(String value) {
        if (isNumber(value)) {
            return value;
        } else if(isURL(value)) {
            return "<" + value + ">";
        } else {
            return "'" + value + "'";
        }
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
     * Print atoms and bonds as skos:example property in CVME
     *
     */
    void printChemSKOSAtomsAndBonds() {
        System.out.println("<urn:uuid:" + addUUID(STRIKE) + "> skos:example \"\"\"\n");
        System.out.println("  CT\n");
        int atomsSize = atoms.size();
        int bondsSize = bonds.size();
        if (atomsSize >= 0 && atomsSize <= 9) {
            System.out.print("  "+atomsSize);
        } else if (atomsSize >= 10 && atomsSize <= 99) {
            System.out.print(" "+atomsSize);
        } else {
            System.out.print(atomsSize);
        }
        if (bondsSize >= 0 && bondsSize <= 9) {
            System.out.println("  "+bondsSize+"  0  0  0  0            999 V2000");
        } else if (bondsSize >= 10 && bondsSize <= 99) {
            System.out.println(" "+bondsSize+"  0  0  0  0            999 V2000");
        } else {
            System.out.println(bondsSize+"  0  0  0  0            999 V2000");
        }
        for (Atom atom : atoms) {
            float x = atom.x;
            float y = atom.y;
            float z = atom.z;
            String symbol = atom.symbol;
            String line = "";
            String temp = "";
            if (x < 0) {
                temp = String.format("%.4g%n", x);
                if (temp.length() == 7 ) {
                    temp += "0";
                } else if (temp.length() == 6 ) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", x);
                if (temp.length() == 6 ) {
                    temp += "0";
                } else if (temp.length() == 5 ) {
                    temp += "00";
                }
                line += "    " + temp;
            }
            if (y < 0) {
                temp = String.format("%.4g%n", y);
                if (temp.length() == 7 ) {
                    temp += "0";
                } else if (temp.length() == 6 ) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", y);
                if (temp.length() == 6 ) {
                    temp += "0";
                } else if (temp.length() == 5 ) {
                    temp += "00";
                }
                line += "    " + temp;
            }
            if (z < 0) {
                temp = String.format("%.4g%n", z);
                if (temp.length() == 7 ) {
                    temp += "0";
                } else if (temp.length() == 6 ) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", z);
                if (temp.length() == 6 ) {
                    temp += "0";
                } else if (temp.length() == 5 ) {
                    temp += "00";
                }
                line += "    " + temp;
            }
            if (symbol.length() == 1) {
                line += " " + symbol + "   ";
            } else {
                line += " " + symbol + "  ";
            }
            line += "0  0  0  0  0  0  0  0  0  0  0  0";
            line = line.replace("\n", "").replace(",", ".");
            System.out.println(line);
        }
        for (Bond bond : bonds) {
            int atom1 = bond.atom1;
            int atom2 = bond.atom2;
            byte type = bond.type;
            byte stereo = bond.stereo;
            String line = "";
            if (atom1 <= 9) {
                line += "  " + atom1;
            } else if (atom1 >= 10 && atom1 <= 99) {
                line += " " + atom1;
            } else {
                line += "" + atom1;
            }
            if (atom2 <= 9) {
                line += "  " + atom2;
            } else if (atom2 >= 10 && atom2 <= 99) {
                line += " " + atom2;
            } else {
                line += "" + atom2;
            }
            line += "  " + type;
            line += "  " + stereo + "  0  0  0";
            System.out.println(line);
        }
        System.out.print("M  END");
        System.out.println("\"\"\" .");
    }
    
    /**
     * Print SMILES form SDF
     *
     */
    void printSMILES() {
        String query_str = "";

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if ("SMILES".equals(key)) {
                query_str = values.get(0);
            } 
            
        }
        System.out.println(query_str);
    }
    
        /**
     * Print InChI form SDF
     *
     */
    void printInChI() {
        String query_str = "";

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if ("InChI".equals(key)) {
                query_str = values.get(0);
            } 
            
        }
        System.out.println(query_str);
    }
    
    /**
     * Print atoms data and Compound-Atom relations in Cypher
     *
     */
    void printCypherAtoms() {
        int it = 1;
        for (Atom atom : atoms) {
            System.out.println("CREATE (a" + it + addUUID(UNDERLINE) + ":Atom {symbol: '" + atom.symbol + "', x: " + atom.x + ", y: " + atom.y + ", z: " + atom.z + "})");
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
            query_str += "\n(c" + addUUID(UNDERLINE) + ")-[:RELATED]->(a" + i + addUUID(UNDERLINE) + "),";
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
            query_str += "\n(a" + bond.atom1 + addUUID(UNDERLINE) + ")-[:BOND_WITH {";

            if (!"0".equals(bondTypeNumberToString(bond.type))) {
                query_str += "type: \"" + bondTypeNumberToString(bond.type) + "\"";
            }

            if (!"0".equals(bondTypeNumberToString(bond.type)) && !"0".equals(bondStereoNumberToString(bond.stereo, bond.type))) {
                query_str += ", ";
            }

            if (!"0".equals(bondStereoNumberToString(bond.stereo, bond.type))) {
                query_str += "stereo: " + bondStereoNumberToString(bond.stereo, bond.type);
            }

            query_str += "}]->(a" + bond.atom2 + addUUID(UNDERLINE) + "),";
        }
        query_str = query_str.substring(0, query_str.length() - 1);
        System.out.println(query_str);
    }

    /**
     * Change bond types numbers to string value
     *
     * @param type Bond type number
     * @return String value of bond type, 0 if not supported
     */
    String bondTypeNumberToString(byte type) {
        switch (type) {
            case 1:
                return "single";
            case 2:
                return "double";
            case 3:
                return "triple";
            // [Query] Values 4 through 8 are for SSS queries only.
            case 4:
                return "aromatic";
            case 5:
                return "single or double";
            case 6:
                return "single or aromatic";
            case 7:
                return "double or aromatic";
            case 8:
                return "any";
            default:
                return "0";
        }
    }

    /**
     * Change bond stereo numbers to string value
     *
     * @param stereo Bond stereo number
     * @param type Bond type number
     * @return String value of bond stereo with double quotes, false or 0 if not
     * supported
     */
    String bondStereoNumberToString(byte stereo, byte type) {
        switch (type) {
            case 1:
                // single bond
                switch (stereo) {
                    case 0:
                        // not stereo
                        return "false";
                    case 1:
                        return "\"up\"";
                    case 4:
                        return "\"either\"";
                    case 6:
                        return "\"down\"";
                    default:
                        return "0";
                }
            case 2:
                // double bond
                switch (stereo) {
                    case 0:
                        // Use x-, y-, z-coords from atom block to determine cis or trans,
                        return "\"not determined\"";
                    case 3:
                        return "\"cis or trans (either) double bond\"";
                    default:
                        return "0";
                }
            default:
                return "0";
        }
    }

    /**
     * Prepare UUID to use in Cypher output
     *
     */
    String addUUID(byte dash) {
        if (dash == 0) {
            return uuid.toString();
        } else {
            return "_" + uuid.toString().replace('-', '_');
        }
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
  
    private boolean isURL(String s) {
        try {
            URL url = new URL(s);
            url.toURI();
        } catch (Exception exception) {
            return false;
        }
        return true;
}

    @Override
    public String toString() {
        return properties.toString();
    }
}
