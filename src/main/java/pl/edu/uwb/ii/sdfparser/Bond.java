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

/**
 * Bonds data
 *
 * @author Łukasz Szeremeta 2017
 */
public class Bond {

    int atom1;
    byte type;
    int atom2;
    byte stereo;

    /**
     * Bond class constructor
     *
     * @param atom1 first atom id
     * @param type bond type
     * @param atom2 second atom id
     * @param stereo bond stereo
     *
     */
    public Bond(int atom1, byte type, int atom2, byte stereo) {
        this.atom1 = atom1;
        this.type = type;
        this.atom2 = atom2;
        this.stereo = stereo;
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(atom1) + ")--" + String.valueOf(type) + "--(" + String.valueOf(atom2) + ")";
    }
}
