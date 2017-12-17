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
 * @author Łukasz Szeremeta 2017
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

                    // V2000, V3000
                    if ((tokens.length == 7 || tokens.length == 6) && !tokens[6].startsWith("V")) {
                        c.bonds.add(new Bond(Integer.parseInt(tokens[0]), Byte.parseByte(tokens[2]), Integer.parseInt(tokens[1]), Byte.parseByte(tokens[3])));
                    }
                } else if (molfileReady && !strLine.matches("M\\s+\\w+.*")) {
                    // SDF file parse
                    if (strLine.replaceAll("\\s+", "").startsWith("><")) {
                        pName = strLine.split("<")[1];
                        pName = pName.substring(0, pName.length() - 1);
                    } else if (strLine.startsWith("$$$$")) {
                        c.printProperties();
                        c.printAtoms();
                        c.printBonds();
                        c.clearAll();
                        molfileReady = false;
                    } else if (strLine.isEmpty()) {
                    } else if (!strLine.isEmpty()) {
                        c.addPropertyByName(pName, strLine);
                    }
                }

            }
            in.close();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error while parsing file: " + e.toString());
        }
    }

}
