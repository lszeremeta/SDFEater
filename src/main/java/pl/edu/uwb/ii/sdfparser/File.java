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

import java.io.BufferedReader;
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
     * @param format Output format: c - Cypher, r - cvme, s - smiles, n - inchi
     * @param urls Try to generate full database URLs instead of IDs
     * (true/false)
     * @param periodic Map with additional atoms data from periodic table for
     * cypher format (true/false)
     *
     */
    void parse(Compound c, char format, boolean urls, boolean periodic) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
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
                        switch (format) {
                            case 'c':
                                c.printCypherCompound();
                                if (periodic) {
                                    c.printCypherAtomsWithPeriodicTableData();
                                } else {
                                    c.printCypherAtoms();
                                }
                                c.printCypherBonds();
                                break;
                            case 'r':
                                c.printChemSKOSCompound();
                                c.printChemSKOSAtomsAndBonds();
                                break;
                            case 's':
                                c.printSMILES();
                                break;
                            case 'n':
                                c.printInChI();
                                break;
                            default:
                                break;
                        }
                        c.clearAll();
                        molfileReady = false;
                    } else if (strLine.isEmpty()) {
                    } else if (!strLine.isEmpty()) {
                        if (urls) {
                            // Database links, XML tags remove
                            switch (pName) {
                                case "Definition":
                                    // XML tags remove
                                    c.addPropertyByName(pName, strLine.replaceAll("<[^>]+>", ""));
                                    break;
                                case "Agricola Citation Links":
                                    c.addPropertyByName(pName, "https://agricola.nal.usda.gov/cgi-bin/Pwebrecon.cgi?Search_Arg=" + strLine + "&DB=local&CNT=25&Search_Code=GKEY%5E&STARTDB=AGRIDB");
                                    break;
                                case "ArrayExpress Database Links":
                                    c.addPropertyByName(pName, "https://www.ebi.ac.uk/arrayexpress/experiments/" + strLine);
                                    break;
                                case "BioModels Database Links":
                                    c.addPropertyByName(pName, "https://www.ebi.ac.uk/biomodels-main/" + strLine);
                                    break;
                                case "ChEBI ID":
                                    c.addPropertyByName(pName, "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + strLine.substring(6));
                                    break;
                                case "DrugBank Database Links":
                                    c.addPropertyByName(pName, "https://www.drugbank.ca/drugs/" + strLine);
                                    break;
                                case "ECMDB Database Links":
                                    c.addPropertyByName(pName, "http://ecmdb.ca/compounds/" + strLine);
                                    break;
                                case "HMDB Database Links":
                                    // metabolites
                                    c.addPropertyByName(pName, "http://www.hmdb.ca/metabolites/" + strLine);
                                    break;
                                case "IntAct Database Links":
                                    c.addPropertyByName(pName, "https://www.ebi.ac.uk/intact/interaction/" + strLine);
                                    break;
                                case "IntEnz Database Links":
                                    strLine = strLine.replaceAll(" ", "+");
                                    c.addPropertyByName(pName, "http://www.ebi.ac.uk/intenz/query?q=" + strLine);
                                    break;
                                case "KEGG COMPOUND Database Links":
                                    c.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?cpd:" + strLine);
                                    break;
                                case "KEGG DRUG Database Links":
                                    c.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?dr:" + strLine);
                                    break;
                                case "KEGG GLYCAN Database Links":
                                    c.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?gl:" + strLine);
                                    break;
                                case "KNApSAcK Database Links":
                                    c.addPropertyByName(pName, "http://kanaya.naist.jp/knapsack_jsp/information.jsp?word=" + strLine);
                                    break;
                                case "LIPID MAPS instance Database Links":
                                    c.addPropertyByName(pName, "http://www.lipidmaps.org/data/LMSDRecord.php?LMID=" + strLine);
                                    break;
                                case "MetaCyc Database Links":
                                    c.addPropertyByName(pName, "https://metacyc.org/compound?orgid=META&id=" + strLine);
                                    break;
                                case "Patent Database Links":
                                    c.addPropertyByName(pName, "https://worldwide.espacenet.com/searchResults?query=" + strLine);
                                    break;
                                case "PDBeChem Database Links":
                                    c.addPropertyByName(pName, "http://www.ebi.ac.uk/pdbe-srv/pdbechem/chemicalCompound/show/" + strLine);
                                    break;
                                case "PubChem Database Links":
                                    // custom key value for compound and substance links
                                    switch (strLine.substring(0, 3)) {
                                        case "CID":
                                            c.addPropertyByName("PubChem Database Compound Links", "https://pubchem.ncbi.nlm.nih.gov/compound/" + strLine.substring(5));
                                            break;
                                        case "SID":
                                            c.addPropertyByName("PubChem Database Substance Links", "https://pubchem.ncbi.nlm.nih.gov/substance/" + strLine.substring(5));
                                            break;
                                    }
                                    break;
                                case "PubMed Central Citation Links":
                                    c.addPropertyByName(pName, "https://www.ncbi.nlm.nih.gov/pmc/articles/" + strLine + "/");
                                    break;
                                case "PubMed Citation Links":
                                    c.addPropertyByName(pName, "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + strLine);
                                    break;
                                case "Reactome Database Links":
                                    c.addPropertyByName(pName, "https://reactome.org/content/detail/" + strLine);
                                    break;
                                case "RESID Database Links":
                                    c.addPropertyByName(pName, "http://pir.georgetown.edu/cgi-bin/resid?id=" + strLine);
                                    break;
                                case "Rhea Database Links":
                                    c.addPropertyByName(pName, "https://www.rhea-db.org/reaction?id=" + strLine);
                                    break;
                                case "SABIO-RK Database Links":
                                    c.addPropertyByName(pName, "http://sabio.h-its.org/reacdetails.jsp?reactid=" + strLine);
                                    break;
                                case "UM-BBD compID Database Links":
                                    c.addPropertyByName(pName, "http://eawag-bbd.ethz.ch/servlets/pageservlet?ptype=c&compID=" + strLine);
                                    break;
                                case "UniProt Database Links":
                                    c.addPropertyByName(pName, "https://www.uniprot.org/uniprot/" + strLine);
                                    break;
                                case "Wikipedia Database Links":
                                    c.addPropertyByName(pName, "https://en.wikipedia.org/wiki/" + strLine);
                                    break;
                                case "YMDB Database Links":
                                    c.addPropertyByName(pName, "http://www.ymdb.ca/compounds/" + strLine);
                                    break;
                                default:
                                    c.addPropertyByName(pName, strLine);
                            }
                        } else {
                            c.addPropertyByName(pName, strLine);
                        }
                    }
                }
            }

            if (format == 'c') {
                System.out.println(';');
            }

            br.close();
            fstream.close();
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
