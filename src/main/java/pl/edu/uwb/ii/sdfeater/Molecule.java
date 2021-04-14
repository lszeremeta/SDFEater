/*
 * The MIT License
 *
 * Copyright 2017-2019 Łukasz Szeremeta.
 * Copyright 2018-2019 Dominik Tomaszuk.
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
package pl.edu.uwb.ii.sdfeater;

import com.google.gson.GsonBuilder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static pl.edu.uwb.ii.sdfeater.SDFEater.jenaModel;
import static pl.edu.uwb.ii.sdfeater.SDFEater.periodic_table_data;

/**
 * Class that stores information about chemical molecule
 *
 * @author Łukasz Szeremeta 2017-2021
 * @author Dominik Tomaszuk 2017-2018
 */
class Molecule {

    /**
     * Consts for UUID
     */
    private static final byte STRIKE = 0;
    private static final byte UNDERLINE = 1;

    /**
     * Incremental ID
     */
    private static final AtomicLong idCounter = new AtomicLong();
    /**
     * Stores atoms data
     */
    final List<Atom> atoms = new ArrayList<>();

    /**
     * Stores bonds data
     */
    final List<Bond> bonds = new ArrayList<>();
    /**
     * Stores all properties of the chemical molecule
     */
    private final Map<String, List<String>> properties = new HashMap<>();

    /**
     * Subject base of molecule
     */
    String subjectBase = "https://example.com/molecule#entity";

    private UUID uuid;

    Molecule() {
        uuid = UUID.randomUUID();
    }

    /**
     * Create incremental ID
     */
    private static String createID() {
        return String.valueOf(idCounter.getAndIncrement());
    }

    /**
     * Set molecule property name
     *
     * @param propertyName property name (key)
     */
    private void setPropertyName(String propertyName) {
        properties.put(propertyName, new ArrayList<>());
    }

    /**
     * Add properties values by property name
     *
     * @param propertyName property name (key)
     * @param pList        properties list (values)
     */
    void addPropertiesByName(String propertyName, List<String> pList) {
        properties.put(propertyName, pList);
    }

    /**
     * Add single property value by property name
     *
     * @param propertyName  property name (key)
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
     */
    void printAtoms() {
        for (Atom atom : atoms) {
            System.out.println(atom.toString());
        }
    }

    /**
     * Print bonds data
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
     * Print main molecule data in Cypher
     */
    void printCypherMolecule() {
        StringBuilder val_tmp = new StringBuilder();
        StringBuilder query_str = new StringBuilder("CREATE (c" + addUUID(UNDERLINE) + ":Molecule {");

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            query_str.append(key.replaceAll("\\s+|-", "").replaceAll("CAS Registry Numbers|CAS_NUMBER", "CASNumber")).append(": ");

            if (values.size() > 1) {
                query_str.append("[");
                for (String value : values) {
                    val_tmp.append(printValueAsNumberOrStringInCypher(value));
                }
                val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                query_str.append(val_tmp).append("], ");
                val_tmp = new StringBuilder();
            } else {
                String value = values.get(0);
                query_str.append(printValueAsNumberOrStringInCypher(value));
            }
        }

        query_str = new StringBuilder(query_str.substring(0, query_str.length() - 2) + "})");

