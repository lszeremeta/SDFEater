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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main parser class
 *
 * @author Łukasz Szeremeta 2017-2018
 * @author Dominik Tomaszuk 2017-2018
 */
public class SDFParser {

    /**
     * Stores all Atoms data from periodic table
     */
    static Map<String, Map<String, Object>> periodic_table_data;

    /**
     * Loads periodic data from JSON file to the Map
     */
    static void loadPeriodicTableData() {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        InputStream periodic_resource = SDFParser.class.getResourceAsStream("periodic_table.json");
        Reader periodic_reader = new InputStreamReader(periodic_resource);
        periodic_table_data = gson.fromJson(periodic_reader, type);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Compound c = new Compound();
        Options options = new Options();
        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        Option formatarg = new Option("f", "format", true, "output format (cypher, cvme, smiles, inchi)");
        formatarg.setRequired(true);
        options.addOption(formatarg);
        Option urls = new Option("u", "urls", false, "try to generate full database URLs instead of IDs (enabled in cvme)");
        urls.setRequired(false);
        options.addOption(urls);
        Option periodic_data = new Option("p", "periodic", false, "add additional atoms data from periodic table (for cypher format)");
        periodic_data.setRequired(false);
        options.addOption(periodic_data);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            String fileparam = cmd.getOptionValue("input");
            File file = new File(fileparam);
            if (cmd.hasOption("format")) {
                String format = cmd.getOptionValue("format");
                if (format.equalsIgnoreCase("cypher")) {
                    if (cmd.hasOption("urls") && cmd.hasOption("periodic")) {
                        loadPeriodicTableData();
                        file.parse(c, 'c', true, true);
                    } else if (!cmd.hasOption("urls") && cmd.hasOption("periodic")) {
                        loadPeriodicTableData();
                        file.parse(c, 'c', false, true);
                    } else if (cmd.hasOption("urls") && !cmd.hasOption("periodic")) {
                        file.parse(c, 'c', true, false);
                    } else if (!cmd.hasOption("urls") && !cmd.hasOption("periodic")) {
                        file.parse(c, 'c', false, false);
                    }
                } else if (format.equalsIgnoreCase("cvme")) {
                    file.parse(c, 'r', true, false);
                } else if (format.equalsIgnoreCase("smiles")) {
                    file.parse(c, 's', false, false);
                } else if (format.equalsIgnoreCase("inchi")) {
                    file.parse(c, 'n', false, false);
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SDFParser.jar", options);
        }
    }
}
