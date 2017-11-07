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
package chebisdfparser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
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

            // TODO change later for only one compound (each compound ends with $$$$ in new line)
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();

                if (!molfileReady) {
                    // TODO molfile
                    System.out.println(strLine);
                }

                if (strLine.startsWith("M ")) {
                    molfileReady = true;
                } else if (molfileReady) {
                    // SDF file parse
                    if (strLine.startsWith("> <")) {
                        // > <ChEBI Name> więc można obcinać 3 znaki z przodu i jeden z tyłu (prawodpodobnie wydajniej),
                        // ale może być jakiś podwójny odstęp...
                        pName = strLine.substring(3, strLine.length() - 1);
                        // alternatywne rozwiązanie:
                        //pName = strLine.split("<")[1];
                        //pName = propertyName.substring(0, propertyName.length() - 1);
                    } else if (strLine.startsWith("$$$$")) {
                        // TODO
                    } else if (strLine.isEmpty()) {
                    } else if (!strLine.isEmpty()) {
                        c.addPropertyByName(pName, strLine);
                    }
                }

            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error while parsing file: " + e.getMessage());
        }
    }

}