        System.out.println(query_str);
    }

    /**
     * Print main molecule data in CVME
     */
    void printChemSKOSMolecule() {
        StringBuilder val_tmp = new StringBuilder();
        StringBuilder query_str = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            //query_str += key.replaceAll("\\s+", "");
            if ("SMILES".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> skos:notation ").append(printValueAsNumberOrStringCVME(value)).append("^^chemskos:SMILES .\n");
            } else if ("Formulae".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> skos:hiddenLabel ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> skos:definition ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("InChIKey".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> dbp:inchikey ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("InChI".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> dbo:inchi ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("Mass".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> dbo:molecularWeight ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("IUPAC Names".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> skos:prefLabel ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("CAS Registry Numbers".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> dbo:casNumber ").append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
            } else if ("Synonyms".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> skos:altLabel ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append("@en, ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append("@en .\n");
                }
            } else if ("PubMed Citation Links".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append(", ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append(" .\n");
                }
            } else if ("KNApSAcK Database Links".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append(", ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append(" .\n");
                }
            } else if ("LIPID MAPS instance Database Links".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append(", ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append(" .\n");
                }
            } else if ("UniProt Database Links".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append(", ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append(" .\n");
                }
            } else if ("Rhea Database Links".equals(key)) {
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ");
                if (values.size() > 1) {
                    for (String value : values) {
                        val_tmp.append(printValueAsNumberOrStringCVME(value)).append(", ");
                    }
                    val_tmp = new StringBuilder(val_tmp.substring(0, val_tmp.length() - 2));
                    query_str.append(val_tmp).append(" .\n");
                    val_tmp = new StringBuilder();
                } else {
                    String value = values.get(0);
                    query_str.append(printValueAsNumberOrStringCVME(value)).append(" .\n");
                }
            } else if ("KEGG COMPOUND Database Links".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ").append(printValueAsNumberOrStringCVME(value)).append(" .\n");
            } else if ("Patent Database Links".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> cvme:patent ").append(printValueAsNumberOrStringCVME(value)).append(" .\n");
            } else if ("PubChem Database Molecule Links".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ").append(printValueAsNumberOrStringCVME(value)).append(" .\n");
            } else if ("PubChem Database Substance Links".equals(key)) {
                String value = values.get(0);
                query_str.append("<urn:uuid:").append(addUUID(STRIKE)).append("> rdfs:seeAlso ").append(printValueAsNumberOrStringCVME(value)).append(" .\n");
            }

        }
        System.out.println(query_str);
    }

    /**
     * Add main molecule data to Jena model
     *
     * @param subject subject type
     */
    void addToJenaModel(SDFEater.Subject subject) {
        Resource me = ResourceFactory.createResource();

        if (subject == SDFEater.Subject.iri) {
            me = ResourceFactory.createResource(subjectBase + createID());
        } else if (subject == SDFEater.Subject.uuid) {
            me = ResourceFactory.createResource("urn:uuid:" + uuid);
        } else if (subject == SDFEater.Subject.bnode) {
            me = ResourceFactory.createResource();
        }

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {

            String key = entry.getKey();
            List<String> values = entry.getValue();
            jenaModel.add(me, RDF.type, "http://schema.org/MolecularEntity");

            if ("ChEBI ID".equals(key)) {
                String value = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/url");
                jenaModel.add(me, p, value);
            } else if ("DATABASE_ID".equals(key) || "DRUGBANK_ID".equals(key)) {
                String value = "https://go.drugbank.com/drugs/" + values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/url");
                jenaModel.add(me, p, value);
            } else if ("SMILES".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/smiles");
                jenaModel.add(me, p, value);
            } else if ("Formulae".equals(key) || "FORMULA".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/molecularFormula");
                jenaModel.add(me, p, value);
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/description");
                jenaModel.add(me, p, value);
            } else if ("InChIKey".equals(key) || "INCHI_KEY".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/inChIKey");
                jenaModel.add(me, p, value);
            } else if ("InChI".equals(key) || "INCHI_IDENTIFIER".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/inChI");
                jenaModel.add(me, p, value);
            } else if ("Mass".equals(key) || "MOLECULAR_WEIGHT".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/molecularWeight");
                jenaModel.add(me, p, value);
            } else if ("IUPAC Names".equals(key) || "JCHEM_IUPAC".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/iupacName");
                jenaModel.add(me, p, value);
            } else if ("CAS Registry Numbers".equals(key) || "CAS_NUMBER".equals((key))) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/identifier");
                jenaModel.add(me, p, value);
            } else if ("Synonyms".equals(key) || "SYNONYMS".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/alternateName");
                jenaModel.add(me, p, value);
            } else if ("COMMON_NAME".equals(key) || "GENERIC_NAME".equals(key)) {
                String value = values.get(0);
                Property p = jenaModel.createProperty("http://schema.org/name");
                jenaModel.add(me, p, value);
            }
        }

    }

    /**
     * Construct molecule data in JSON-LD
     *
     * @param subject subject type
     * @return Molecule data in JSON-LD
     */
    StringBuilder constructJSONLDMolecule(SDFEater.Subject subject) {
        StringBuilder output_str = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if ("ChEBI ID".equals(key)) {
                String value = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + values.get(0);
                output_str.append("      \"url\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("DATABASE_ID".equals(key) || "DRUGBANK_ID".equals(key)) {
                String value = "https://go.drugbank.com/drugs/" + values.get(0);
                output_str.append("      \"url\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("SMILES".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"smiles\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("Formulae".equals(key) || "FORMULA".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"molecularFormula\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"description\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("InChIKey".equals(key) || "INCHI_KEY".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"inChIKey\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("InChI".equals(key) || "INCHI_IDENTIFIER".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"inChI\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("Mass".equals(key) || "MOLECULAR_WEIGHT".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"molecularWeight\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("IUPAC Names".equals(key) || "JCHEM_IUPAC".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"iupacName\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("CAS Registry Numbers".equals(key) || "CAS_NUMBER".equals((key))) {
                String value = values.get(0);
                output_str.append("      \"identifier\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("Synonyms".equals(key) || "SYNONYMS".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"alternateName\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            } else if ("COMMON_NAME".equals(key) || "GENERIC_NAME".equals(key)) {
                String value = values.get(0);
                output_str.append("      \"name\" : ").append(printValueAsNumberOrStringInJSONLD(value)).append(",\n");
            }
        }

        if (output_str.length() > 0) {
            StringBuilder start_part = new StringBuilder();

            start_part.append("    {\n");
            if (subject == SDFEater.Subject.iri) {
                start_part.append("      \"@id\" : " + printValueAsNumberOrStringInJSONLD(subjectBase + createID()) + ",\n");
            } else if (subject == SDFEater.Subject.uuid) {
                start_part.append("      \"@id\" : \"urn:uuid:" + uuid + "\",\n");
            } else if (subject == SDFEater.Subject.bnode) {
                start_part.append("      \"@id\" : \"_:b" + createID() + "\",\n");
            }
            start_part.append("      \"@type\" : \"http://schema.org/MolecularEntity\",\n");

            output_str.insert(0, start_part);

            output_str.setLength(output_str.length() - 2);
            output_str.append("\n    },\n");
        }

        return output_str;
    }

    /**
     * Print main molecule data in RDFa
     *
     * @param subject subject type
     */
    void printRDFaMolecule(SDFEater.Subject subject) {
        StringBuilder output_str = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if ("ChEBI ID".equals(key)) {
                String value = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + values.get(0);
                output_str.append("      <a href='").append(htmlEscape(value, true)).append("' rel='schema:url'>").append(htmlEscape(value, false)).append("</a>\n");
            } else if ("DATABASE_ID".equals(key) || "DRUGBANK_ID".equals(key)) {
                String value = "https://go.drugbank.com/drugs/" + values.get(0);
                output_str.append("      <a href='").append(htmlEscape(value, true)).append("' rel='schema:url'>").append(htmlEscape(value, false)).append("</a>\n");
            } else if ("SMILES".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:smiles'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Formulae".equals(key) || "FORMULA".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:molecularFormula'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:description'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("InChIKey".equals(key) || "INCHI_KEY".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:inChIKey'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("InChI".equals(key) || "INCHI_IDENTIFIER".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:inChI'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Mass".equals(key) || "MOLECULAR_WEIGHT".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:molecularWeight'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("IUPAC Names".equals(key) || "JCHEM_IUPAC".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:iupacName'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("CAS Registry Numbers".equals(key) || "CAS_NUMBER".equals((key))) {
                String value = values.get(0);
                output_str.append("      <div property='schema:identifier'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Synonyms".equals(key) || "SYNONYMS".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:alternateName'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("COMMON_NAME".equals(key) || "GENERIC_NAME".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div property='schema:name'>").append(htmlEscape(value, false)).append("</div>\n");
            }
        }

        if (output_str.length() > 0) {
            if (subject == SDFEater.Subject.iri) {
                String mID = createID();
                System.out.print("    <div typeof='schema:MolecularEntity' about='" + htmlEscape(subjectBase + mID, true) + "'");

                if (subjectBase.contains("#")) {
                    System.out.print(" id='" + subjectBase.substring(subjectBase.lastIndexOf('#') + 1) + mID + "'");
                }

                System.out.print(">\n");

            } else if (subject == SDFEater.Subject.uuid) {
                System.out.println("    <div typeof='schema:MolecularEntity' about='urn:uuid:" + uuid + "'>");
            } else if (subject == SDFEater.Subject.bnode) {
                System.out.println("    <div typeof='schema:MolecularEntity' about='_:b" + createID() + "'>");
            }

            System.out.print(output_str);
            System.out.println("    </div>");
        }

    }

    /**
     * Print main molecule data in Microdata
     *
     * @param subject subject type
     */
    void printMicrodataMolecule(SDFEater.Subject subject) {
        StringBuilder output_str = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if ("ChEBI ID".equals(key)) {
                String value = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + values.get(0);
                output_str.append("      <a href='").append(htmlEscape(value, true)).append("' itemprop='url'>").append(htmlEscape(value, false)).append("</a>\n");
            } else if ("DATABASE_ID".equals(key) || "DRUGBANK_ID".equals(key)) {
                String value = "https://go.drugbank.com/drugs/" + values.get(0);
                output_str.append("      <a href='").append(htmlEscape(value, true)).append("' itemprop='url'>").append(htmlEscape(value, false)).append("</a>\n");
            } else if ("SMILES".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='smiles'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Formulae".equals(key) || "FORMULA".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='molecularFormula'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Definition".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='description'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("InChIKey".equals(key) || "INCHI_KEY".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='inChIKey'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("InChI".equals(key) || "INCHI_IDENTIFIER".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='inChI'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Mass".equals(key) || "MOLECULAR_WEIGHT".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='molecularWeight'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("IUPAC Names".equals(key) || "JCHEM_IUPAC".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='iupacName'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("CAS Registry Numbers".equals(key) || "CAS_NUMBER".equals((key))) {
                String value = values.get(0);
                output_str.append("      <div itemprop='identifier'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("Synonyms".equals(key) || "SYNONYMS".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='alternateName'>").append(htmlEscape(value, false)).append("</div>\n");
            } else if ("COMMON_NAME".equals(key) || "GENERIC_NAME".equals(key)) {
                String value = values.get(0);
                output_str.append("      <div itemprop='name'>").append(htmlEscape(value, false)).append("</div>\n");
            }
        }

        if (output_str.length() > 0) {
            if (subject == SDFEater.Subject.iri) {
                String mID = createID();
                System.out.print("    <div itemscope itemtype='http://schema.org/MolecularEntity' itemid='" + htmlEscape(subjectBase + mID, true) + "'");

                if (subjectBase.contains("#")) {
                    System.out.print(" id='" + subjectBase.substring(subjectBase.lastIndexOf('#') + 1) + mID + "'");
                }

                System.out.print(">\n");
            } else if (subject == SDFEater.Subject.uuid) {
                System.out.println("    <div itemscope itemtype='http://schema.org/MolecularEntity' itemid='urn:uuid:" + uuid + "'>");
            } else if (subject == SDFEater.Subject.bnode) {
                System.out.println("    <div itemscope itemtype='http://schema.org/MolecularEntity' itemid='_:b" + createID() + "'>");
            }

            System.out.print(output_str);
            System.out.println("    </div>");
        }

    }

    /**
     * Detect if value is number, URL or String
     *
     * @param value Value to check
     * @return value, if number, 'value', if string, <value> if URL
     */
    private String printValueAsNumberOrStringCVME(String value) {
        if (isNumber(value)) {
            return value;
        } else if (isURL(value)) {
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
     * Detect if value is number and use this in JSON-LD output
     *
     * @param value Value to check
     * @return valid JSON value
     */
    private String printValueAsNumberOrStringInJSONLD(String value) {
        if (isNumber(value)) {
            return value;
        } else {
            return new GsonBuilder().disableHtmlEscaping().create().toJson(value);
        }
    }

    /**
     * HTML escape
     * <p>
     * Escape "&", "<" and ">" characters to safe one. If quote is true, escape " and ' too.
     *
     * @param value Value to escape
     * @param quote Escape quotation marks too? True - escape " and ', false - don't escape.
     * @return Escaped HTML value
     */
    private String htmlEscape(String value, boolean quote) {
        String escapedValue = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        if (quote) {
            escapedValue = escapedValue.replace("\"", "&quot;");
            escapedValue = escapedValue.replace("'", "&#x27;");
        }

        return escapedValue;
    }

    /**
     * Print atoms and bonds as skos:example property in CVME
     */
    void printChemSKOSAtomsAndBonds() {
        System.out.println("<urn:uuid:" + addUUID(STRIKE) + "> skos:example \"\"\"\n");
        System.out.println("  CT\n");
        int atomsSize = atoms.size();
        int bondsSize = bonds.size();
        if (atomsSize >= 0 && atomsSize <= 9) {
            System.out.print("  " + atomsSize);
        } else if (atomsSize >= 10 && atomsSize <= 99) {
            System.out.print(" " + atomsSize);
        } else {
            System.out.print(atomsSize);
        }
        if (bondsSize >= 0 && bondsSize <= 9) {
            System.out.println("  " + bondsSize + "  0  0  0  0            999 V2000");
        } else if (bondsSize >= 10 && bondsSize <= 99) {
            System.out.println(" " + bondsSize + "  0  0  0  0            999 V2000");
        } else {
            System.out.println(bondsSize + "  0  0  0  0            999 V2000");
        }
        for (Atom atom : atoms) {
            float x = atom.x;
            float y = atom.y;
            float z = atom.z;
            String symbol = atom.symbol;
            String line = "";
            String temp;
            if (x < 0) {
                temp = String.format("%.4g%n", x);
                if (temp.length() == 7) {
                    temp += "0";
                } else if (temp.length() == 6) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", x);
                if (temp.length() == 6) {
                    temp += "0";
                } else if (temp.length() == 5) {
                    temp += "00";
                }
                line += "    " + temp;
            }
            if (y < 0) {
                temp = String.format("%.4g%n", y);
                if (temp.length() == 7) {
                    temp += "0";
                } else if (temp.length() == 6) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", y);
                if (temp.length() == 6) {
                    temp += "0";
                } else if (temp.length() == 5) {
                    temp += "00";
                }
                line += "    " + temp;
            }
            if (z < 0) {
                temp = String.format("%.4g%n", z);
                if (temp.length() == 7) {
                    temp += "0";
                } else if (temp.length() == 6) {
                    temp += "00";
                }
                line += "   " + temp;
            } else {
                temp = String.format("%.4g%n", z);
                if (temp.length() == 6) {
                    temp += "0";
                } else if (temp.length() == 5) {
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
     * Print atoms data and Molecule-Atom relations in Cypher
     */
    void printCypherAtoms() {
        if (!atoms.isEmpty()) {
            int it = 1;
            for (Atom atom : atoms) {
                System.out.println("CREATE (a" + it + addUUID(UNDERLINE) + ":Atom {symbol: '" + atom.symbol + "', x: " + atom.x + ", y: " + atom.y + ", z: " + atom.z + "})");
                it++;
            }

            printCypherMoleculeAtomRelation();
        }
    }

    /**
     * Print atoms data with additional periodic table data and Molecule-Atom
     * relations in Cypher
     */
    void printCypherAtomsWithPeriodicTableData() {
        if (!atoms.isEmpty()) {
            StringBuilder str = new StringBuilder();
            int it = 1;
            for (Atom atom : atoms) {
                str.append("CREATE (a").append(it).append(addUUID(UNDERLINE)).append(":Atom {symbol: '").append(atom.symbol).append("', x: ").append(atom.x).append(", y: ").append(atom.y).append(", z: ").append(atom.z);

                try {
                    for (Map.Entry<String, Object> entry : getAtomPeriodicDataByAtomSymbol(atom.symbol).entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        str.append(", ").append(key).append(": ");
                        if (isNumber(value.toString())) {
                            str.append(value);
                        } else {
                            str.append("'").append(value).append("'");
                        }
                    }
                } catch (Exception e) {
                    //System.err.println("WARNING: No additional data could be found in the periodic table for " + atom.symbol);
                }

                str.append("})\n");
                it++;
            }
            System.out.print(str);

            printCypherMoleculeAtomRelation();
        }
    }

    /**
     * Gets additional periodic table data by atom symbol
     *
     * @param symbol Atom symbol
     * @return All periodic table data
     */
    private Map<String, Object> getAtomPeriodicDataByAtomSymbol(String symbol) {
        return periodic_table_data.get(symbol);
    }

    /**
     * Print Molecule-Atom relations in Cypher
     */
    private void printCypherMoleculeAtomRelation() {
        if (!atoms.isEmpty()) {
            StringBuilder query_str = new StringBuilder("CREATE");

            for (int i = 1; i <= atoms.size(); i++) {
                query_str.append("\n(c").append(addUUID(UNDERLINE)).append(")-[:RELATED]->(a").append(i).append(addUUID(UNDERLINE)).append("),");
            }
            query_str = new StringBuilder(query_str.substring(0, query_str.length() - 1));
            System.out.println(query_str);
        }
    }

    /**
     * Print bonds data in Cypher
     */
    void printCypherBonds() {
        if (!bonds.isEmpty()) {
            StringBuilder query_str = new StringBuilder("CREATE");
            for (Bond bond : bonds) {
                query_str.append("\n(a").append(bond.atom1).append(addUUID(UNDERLINE)).append(")-[:BOND_WITH {");

                if (!"0".equals(bondTypeNumberToString(bond.type))) {
                    query_str.append("type: \"").append(bondTypeNumberToString(bond.type)).append("\"");
                }

                if (!"0".equals(bondTypeNumberToString(bond.type)) && !"0".equals(bondStereoNumberToString(bond.stereo, bond.type))) {
                    query_str.append(", ");
                }

                if (!"0".equals(bondStereoNumberToString(bond.stereo, bond.type))) {
                    query_str.append("stereo: ").append(bondStereoNumberToString(bond.stereo, bond.type));
                }

                query_str.append("}]->(a").append(bond.atom2).append(addUUID(UNDERLINE)).append("),");
            }
            query_str = new StringBuilder(query_str.substring(0, query_str.length() - 1));
            System.out.println(query_str);
        }

    }

    /**
     * Change bond types numbers to string value
     *
     * @param type Bond type number
     * @return String value of bond type, 0 if not supported
     */
    private String bondTypeNumberToString(byte type) {
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
     * @param type   Bond type number
     * @return String value of bond stereo with double quotes, false or 0 if not
     * supported
     */
    private String bondStereoNumberToString(byte stereo, byte type) {
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
     */
    private String addUUID(byte dash) {
        if (dash == 0) {
            return uuid.toString();
        } else {
            return "_" + uuid.toString().replace('-', '_');
        }
    }

    /**
     * Prepare program structures for new molecule
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
        String regex = "-?\\d+(\\.\\d+)?";
        return s.matches(regex);
    }

    private boolean isURL(String s) {
        try {
            URL url = new URL(s);
            url.toURI();
        } catch (MalformedURLException | URISyntaxException exception) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
