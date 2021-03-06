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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Main parser class
 *
 * @author Łukasz Szeremeta 2017-2021
 * @author Dominik Tomaszuk 2017-2018
 */
class SDFEater {

    /**
     * Stores all Atoms data from periodic table
     */
    static Map<String, Map<String, Object>> periodic_table_data;
    /**
     * Apache Jena Model for some formats
     */
    static Model jenaModel;

    /**
     * Loads periodic data from JSON file to the Map
     */
    protected static void loadPeriodicTableData() {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        InputStream periodic_resource = SDFEater.class.getResourceAsStream("periodic_table.json");
        Reader periodic_reader = new InputStreamReader(periodic_resource);
        periodic_table_data = gson.fromJson(periodic_reader, type);
    }

    /**
     * Initialize Apache Jena Model for some formats
     */
    static void initializeJenaModel() {
        jenaModel = ModelFactory.createDefaultModel();
        jenaModel.setNsPrefix("schema", "https://schema.org/");
        jenaModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Molecule molecule = new Molecule();
        Options options = new Options();
        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        Option formatarg = new Option("f", "format", true, "output format (cypher, cypheru, cypherp, cypherup, cvme, smiles, inchi, turtle, ntriples, rdfxml, rdfthrift, jsonldhtml, jsonld, rdfa, microdata)");
        formatarg.setRequired(true);
        options.addOption(formatarg);
        Option subject = new Option("s", "subject", true, "subject type (iri, uuid, bnode; iri by default); for all formats excluding cypher, cvme, smiles, inchi");
        subject.setRequired(false);
        options.addOption(subject);
        Option base = new Option("b", "base", true, "molecule subject base for 'iri' subject type ('" + molecule.subjectBase + "' by default)");
        base.setRequired(false);
        options.addOption(base);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            String fileparam = cmd.getOptionValue("input");
            File file = new File(fileparam);

            // load and set additional modules for formats
            switch (cmd.getOptionValue("format")) {
                case "cypherp":
                case "cypherup":
                    loadPeriodicTableData();
                    break;
                case "turtle":
                case "ntriples":
                case "rdfxml":
                case "rdfthrift":
                    initializeJenaModel();
                    break;
            }

            // replace default base molecule IRI
            if (cmd.getOptionValue("subject", Subject.iri.toString()).equals(Subject.iri.toString())) {
                molecule.subjectBase = cmd.getOptionValue("base", molecule.subjectBase);
            }

            file.parse(molecule, Format.valueOf(cmd.getOptionValue("format")), Subject.valueOf(cmd.getOptionValue("subject", Subject.iri.toString())));
        } catch (IllegalArgumentException e) {
            System.err.println("Incorrect option selected");
            formatter.printHelp("SDFEater.jar", options);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            formatter.printHelp("SDFEater.jar", options);
        }
    }

    /**
     * Supported formats
     */
    public enum Format {
        cypher,
        cypheru,
        cypherp,
        cypherup,
        cvme,
        smiles,
        inchi,
        turtle,
        ntriples,
        rdfxml,
        rdfthrift,
        jsonldhtml,
        jsonld,
        rdfa,
        microdata
    }

    /**
     * Subject type
     */
    public enum Subject {
        iri,
        uuid,
        bnode
    }
}
