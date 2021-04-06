package pl.edu.uwb.ii.sdfeater;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.uwb.ii.sdfeater.SDFEater.initializeJenaModel;
import static pl.edu.uwb.ii.sdfeater.SDFEater.loadPeriodicTableData;

/**
 * Output data tests
 *
 * @author Łukasz Szeremeta 2021
 */
class OutputTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final String[] testMoleculeDataFields = {"alternateName", "description", "identifier", "inChI", "inChIKey", "iupacName", "molecularFormula", "molecularWeight", "smiles"};
    private final String[] testMoleculeData = {"(+)-2-fenchanone", "A fenchone that has", "4695-62-9", "InChI", "1S/C10H16O/c1-9(2)7-4-5-10(3,6-7)8(9)11/h7H,4-6H2,1-3H3/t7-,10+/m1/s1", "LHXDLQBQYFFVNW-XCBNKYQSSA-N", "(1S,4R)-fenchan-2-one", "C10H16O", "152.23340", "CC1(C)[C@@H]2CC[C@@](C)(C2)C1", "(-)-Epicatechin", "A catechin with", "490-46-0", "1S/C15H14O6/c16-8-4-11(18)9-6-13(20)15(21-14(9)5-8)7-1-2-10(17)12(19)3-7/h1-5,13,15-20H,6H2/t13-,15-/m1/s1", "PFTAWBLQPZVEMU-UKRRQHHQSA-N", "(2R,3R)-2-(3,4-dihydroxyphenyl)-3,4-dihydro-2H-chromene-3,5,7-triol", "C15H14O6", "290.26810", "[H][C@@]1(Oc2cc(O)cc(O)c2C[C@H]1O)c1ccc(O)c(O)c1"};
    private final File file = new File(Paths.get("src", "test", "resources", "chebi_test.sdf").toFile().getAbsolutePath());
    private Molecule molecule;

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
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        molecule = new Molecule();
    }


    // CYPHER (raw)

    /**
     * Test if Cypher raw output contains required strings
     */
    @Test
    void cypherRawContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.cypher, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"CREATE (", "{", "}", ",", ")", ":", "[", "]", "'", ":", "->", ")-[:", "]->(", "symbol:", "C", "x:", "y:", "z:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Cypher raw output contains all molecule data
     */
    @Test
    void cypherRawContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.cypher, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // CYPHER (with URLs)

    /**
     * Test if Cypher output with URLs contains required strings
     */
    @Test
    void cypherURLContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.cypheru, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"CREATE (", "{", "}", ",", ")", ":", "'", ":", ")-[:", "]->(", "symbol:", "C", "x:", "y:", "z:", "http", "//", "/"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Cypher output with URLs contains all molecule data
     */
    @Test
    void cypherURLContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.cypheru, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // CYPHER (with periodic table data)

    /**
     * Test if Cypher output periodic table contains required strings
     */
    @Test
    void cypherPeriodicContainsRequiredStrings() {
        loadPeriodicTableData();
        file.parse(molecule, SDFEater.Format.cypherp, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"CREATE (", "{", "}", ",", ")", ":", "'", ":", ")-[:", "]->(", "symbol:", "C", "x:", "y:", "z:", "atomicNumber:", "name:", "Carbon", "atomicMass:", "bondingType:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Cypher output periodic table contains all molecule data
     */
    @Test
    void cypherPeriodicContainsAllMoleculeData() {
        loadPeriodicTableData();
        file.parse(molecule, SDFEater.Format.cypherp, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // CYPHER (with URLs and periodic table data)

    /**
     * Test if Cypher output with URLs and periodic table contains required strings
     */
    @Test
    void cypherAllContainsRequiredStrings() {
        loadPeriodicTableData();
        file.parse(molecule, SDFEater.Format.cypherup, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"CREATE (", "{", "}", ",", ")", ":", "'", ":", ")-[:", "]->(", "symbol:", "C", "x:", "y:", "z:", "http", "//", "/", "atomicNumber:", "name:", "Carbon", "atomicMass:", "bondingType:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Cypher output with URLs and periodic table contains all molecule data
     */
    @Test
    void cypherAllContainsAllMoleculeData() {
        loadPeriodicTableData();
        file.parse(molecule, SDFEater.Format.cypherup, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // CVME

    /**
     * Test if CVME output contains required strings
     */
    @Test
    void cvmeContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.cvme, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"skos:altLabel", "skos:definition", "skos:notation", "skos:prefLabel", "rdfs:seeAlso", "dbo:inchi", "dbp:inchikey", "dbo:casNumber", "skos:hiddenLabel", "skos:example", "urn:uuid:", "^^", "@", "chemskos:SMILES", ".", "<", ">", "http", ":", "//", "\"\"\"", "END", "C"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if CVME output contains all molecule data
     */
    @Test
    void cvmeContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.cvme, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // SMILES

    /**
     * Test if SMILES output contains SMILES
     */
    @Test
    void smilesContainsSMILES() {
        file.parse(molecule, SDFEater.Format.smiles, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"[H][C@@]1(Oc2cc(O)cc(O)c2C[C@H]1O)c1ccc(O)c(O)c1", "CC1(C)[C@@H]2CC[C@@](C)(C2)C1=O"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // InChI

    /**
     * Test if InChI output contains InChI
     */
    @Test
    void inchiContainsInChI() {
        file.parse(molecule, SDFEater.Format.inchi, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"InChI=1S/C15H14O6/c16-8-4-11(18)9-6-13(20)15(21-14(9)5-8)7-1-2-10(17)12(19)3-7/h1-5,13,15-20H,6H2/t13-,15-/m1/s1", "InChI=1S/C10H16O/c1-9(2)7-4-5-10(3,6-7)8(9)11/h7H,4-6H2,1-3H3/t7-,10+/m1/s1"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // Turtle

    /**
     * Test if Turtle output contains required strings
     */
    @Test
    void turtleContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"@prefix", "schema.org", "rdf", "MolecularEntity", "[", ";", "]", ".", "<", ">", "http", "//", ":", "\""};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Turtle output contains all molecule data fields
     */
    @Test
    void turtleContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if Turtle output contains all molecule data
     */
    @Test
    void turtleContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if Turtle output have IRI subject
     */
    @Test
    void turtleHaveIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Turtle output have uuid subject
     */
    @Test
    void turtleHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Turtle output have bNode subject
     */
    @Test
    void turtleHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.turtle, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"[", "]"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // N-Triples

    /**
     * Test if N-Triples output contains required strings
     */
    @Test
    void nTriplesContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"schema.org", "rdf", "MolecularEntity", ".", "<", ">", "http", "//", ":", "\""};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if N-Triples output contains all molecule data fields
     */
    @Test
    void nTriplesContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if N-Triples output contains all molecule data
     */
    @Test
    void nTriplesContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if N-Triples output have IRI subject
     */
    @Test
    void nTriplesHaveIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if N-Triples output have uuid subject
     */
    @Test
    void nTriplesHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if N-Triples output have bNode subject
     */
    @Test
    void nTriplesHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.ntriples, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"_:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // RDF/XML

    /**
     * Test if RDF/XML output contains required strings
     */
    @Test
    void rdfXMLContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "rdf:RDF", "xmlns:", "<rdf:Description", "type", "MolecularEntity", "schema", "</", "&lt;", "&gt;"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDF/XML output contains all molecule data fields
     */
    @Test
    void rdfXMLContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if RDF/XML output contains all molecule data
     */
    @Test
    void rdfXMLContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if RDF/XML output have IRI subject
     */
    @Test
    void rdfXMLHaveIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDF/XML output have uuid subject
     */
    @Test
    void rdfXMLHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDF/XML output have bNode subject
     */
    @Test
    void rdfXMLHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfxml, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"rdf:about"};
        assertFalse(stringContainsAllValues(out, required));
    }

    // RDF Binary encoding using Thrift (rdfthrift)

    /**
     * Test if rdfthrift output contains required strings
     */
    @Test
    void rdfthriftContainsRequiredStrings() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"schema", "rdf"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if rdfthrift output contains all molecule data fields
     */
    @Test
    void rdfthriftContainsAllMoleculeDataFields() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if rdfthrift output contains all molecule data
     */
    @Test
    void rdfthriftContainsAllMoleculeData() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if rdfthrift output have IRI subject
     */
    @Test
    void rdfthriftHaveIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if rdfthrift output have uuid subject
     */
    @Test
    void rdfthriftHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if rdfthrift output have bNode subject
     */
    @Test
    void rdfthriftHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfthrift, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity", "urn:uuid:"};
        assertFalse(stringContainsAllValues(out, required));
    }

    // JSON-LD + HTML

    /**
     * Test if JSON-LD+HTML output contains required strings
     */
    @Test
    void jsonldHtmlContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.jsonldhtml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "script>", "@id", "{", "}", ",", "MolecularEntity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD+HTML output contains all molecule data fields
     */
    @Test
    void jsonldHtmlContainsAllMoleculeDataFields() {
        file.parse(molecule, SDFEater.Format.jsonldhtml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if JSON-LD+HTML output contains all molecule data
     */
    @Test
    void jsonldHtmlContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.jsonldhtml, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    // JSON-LD

    /**
     * Test if JSON-LD output contains required strings
     */
    @Test
    void jsonldContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"@id", "{", "}", ",", "MolecularEntity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD output contains all molecule data fields
     */
    @Test
    void jsonldContainsAllMoleculeDataFields() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if JSON-LD output contains all molecule data
     */
    @Test
    void jsonldContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if JSON-LD output have IRI subject
     */
    @Test
    void jsonldHaveIRISubject() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD output have uuid subject
     */
    @Test
    void jsonldHaveUUIDSubject() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if JSON-LD output have bNode subject
     */
    @Test
    void jsonldHaveBNodeSubject() {
        file.parse(molecule, SDFEater.Format.jsonld, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"_:b"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // RDFa

    /**
     * Test if RDFa output contains required strings
     */
    @Test
    void rdfaContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "typeof", "property", "MolecularEntity", "&lt;", "&gt;"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDFa output contains all molecule data fields
     */
    @Test
    void rdfaContainsAllMoleculeDataFields() {
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if RDFa output contains all molecule data
     */
    @Test
    void rdfaContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if RDFa output have IRI subject
     */
    @Test
    void rdfaHaveIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDFa output have uuid subject
     */
    @Test
    void rdfaHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if RDFa output have bNode subject
     */
    @Test
    void rdfaHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.rdfa, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"_:b"};
        assertTrue(stringContainsAllValues(out, required));
    }

    // Microdata

    /**
     * Test if Microdata output contains required strings
     */
    @Test
    void microdataContainsRequiredStrings() {
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"<", ">", "</", "itemscope", "itemtype", "itemprop", "MolecularEntity", "&lt;", "&gt;"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Microdata output contains all molecule data fields
     */
    @Test
    void microdataContainsAllMoleculeDataFields() {
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeDataFields));
    }

    /**
     * Test if Microdata output contains all molecule data
     */
    @Test
    void microdataContainsAllMoleculeData() {
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        assertTrue(stringContainsAllValues(out, testMoleculeData));
    }

    /**
     * Test if Microdata output have IRI subject
     */
    @Test
    void microdataIRISubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.iri);
        String out = outputStreamCaptor.toString();
        String[] required = {"http", "example.com", "molecule", "#", "entity"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Microdata output have uuid subject
     */
    @Test
    void microdataHaveUUIDSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.uuid);
        String out = outputStreamCaptor.toString();
        String[] required = {"urn:uuid:"};
        assertTrue(stringContainsAllValues(out, required));
    }

    /**
     * Test if Microdata output have bNode subject
     */
    @Test
    void microdataHaveBNodeSubject() {
        initializeJenaModel();
        file.parse(molecule, SDFEater.Format.microdata, SDFEater.Subject.bnode);
        String out = outputStreamCaptor.toString();
        String[] required = {"_:b"};
        assertTrue(stringContainsAllValues(out, required));
    }

}