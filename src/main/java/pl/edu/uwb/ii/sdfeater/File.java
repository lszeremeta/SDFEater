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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;

import static pl.edu.uwb.ii.sdfeater.SDFEater.jenaModel;

/**
 * Class responsible for all file operations
 *
 * @author Łukasz Szeremeta 2017-2021
 * @author Dominik Tomaszuk 2018
 */
class File {

    /**
     * Filename
     */
    private final String filename;

    /**
     * File class constructor
     *
     * @param filename filename of input file
     */
    File(String filename) {
        this.filename = filename;
    }

    /**
     * Reads and retrieves data from the input file and then writes it to the
     * appropriate program structures
     *
     * @param molecule Molecule object to which values from the file will be entered
     * @param format   Output format
     * @param subject  Subject type
     */
    void parse(Molecule molecule, SDFEater.Format format, SDFEater.Subject subject) {
        StringBuilder output_str = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            String pName = "";    // current property name
            boolean molfileReady = false;
            String[] tokens;

            /* Do something BEFORE file reading */
            switch (format) {
                // JSON-LD with HTML
                case jsonldhtml:
                    output_str.append("<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "  <head>\n" +
                            "    <title>Example Document</title>\n" +
                            "    <script type=\"application/ld+json\">\n" +
                            "{\n" +
                            "  \"@graph\" : [\n" +
                            "    {\n" +
                            "      \"@id\": \"https://github.com/lszeremeta/SDFEater\",\n" +
                            "      \"@type\": \"http://schema.org/Organization\",\n" +
                            "      \"http://schema.org/name\": \"SDFEater\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"@id\": \"#\",\n" +
                            "      \"@type\": \"http://schema.org/Dataset\",\n" +
                            "      \"http://schema.org/about\": {\n" +
                            "        \"@id\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                            "      },\n" +
                            "      \"http://schema.org/description\": \"This is a dataset of molecules generated by SDFEater.\",\n" +
                            "      \"http://schema.org/keywords\": [\n" +
                            "        \"molecules\",\n" +
                            "        \"cheminformatics\",\n" +
                            "        \"chemical compounds\"\n" +
                            "      ],\n" +
                            "      \"http://schema.org/license\": {\n" +
                            "        \"@id\": \"http://opendatacommons.org/licenses/pddl/1.0/\"\n" +
                            "      },\n" +
                            "      \"http://schema.org/name\": \"Molecules\",\n" +
                            "      \"http://schema.org/creator\": {\n" +
                            "        \"@id\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                            "      },\n" +
                            "      \"http://schema.org/temporal\": \"" + Year.now().toString() + "\",\n" +
                            "      \"http://schema.org/url\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                            "    },\n");
                    break;
                case jsonld:
                    output_str.append(
                            "{\n" +
                                    "  \"@graph\" : [\n" +
                                    "    {\n" +
                                    "      \"@id\": \"https://github.com/lszeremeta/SDFEater\",\n" +
                                    "      \"@type\": \"http://schema.org/Organization\",\n" +
                                    "      \"http://schema.org/name\": \"SDFEater\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"@id\": \"#\",\n" +
                                    "      \"@type\": \"http://schema.org/Dataset\",\n" +
                                    "      \"http://schema.org/about\": {\n" +
                                    "        \"@id\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                                    "      },\n" +
                                    "      \"http://schema.org/description\": \"This is a dataset of molecules generated by SDFEater.\",\n" +
                                    "      \"http://schema.org/keywords\": [\n" +
                                    "        \"molecules\",\n" +
                                    "        \"cheminformatics\",\n" +
                                    "        \"chemical compounds\"\n" +
                                    "      ],\n" +
                                    "      \"http://schema.org/license\": {\n" +
                                    "        \"@id\": \"http://opendatacommons.org/licenses/pddl/1.0/\"\n" +
                                    "      },\n" +
                                    "      \"http://schema.org/name\": \"Molecules\",\n" +
                                    "      \"http://schema.org/creator\": {\n" +
                                    "        \"@id\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                                    "      },\n" +
                                    "      \"http://schema.org/temporal\": \"" + Year.now().toString() + "\",\n" +
                                    "      \"http://schema.org/url\": \"https://github.com/lszeremeta/SDFEater\"\n" +
                                    "    },\n");
                    break;
                // RDFa
                case rdfa:
                    System.out.println("<!DOCTYPE html>");
                    System.out.println("<html lang='en'>");
                    System.out.println("  <head>");
                    System.out.println("    <title>Example Document</title>");
                    System.out.println("  </head>");
                    System.out.println("  <body vocab='http://schema.org/'>");
                    System.out.println("    <div typeof='schema:Dataset'>\n" +
                            "      <div rel='schema:creator'>\n" +
                            "        <div typeof='schema:Organization' about='https://github.com/lszeremeta/SDFEater'>\n" +
                            "          <div property='schema:name' content='SDFEater'></div>\n" +
                            "        </div>\n" +
                            "      </div>\n" +
                            "      <div property='schema:keywords' content='cheminformatics'></div>\n" +
                            "      <div property='schema:keywords' content='molecules'></div>\n" +
                            "      <div property='schema:keywords' content='chemical compounds'></div>\n" +
                            "      <div property='schema:temporal' content='" + Year.now().toString() + "'></div>\n" +
                            "      <div property='schema:name' content='Molecules'></div>\n" +
                            "      <div rel='schema:license' resource='http://opendatacommons.org/licenses/pddl/1.0/'></div>\n" +
                            "      <div property='schema:description' content='This is a dataset of molecules generated by SDFEater.'></div>\n" +
                            "      <div rel='schema:about' resource='https://github.com/lszeremeta/SDFEater'></div>\n" +
                            "      <div property='schema:url' content='https://github.com/lszeremeta/SDFEater'></div>\n" +
                            "    </div>");
                    break;
                // Microdata
                case microdata:
                    System.out.println("<!DOCTYPE html>");
                    System.out.println("<html lang='en'>");
                    System.out.println("  <head>");
                    System.out.println("    <title>Example Document</title>");
                    System.out.println("  </head>");
                    System.out.println("  <body>");
                    System.out.println("    <div itemscope itemtype='http://schema.org/Dataset'>\n" +
                            "      <div itemprop='name' content='Molecules'></div>\n" +
                            "      <div itemprop='keywords' content='cheminformatics'></div>\n" +
                            "      <div itemprop='keywords' content='molecules'></div>\n" +
                            "      <div itemprop='keywords' content='chemical compounds'></div>\n" +
                            "      <div itemprop='temporal' content='" + Year.now().toString() + "'></div>\n" +
                            "      <div itemprop='url' content='https://github.com/lszeremeta/SDFEater'></div>\n" +
                            "      <div itemprop='description' content='This is a dataset of molecules generated by SDFEater.'></div>\n" +
                            "      <div itemprop='creator' itemscope itemtype='http://schema.org/Organization'>\n" +
                            "        <div itemprop='name' content='SDFEater'></div>\n" +
                            "      </div>\n" +
                            "      <div itemprop='license' content='http://opendatacommons.org/licenses/pddl/1.0/'></div>\n" +
                            "    </div>");
                    break;
                default:
                    break;
            }

            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim().replace("\\", "\\\\").replace("'", "\\'");

                if (strLine.startsWith("END", 3)) {
                    molfileReady = true;
                } else if (!molfileReady && !strLine.matches("M\\s+\\w+.*")) {
                    // TODO: V3000

                    tokens = strLine.split("\\s+");

                    if (tokens.length == 16) {
                        molecule.atoms.add(new Atom(tokens[3], Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                    }

                    // V2000, V3000; comment text exclusion
                    if ((tokens.length == 7 && !tokens[6].startsWith("V") && isInt(tokens[0]) || tokens.length == 6 && isInt(tokens[0]))) {
                        molecule.bonds.add(new Bond(Integer.parseInt(tokens[0]), Byte.parseByte(tokens[2]), Integer.parseInt(tokens[1]), Byte.parseByte(tokens[3])));
                    }
                } else if (molfileReady && !strLine.matches("M\\s+\\w+.*")) {
                    // SDF file parse
                    if (strLine.replaceAll("\\s+", "").startsWith("><")) {
                        pName = strLine.split("<")[1];
                        pName = pName.substring(0, pName.length() - 1);
                    } else if (strLine.startsWith("$$$$")) {
                        switch (format) {
                            case cypher:
                            case cypheru:
                                molecule.printCypherMolecule();
                                molecule.printCypherAtoms();
                                molecule.printCypherBonds();
                                System.out.println(';');
                                break;
                            case cypherp:
                            case cypherup:
                                molecule.printCypherMolecule();
                                molecule.printCypherAtomsWithPeriodicTableData();
                                molecule.printCypherBonds();
                                System.out.println(';');
                                break;
                            case cvme:
                                molecule.printChemSKOSMolecule();
                                molecule.printChemSKOSAtomsAndBonds();
                                break;
                            case smiles:
                                molecule.printSMILES();
                                break;
                            case inchi:
                                molecule.printInChI();
                                break;
                            case turtle:
                            case ntriples:
                            case rdfxml:
                            case rdfthrift:
                                molecule.addToJenaModel(subject);
                                break;
                            case jsonldhtml:
                            case jsonld:
                                output_str.append(molecule.constructJSONLDMolecule(subject));
                                break;
                            case rdfa:
                                molecule.printRDFaMolecule(subject);
                                break;
                            case microdata:
                                molecule.printMicrodataMolecule(subject);
                                break;
                            default:
                                break;
                        }
                        molecule.clearAll();
                        molfileReady = false;
                        //} else if (strLine.isEmpty()) {
                    } else if (!strLine.isEmpty()) {
                        if (format == SDFEater.Format.cypheru || format == SDFEater.Format.cypherup || format == SDFEater.Format.cvme) {
                            // Database links
                            switch (pName) {
                                case "Agricola Citation Links":
                                    molecule.addPropertyByName(pName, "https://agricola.nal.usda.gov/cgi-bin/Pwebrecon.cgi?Search_Arg=" + strLine + "&DB=local&CNT=25&Search_Code=GKEY%5E&STARTDB=AGRIDB");
                                    break;
                                case "ArrayExpress Database Links":
                                    molecule.addPropertyByName(pName, "https://www.ebi.ac.uk/arrayexpress/experiments/" + strLine);
                                    break;
                                case "BioModels Database Links":
                                    molecule.addPropertyByName(pName, "https://www.ebi.ac.uk/biomodels-main/" + strLine);
                                    break;
                                case "ChEBI ID":
                                    molecule.addPropertyByName(pName, "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + strLine.substring(6));
                                    break;
                                case "DrugBank Database Links":
                                    molecule.addPropertyByName(pName, "https://www.drugbank.ca/drugs/" + strLine);
                                    break;
                                case "ECMDB Database Links":
                                    molecule.addPropertyByName(pName, "http://ecmdb.ca/compounds/" + strLine);
                                    break;
                                case "HMDB Database Links":
                                    // metabolites
                                    molecule.addPropertyByName(pName, "http://www.hmdb.ca/metabolites/" + strLine);
                                    break;
                                case "IntAct Database Links":
                                    molecule.addPropertyByName(pName, "https://www.ebi.ac.uk/intact/interaction/" + strLine);
                                    break;
                                case "IntEnz Database Links":
                                    strLine = strLine.replaceAll(" ", "+");
                                    molecule.addPropertyByName(pName, "http://www.ebi.ac.uk/intenz/query?q=" + strLine);
                                    break;
                                case "KEGG COMPOUND Database Links":
                                    molecule.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?cpd:" + strLine);
                                    break;
                                case "KEGG DRUG Database Links":
                                    molecule.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?dr:" + strLine);
                                    break;
                                case "KEGG GLYCAN Database Links":
                                    molecule.addPropertyByName(pName, "http://www.genome.jp/dbget-bin/www_bget?gl:" + strLine);
                                    break;
                                case "KNApSAcK Database Links":
                                    molecule.addPropertyByName(pName, "http://kanaya.naist.jp/knapsack_jsp/information.jsp?word=" + strLine);
                                    break;
                                case "LIPID MAPS instance Database Links":
                                    molecule.addPropertyByName(pName, "http://www.lipidmaps.org/data/LMSDRecord.php?LMID=" + strLine);
                                    break;
                                case "MetaCyc Database Links":
                                    molecule.addPropertyByName(pName, "https://metacyc.org/compound?orgid=META&id=" + strLine);
                                    break;
                                case "Patent Database Links":
                                    molecule.addPropertyByName(pName, "https://worldwide.espacenet.com/searchResults?query=" + strLine);
                                    break;
                                case "PDBeChem Database Links":
                                    molecule.addPropertyByName(pName, "http://www.ebi.ac.uk/pdbe-srv/pdbechem/chemicalCompound/show/" + strLine);
                                    break;
                                case "PubChem Database Links":
                                    // custom key value for compound and substance links
                                    switch (strLine.substring(0, 3)) {
                                        case "CID":
                                            molecule.addPropertyByName("PubChem Database Molecule Links", "https://pubchem.ncbi.nlm.nih.gov/compound/" + strLine.substring(5));
                                            break;
                                        case "SID":
                                            molecule.addPropertyByName("PubChem Database Substance Links", "https://pubchem.ncbi.nlm.nih.gov/substance/" + strLine.substring(5));
                                            break;
                                    }
                                    break;
                                case "PubMed Central Citation Links":
                                    molecule.addPropertyByName(pName, "https://www.ncbi.nlm.nih.gov/pmc/articles/" + strLine + "/");
                                    break;
                                case "PubMed Citation Links":
                                    molecule.addPropertyByName(pName, "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + strLine);
                                    break;
                                case "Reactome Database Links":
                                    molecule.addPropertyByName(pName, "https://reactome.org/content/detail/" + strLine);
                                    break;
                                case "RESID Database Links":
                                    molecule.addPropertyByName(pName, "http://pir.georgetown.edu/cgi-bin/resid?id=" + strLine);
                                    break;
                                case "Rhea Database Links":
                                    molecule.addPropertyByName(pName, "https://www.rhea-db.org/reaction?id=" + strLine);
                                    break;
                                case "SABIO-RK Database Links":
                                    molecule.addPropertyByName(pName, "http://sabio.h-its.org/reacdetails.jsp?reactid=" + strLine);
                                    break;
                                case "UM-BBD compID Database Links":
                                    molecule.addPropertyByName(pName, "http://eawag-bbd.ethz.ch/servlets/pageservlet?ptype=c&compID=" + strLine);
                                    break;
                                case "UniProt Database Links":
                                    molecule.addPropertyByName(pName, "https://www.uniprot.org/uniprot/" + strLine);
                                    break;
                                case "Wikipedia Database Links":
                                    molecule.addPropertyByName(pName, "https://en.wikipedia.org/wiki/" + strLine);
                                    break;
                                case "YMDB Database Links":
                                    molecule.addPropertyByName(pName, "http://www.ymdb.ca/compounds/" + strLine);
                                    break;
                                default:
                                    molecule.addPropertyByName(pName, strLine);
                            }
                        } else {
                            molecule.addPropertyByName(pName, strLine);
                        }
                    }
                }
            }
            br.close();
            fstream.close();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error while parsing file: " + e.toString());
            System.exit(2);
        }

        /* Do something AFTER file reading */
        switch (format) {
            case turtle:
                jenaModel.write(System.out, "TURTLE");
                break;
            case ntriples:
                jenaModel.write(System.out, "NTRIPLES");
                break;
            case jsonld:
                output_str.setLength(output_str.length() - 2);
                output_str.append("\n  ],\n" +
                        "  \"@context\" : {\n" +
                        "    \"identifier\" : {\n" +
                        "      \"@id\" : \"http://schema.org/identifier\"\n" +
                        "    },\n" +
                        "    \"name\" : {\n" +
                        "      \"@id\" : \"http://schema.org/name\"\n" +
                        "    },\n" +
                        "    \"inChIKey\" : {\n" +
                        "      \"@id\" : \"http://schema.org/inChIKey\"\n" +
                        "    },\n" +
                        "    \"inChI\" : {\n" +
                        "      \"@id\" : \"http://schema.org/inChI\"\n" +
                        "    },\n" +
                        "    \"smiles\" : {\n" +
                        "      \"@id\" : \"http://schema.org/smiles\"\n" +
                        "    },\n" +
                        "    \"url\" : {\n" +
                        "      \"@id\" : \"http://schema.org/url\"\n" +
                        "    },\n" +
                        "    \"iupacName\" : {\n" +
                        "      \"@id\" : \"http://schema.org/iupacName\"\n" +
                        "    },\n" +
                        "    \"molecularFormula\" : {\n" +
                        "      \"@id\" : \"http://schema.org/molecularFormula\"\n" +
                        "    },\n" +
                        "    \"molecularWeight\" : {\n" +
                        "      \"@id\" : \"http://schema.org/molecularWeight\"\n" +
                        "    },\n" +
                        "    \"monoisotopicMolecularWeight\" : {\n" +
                        "      \"@id\" : \"http://schema.org/monoisotopicMolecularWeight\"\n" +
                        "    },\n" +
                        "    \"description\" : {\n" +
                        "      \"@id\" : \"http://schema.org/description\"\n" +
                        "    },\n" +
                        "    \"disambiguatingDescription\" : {\n" +
                        "      \"@id\" : \"http://schema.org/disambiguatingDescription\"\n" +
                        "    },\n" +
                        "    \"image\" : {\n" +
                        "      \"@id\" : \"http://schema.org/image\"\n" +
                        "    },\n" +
                        "    \"alternateName\" : {\n" +
                        "      \"@id\" : \"http://schema.org/alternateName\"\n" +
                        "    },\n" +
                        "    \"sameAs\" : {\n" +
                        "      \"@id\" : \"http://schema.org/sameAs\"\n" +
                        "    },\n" +
                        "    \"schema\" : \"http://schema.org/\"\n" +
                        "  }\n" +
                        "}");
                System.out.println(output_str);
                break;
            // JSON-LD with HTML
            case jsonldhtml:
                output_str.setLength(output_str.length() - 2);
                output_str.append("\n  ],\n" +
                        "  \"@context\" : {\n" +
                        "    \"identifier\" : {\n" +
                        "      \"@id\" : \"http://schema.org/identifier\"\n" +
                        "    },\n" +
                        "    \"name\" : {\n" +
                        "      \"@id\" : \"http://schema.org/name\"\n" +
                        "    },\n" +
                        "    \"inChIKey\" : {\n" +
                        "      \"@id\" : \"http://schema.org/inChIKey\"\n" +
                        "    },\n" +
                        "    \"inChI\" : {\n" +
                        "      \"@id\" : \"http://schema.org/inChI\"\n" +
                        "    },\n" +
                        "    \"smiles\" : {\n" +
                        "      \"@id\" : \"http://schema.org/smiles\"\n" +
                        "    },\n" +
                        "    \"url\" : {\n" +
                        "      \"@id\" : \"http://schema.org/url\"\n" +
                        "    },\n" +
                        "    \"iupacName\" : {\n" +
                        "      \"@id\" : \"http://schema.org/iupacName\"\n" +
                        "    },\n" +
                        "    \"molecularFormula\" : {\n" +
                        "      \"@id\" : \"http://schema.org/molecularFormula\"\n" +
                        "    },\n" +
                        "    \"molecularWeight\" : {\n" +
                        "      \"@id\" : \"http://schema.org/molecularWeight\"\n" +
                        "    },\n" +
                        "    \"monoisotopicMolecularWeight\" : {\n" +
                        "      \"@id\" : \"http://schema.org/monoisotopicMolecularWeight\"\n" +
                        "    },\n" +
                        "    \"description\" : {\n" +
                        "      \"@id\" : \"http://schema.org/description\"\n" +
                        "    },\n" +
                        "    \"disambiguatingDescription\" : {\n" +
                        "      \"@id\" : \"http://schema.org/disambiguatingDescription\"\n" +
                        "    },\n" +
                        "    \"image\" : {\n" +
                        "      \"@id\" : \"http://schema.org/image\"\n" +
                        "    },\n" +
                        "    \"alternateName\" : {\n" +
                        "      \"@id\" : \"http://schema.org/alternateName\"\n" +
                        "    },\n" +
                        "    \"sameAs\" : {\n" +
                        "      \"@id\" : \"http://schema.org/sameAs\"\n" +
                        "    },\n" +
                        "    \"schema\" : \"http://schema.org/\"\n" +
                        "  }\n" +
                        "}\n" +
                        "    </script>\n" +
                        "  </head>\n" +
                        "</html>");
                System.out.println(output_str);
                break;
            case rdfxml:
                jenaModel.write(System.out, "RDF/XML");
                break;
            case rdfthrift:
                jenaModel.write(System.out, "RDFTHRIFT");
                break;
            // RDFa and Microdata
            case rdfa:
            case microdata:
                System.out.println("  </body>");
                System.out.println("</html>");
                break;
            default:
                break;
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
