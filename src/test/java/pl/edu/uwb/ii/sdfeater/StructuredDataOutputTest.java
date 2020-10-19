package pl.edu.uwb.ii.sdfeater;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.uwb.ii.sdfeater.SDFEater.initializeJenaModel;

/**
 * Structured data output tests
 * (JSON-LD, JSON-LD + HTML, RDFa, Microdata)
 */
class StructuredDataOutputTest {
    //private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final String[] testMoleculeData = {"(+)-2-fenchanone", "A fenchone that has", "4695-62-9", "InChI=1S/C10H16O/c1-9(2)7-4-5-10(3,6-7)8(9)11/h7H,4-6H2,1-3H3/t7-,10+/m1/s1", "LHXDLQBQYFFVNW-XCBNKYQSSA-N", "(1S,4R)-fenchan-2-one", "C10H16O", "152.23340", "CC1(C)[C@@H]2CC[C@@](C)(C2)C1=O", "(-)-Epicatechin", "A catechin with", "490-46-0", "InChI=1S/C15H14O6/c16-8-4-11(18)9-6-13(20)15(21-14(9)5-8)7-1-2-10(17)12(19)3-7/h1-5,13,15-20H,6H2/t13-,15-/m1/s1", "PFTAWBLQPZVEMU-UKRRQHHQSA-N", "(2R,3R)-2-(3,4-dihydroxyphenyl)-3,4-dihydro-2H-chromene-3,5,7-triol", "C15H14O6", "290.26810", "[H][C@@]1(Oc2cc(O)cc(O)c2C[C@H]1O)c1ccc(O)c(O)c1"};
    private Molecule molecule;
    private File file;

    /**
     * Check if String contains all values from String array
     *
     * @param str    String to check
     * @param values String array with required values
     * @return true if String contains all values, false otherwise
     */
    private static boolean stringContainsAllValues(String str, String[] values) {
        boolean found = true;
        for (String v : values) {
            if (!str.contains(v)) {
                found = false;
                break;
            }
        }
        return found;
    }

    @BeforeEach
    void redirectSystemOutStream() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @BeforeEach
    void initializeObjects() {
        molecule = new Molecule();
        file = new File(Paths.get("src", "test", "resources", "chebi_test.sdf").toFile().getAbsolutePath());
    }

//    @AfterEach
//    void restoreSystemOutStream() {
//        System.setOut(standardOut);
//    }

    /**
     * Test if JSON-LD output contains required strings
     */
    @Test
    void jsonldContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, 'j', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"@id", "{", "}", ",", "MolecularEntity"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD+HTML output contains required strings
     */
    @Test
    void jsonldHtmlContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, 'd', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "script>", "@id", "{", "}", ",", "MolecularEntity"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDFa output contains required strings
     */
    @Test
    void rdfaContainsRequiredStrings() {
        file.parse(molecule, 'a', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "typeof", "property", "MolecularEntity", "&lt;", "&gt;"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Microdata output contains required strings
     */
    @Test
    void microdataContainsRequiredStrings() {
        file.parse(molecule, 'm', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "itemscope", "itemtype", "itemprop", "MolecularEntity", "&lt;", "&gt;"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD output contains all molecule data fields
     */
    @Test
    void jsonldContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, 'j', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"alternateName", "description", "identifier", "inChI", "inChIKey", "iupacName", "molecularFormula", "molecularWeight", "smiles"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD+HTML output contains all molecule data fields
     */
    @Test
    void jsonldHtmlContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, 'd', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"alternateName", "description", "identifier", "inChI", "inChIKey", "iupacName", "molecularFormula", "molecularWeight", "smiles"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDFa output contains all molecule data fields
     */
    @Test
    void rdfaContainsAllMoleculeDataFields() {
        file.parse(molecule, 'a', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"alternateName", "description", "identifier", "inChI", "inChIKey", "iupacName", "molecularFormula", "molecularWeight", "smiles"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Microdata output contains all molecule data fields
     */
    @Test
    void microdataContainsAllMoleculeDataFields() {
        file.parse(molecule, 'm', false, false);

        String out = outputStreamCaptor.toString();
        String[] required = {"alternateName", "description", "identifier", "inChI", "inChIKey", "iupacName", "molecularFormula", "molecularWeight", "smiles"};

        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD output contains all molecule data
     */
    @Test
    void jsonldContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, 'j', false, false);

        String out = outputStreamCaptor.toString();

        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if JSON-LD+HTML output contains all molecule data
     */
    @Test
    void jsonldHtmlContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, 'd', false, false);

        String out = outputStreamCaptor.toString();

        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if RDFa output contains all molecule data
     */
    @Test
    void rdfaContainsAllMoleculeData() {
        file.parse(molecule, 'a', false, false);

        String out = outputStreamCaptor.toString();

        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if Microdata output contains all molecule data
     */
    @Test
    void microdataContainsAllMoleculeData() {
        file.parse(molecule, 'm', false, false);

        String out = outputStreamCaptor.toString();

        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }
}