package org.reactome.server.tools.exporters.sbmlexport;

import com.martiansoftware.jsap.JSAPException;
import org.junit.BeforeClass;
import org.reactome.server.graph.domain.model.Pathway;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.sbml.jsbml.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sarah Keating <skeating@ebi.ac.uk>
 */
public class WriteSBMLOtherEntityTest {
    private static WriteSBML testWrite;

    private final String empty_doc = String.format("<?xml version='1.0' encoding='utf-8' standalone='no'?>%n" +
            "<sbml xmlns=\"http://www.sbml.org/sbml/level3/version1/core\" level=\"3\" version=\"1\"></sbml>%n");


    private final String notes = String.format("<notes>%n" +
            "  <p xmlns=\"http://www.w3.org/1999/xhtml\">Derived from a " +
            "Reactome OtherEntity.</p>%n" + "</notes>");


    private final String openset_notes = String.format("<notes>%n" +
            "  <p xmlns=\"http://www.w3.org/1999/xhtml\">Derived from a Reactome OpenSet.%n" +
            "A set of examples characterizing a very large but not explicitly enumerated set, e.g. mRNAs.</p>%n"
            + "</notes>");

    @BeforeClass
    public static void setup()  throws JSAPException {
        DatabaseObjectService databaseObjectService = ReactomeGraphCore.getService(DatabaseObjectService.class);
        long dbid = 1799339L; // pathway with a various entity types
        Pathway pathway = (Pathway) databaseObjectService.findById(dbid);

        testWrite = new WriteSBML(pathway);
        testWrite.setAnnotationFlag(true);
    }

    /**
     * test the document is created
     */
    @org.junit.Test
    public void testConstructor()
    {
        assertTrue( "WriteSBML constructor failed", testWrite != null );
    }

    @org.junit.Test
    public void testDocument()
    {
        SBMLDocument doc = testWrite.getSBMLDocument();
        assertTrue( "Document creation failed", doc != null);
        assertTrue( "Document level failed", doc.getLevel() == 3);
        assertTrue( "Document version failed", doc.getVersion() == 1);
        // depending on how junit orders the test we might already have the model here
        if (!doc.isSetModel()) {
            assertEquals(empty_doc, testWrite.toString());
        }
    }

    @org.junit.Test
    public void testCreateModel()
    {
        SBMLDocument doc = testWrite.getSBMLDocument();
        if (!doc.isSetModel()) {
            testWrite.createModel();
            doc = testWrite.getSBMLDocument();
        }
        assertTrue( "Document creation failed", doc != null);

        Model model = doc.getModel();
        assertTrue("Model failed", model != null);

        assertEquals("Num compartments failed", model.getNumCompartments(), 2);
        assertEquals("Num species failed", model.getNumSpecies(), 12);

        // species from OtherEntity
        Species species = model.getSpecies("species_1799320");
        assertTrue("species_1799320", species != null);
        assertEquals("num cvterms on species", species.getNumCVTerms(), 1);

        CVTerm cvTerm = species.getCVTerm(0);
        assertEquals("num resources on species cvterm", cvTerm.getNumResources(), 1);
        assertEquals("qualifier on species incorrect", cvTerm.getBiologicalQualifierType(), CVTerm.Qualifier.BQB_IS);

        try {
            String output = species.getNotesString().replace("\n", System.getProperty("line.separator"));
            assertEquals("species notes", notes, output);
        }
        catch(Exception e){
            System.out.println("getNotesString failed");
        }

        // species from OpenSet
        species = model.getSpecies("species_72323");
        assertTrue("species_72323", species!= null);
        assertEquals("num cvterms on species species_72323", species.getNumCVTerms(), 2);

        cvTerm = species.getCVTerm(0);
        assertEquals("num resources on species cvterm", cvTerm.getNumResources(), 1);
        assertEquals("qualifier on species incorrect", cvTerm.getBiologicalQualifierType(), CVTerm.Qualifier.BQB_IS);

        cvTerm = species.getCVTerm(1);
        assertEquals("num resources on species cvterm", cvTerm.getNumResources(), 1);
        assertEquals("qualifier on species incorrect", cvTerm.getBiologicalQualifierType(), CVTerm.Qualifier.BQB_HAS_PART);

        try {
            String output = species.getNotesString().replace("\n", System.getProperty("line.separator"));
            assertEquals("species notes", openset_notes, output);
        }
        catch(Exception e){
            System.out.println("getNotesString failed");
        }
    }

    @org.junit.Test
    public void testSpeciesSBOTerms()
    {
        SBMLDocument doc = testWrite.getSBMLDocument();
        if (!doc.isSetModel()) {
            testWrite.createModel();
            doc = testWrite.getSBMLDocument();
        }

        Model model = doc.getModel();
        assertTrue("Model failed", model != null);

        // species from OtherEntity
        Species species = model.getSpecies("species_1799320");
        assertTrue("sbo term set", species.isSetSBOTerm());
        assertEquals("other entity sbo term", species.getSBOTerm(), 240);

        // species from OpenSet
        species = model.getSpecies("species_72323");
        assertTrue("sbo term set", !species.isSetSBOTerm());

        // species from Complex
        species = model.getSpecies("species_264960");
        assertTrue("sbo term set", species.isSetSBOTerm());
        assertEquals("complex sbo term", species.getSBOTerm(), 253);
    }
}
