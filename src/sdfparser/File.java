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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class responsible for all file operations
 *
 * @author Łukasz Szeremeta 2017-2018
 * @author Dominik Tomaszuk 2017-2018
 */
public class File {

    /**
     * Filename
     */
    String filename;

    /**
     * File class constructor
     *
     * @param filename filename of input file
     *
     */
    public File(String filename) {
        this.filename = filename;
    }

    /**
     * Reads and retrieves data from the input file and then writes it to the
     * appropriate program structures
     *
     * @param c Compound object to which values from the file will be entered
     *
     */
    void parse(Compound c) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String pName = "";    // current property name
            boolean molfileReady = false;
            String[] tokens;

            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();

                if (strLine.startsWith("END", 3)) {
                    molfileReady = true;
                } else if (!molfileReady && !strLine.matches("M\\s+\\w+.*")) {
                    // TODO: V3000

                    tokens = strLine.split("\\s+");

                    if (tokens.length == 16) {
                        c.atoms.add(new Atom(tokens[3], Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                    }

                    // V2000, V3000; comment text exclusion
                    if ((tokens.length == 7 && !tokens[6].startsWith("V") && isInt(tokens[0]) || tokens.length == 6 && isInt(tokens[0]))) {
                        c.bonds.add(new Bond(Integer.parseInt(tokens[0]), Byte.parseByte(tokens[2]), Integer.parseInt(tokens[1]), Byte.parseByte(tokens[3])));
                    }
                } else if (molfileReady && !strLine.matches("M\\s+\\w+.*")) {
                    // SDF file parse
                    if (strLine.replaceAll("\\s+", "").startsWith("><")) {
                        pName = strLine.split("<")[1];
                        pName = pName.substring(0, pName.length() - 1);
                    } else if (strLine.startsWith("$$$$")) {
                        c.printChemSKOSCompound();
                        c.printChemSKOSAtomsAndBonds();
                        //c.printCypherCompound();
                        //c.printCypherAtoms();
                        //c.printCypherBonds();
                        c.clearAll();
                        molfileReady = false;
                    } else if (strLine.isEmpty()) {
                    } else if (!strLine.isEmpty()) {
                        // Database links, XML tags remove
                        switch (pName) {
                            case "Description":
                                // XML tags remove
                                c.addPropertyByName(pName, strLine.replaceAll("<[^>]+>", ""));
                                break;
                            case "ChEBI ID":
                                c.addPropertyByName("ChEBI Database Link", "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + strLine.substring(6));
                                break;
                            case "DrugBank Database Links":
                                c.addPropertyByName("DrugBank Database Link", "https://www.drugbank.ca/drugs/" + strLine);
                                break;
                            case "IntEnz Database Links":
                                strLine = strLine.replaceAll(" ", "+");
                                c.addPropertyByName("IntEnz Database Link", "http://www.ebi.ac.uk/intenz/query?q=" + strLine);
                                break;
                            case "HMDB Database Links":
                                // metabolites
                                c.addPropertyByName("HMDB Database Link", "http://www.hmdb.ca/metabolites/" + strLine);
                                break;
                            case "KEGG COMPOUND Database Links":
                                c.addPropertyByName("KEGG COMPOUND Database Link", "http://www.genome.jp/dbget-bin/www_bget?cpd:" + strLine);
                                break;
                            case "KEGG DRUG Database Links":
                                c.addPropertyByName("KEGG DRUG Database Link", "http://www.genome.jp/dbget-bin/www_bget?dr:" + strLine);
                                break;
                            case "Wikipedia Database Links":
                                c.addPropertyByName("Wikipedia Database Link", "https://en.wikipedia.org/wiki/" + strLine);
                                break;
                            case "Patent Database Links":
                                c.addPropertyByName("Patent Database Link", "https://worldwide.espacenet.com/searchResults?query=" + strLine);
                                break;
                            case "Rhea Database Links":
                                c.addPropertyByName("Rhea Database Link", "https://www.rhea-db.org/reaction?id=" + strLine);
                                break;
                            case "SABIO-RK Database Links":
                                c.addPropertyByName("SABIO-RK Database Link", "http://sabio.h-its.org/reacdetails.jsp?reactid=" + strLine);
                                break;
                            case "PubChem Database Links":
                                switch (strLine.substring(0, 3)) {
                                    case "CID":
                                        c.addPropertyByName("PubChem Database Compound Link", "https://pubchem.ncbi.nlm.nih.gov/compound/" + strLine.substring(5));
                                        break;
                                    case "SID":
                                        c.addPropertyByName("PubChem Database Substance Link", "https://pubchem.ncbi.nlm.nih.gov/substance/" + strLine.substring(5));
                                        break;
                                }
                                break;
                            case "PubMed Citation Links":
                                c.addPropertyByName("PubMed Citation Link", "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + strLine);
                                break;
                            case "UniProt Database Links":
                                c.addPropertyByName("UniProt Database Link", "https://www.uniprot.org/uniprot/" + strLine);
                                break;
                            default:
                                c.addPropertyByName(pName, strLine);
                        }

                    }
                }

            }
            in.close();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error while parsing file: " + e.toString());
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

