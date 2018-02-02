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

import java.util.Arrays;

/**
 * Main parser class
 *
 * @author Łukasz Szeremeta 2017-2018
 * @author Dominik Tomaszuk 2017-2018
 */
public class SDFParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Compound c = new Compound();
        if (args.length > 0) {
            File file = new File(args[0]);
            if (Arrays.asList(args).contains("-e")) {
              if (Arrays.asList(args).contains("-c")) {
                  file.parse(c,'c',true);
              } else if (Arrays.asList(args).contains("-r")) {
                  file.parse(c,'r',true);
              } else {
                  file.parse(c,'c',true);
              }
            } else {
              if (Arrays.asList(args).contains("-c")) {
                  file.parse(c,'c',false);
              } else if (Arrays.asList(args).contains("-r")) {
                  file.parse(c,'r',false);
              } else {
                  file.parse(c,'c',false);
              }
            }
            
        } else {
            System.out.println("USAGE: java -jar \"SDFParser.jar\" FILE OPTIONS");
            System.out.println("Options:\n -c Cypher format (default)\n -r CVME Turtle format (SKOS and RDF based)\n -e try to enrich links");
        }

        //File file = new File("examples/chebi_test.sdf");
        //file.parse(c, 'c');
    }

}
