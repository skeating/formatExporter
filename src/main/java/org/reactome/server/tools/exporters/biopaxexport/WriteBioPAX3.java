package org.reactome.server.tools.exporters.biopaxexport;

import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.domain.model.Event;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.biopax.paxtools.model.*;

/**
 * @author Sarah Keating <skeating@ebi.ac.uk>
 */
public class WriteBioPAX3 {

    /**
     * sbml information variables
     * these can be changed if we decide to target a different sbml level and version
     */
    private final short sbmlLevel = 3;
    private final short sbmlVersion = 1;
    private static long metaid_count = 0;

    private final Pathway thisPathway;
    private final List<Event> thisListEvents;
    private Pathway parentPathway;

    private final List <String> loggedSpecies;
    private final List <String> loggedCompartments;
    private final List <String> loggedReactions;
    private final List <String> loggedSpeciesReferences;

    private static Integer dbVersion = 0;

    private boolean addAnnotations = true;
    private boolean inTestMode = false;

    private boolean useEventOf = true;

    private BioPAXFactory bioPAXFactory = BioPAXLevel.L3.getDefaultFactory();
    private Model thisModel;

    /**
     * Construct an instance of the WriteBioPAX3
     */
    public WriteBioPAX3(){
        thisPathway = null;
        thisListEvents = null;
        parentPathway = null;
//        sbmlDocument = new SBMLDocument(sbmlLevel, sbmlVersion);
        loggedSpecies = new ArrayList<String>();
        loggedCompartments = new ArrayList<String>();
        loggedReactions = new ArrayList<String>();
        loggedSpeciesReferences = new ArrayList<String>();
        // reset metaid count
        metaid_count= 0;


    }

    /**
     * Construct an instance of the WriteBioPAX3 for the specified
     * Pathway.
     *
     * @param pathway  Pathway from ReactomeDB
     */
    public WriteBioPAX3(Pathway pathway){
        thisPathway = pathway;
        thisListEvents = null;
        parentPathway = null;
//        sbmlDocument = new SBMLDocument(sbmlLevel, sbmlVersion);
        loggedSpecies = new ArrayList<String>();
        loggedCompartments = new ArrayList<String>();
        loggedReactions = new ArrayList<String>();
        loggedSpeciesReferences = new ArrayList<String>();
        // reset metaid count
        metaid_count= 0;
    }

    /**
     * Construct an instance of the WriteBioPAX3 for the specified
     * Pathway.
     *
     * @param pathway Pathway from ReactomeDB
     * @param version Integer - version number of the database
     */
    public WriteBioPAX3(Pathway pathway, Integer version){
        thisPathway = pathway;
        thisListEvents = null;
        parentPathway = null;
        dbVersion = version;
//        sbmlDocument = new SBMLDocument(sbmlLevel, sbmlVersion);
        loggedSpecies = new ArrayList<String>();
        loggedCompartments = new ArrayList<String>();
        loggedReactions = new ArrayList<String>();
        loggedSpeciesReferences = new ArrayList<String>();
        // reset metaid count
        metaid_count= 0;
    }

//    /**
//     * Construct an instance of the SBMLWriter for the specified
//     * List of Events.
//     *
//     * @param loe list<Event> from ReactomeDB
//     */
//    public WriteSBML(List<Event> loe){
//        thisPathway = null;
//        thisListEvents = loe;
//        determineParentPathway();
//        sbmlDocument = new SBMLDocument(sbmlLevel, sbmlVersion);
//        loggedSpecies = new ArrayList<String>();
//        loggedCompartments = new ArrayList<String>();
//        loggedReactions = new ArrayList<String>();
//        loggedSpeciesReferences = new ArrayList<String>();
//        sbo = new SBOTermLookup();
//        // reset metaid count
//        metaid_count= 0;
//    }
//
//    /**
//     * Construct an instance of the SBMLWriter for the specified
//     * List of Events.
//     *
//     * @param loe list<Event> from ReactomeDB
//     * @param version Integer - version number of the database
//     */
//    public WriteSBML(List<Event> loe, Integer version){
//        thisPathway = null;
//        thisListEvents = loe;
//        determineParentPathway();
//        dbVersion = version;
//        sbmlDocument = new SBMLDocument(sbmlLevel, sbmlVersion);
//        loggedSpecies = new ArrayList<String>();
//        loggedCompartments = new ArrayList<String>();
//        loggedReactions = new ArrayList<String>();
//        loggedSpeciesReferences = new ArrayList<String>();
//        sbo = new SBOTermLookup();
//        // reset metaid count
//        metaid_count= 0;
//    }

    /**
     * Create the SBML model using the Reactome Pathway specified in the constructor.
     */
    public void createModel(){
//        if (inTestMode && thisListEvents != null){
//            parentPathway = null;
//        }
//        boolean createModel = false;
//        Long pathNum;
//        String pathName = "No parent pathway detected";
//        String modelId = "no_parent_pathway";
//        if (thisPathway != null) {
//            pathNum = thisPathway.getDbId();
//            pathName = thisPathway.getDisplayName();
//            modelId = "pathway_" + pathNum;
//            createModel = true;
//        }
//        else if (thisListEvents != null) {
//            if (parentPathway != null) {
//                pathNum = parentPathway.getDbId();
//                pathName = parentPathway.getDisplayName();
//                modelId = "pathway_" + pathNum;
//            }
//            createModel = true;
//        }

        thisModel = bioPAXFactory.createModel();
        thisModel.setXmlBase("http://");

//        if (createModel) {
//            Model model = sbmlDocument.createModel(modelId);
//            model.setName(pathName);
//            setMetaid(model);
//
            addAllReactions(thisPathway);
//
//            if (addAnnotations) {
//                addModelAnnotations(model);
//            }
//        }
    }

    /**
     * Set the database version number.
     *
     * @param version  Integer the ReactomeDB version number being used.
     */
    public void setDBVersion(Integer version) {
        dbVersion = version;
    }

    String getModelId() {
//        Model m = sbmlDocument.getModel();
//        if (m != null) {
//            return m.getId();
//        }
//        else {
//            return "";
//        }
        return "";
    }

    ///////////////////////////////////////////////////////////////////////////////////

    // functions to output resulting document

    /**
     * Write the Biopax Model to std output.
     */
    public void toStdOut()    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SimpleIOHandler out = new SimpleIOHandler();
        out.convertToOWL(thisModel, os);
        String output;
        try {
            output = new String(os.toByteArray(), "UTF-8");
        }
        catch (Exception e) {
            output = "failed to write";
        }
        System.out.print(output);
    }

    /**
     * Write the BioPAX Model to a file.
     *
     * @param output File to use.
     */
    public void toFile(File output)    {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SimpleIOHandler out = new SimpleIOHandler();
        out.convertToOWL(thisModel, os);
        try {
            if (!output.exists()) {
                output.createNewFile();
            }
            FileOutputStream fop = new FileOutputStream(output);
            fop.write(os.toByteArray());
            fop.flush();
            fop.close();
        }
        catch (Exception e)
        {
            System.out.println("failed to write " + output.getName());
        }
    }

    /**
     * Write the BioPAX Model to a String.
     *
     * @return  String representing the BioPAX Model.
     */
    public String toString()    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SimpleIOHandler out = new SimpleIOHandler();
        out.convertToOWL(thisModel, os);
        String output;
        try {
            output = new String(os.toByteArray(), "UTF-8");
        }
        catch (Exception e) {
            output = "failed to write";
        }
        return output;
    }

    //////////////////////////////////////////////////////////////////////////////////

    // functions to facilitate testing


    /**
     * Set the addAnnotation flag.
     * This allows testing with and without annotations
     *
     * @param flag  Boolean indicating whether to write out annotations
     */
    void setAnnotationFlag(Boolean flag){
        addAnnotations = flag;
    }

    /**
     * Set the inTestMode flag.
     * This allows testing with/without certain things
     *
     * @param flag  Boolean indicating whether tests are running
     */
    void setInTestModeFlag(Boolean flag){
        inTestMode = flag;
    }

    //////////////////////////////////////////////////////////////////////////////////

    // Private functions


    /**
     * Determines a unique parent pathway from the List<Event> argument
     * and sets the member variable parentPathway if one is found
     */
    private void determineParentPathway() {
        if (thisListEvents == null || thisListEvents.size() == 0) {
            parentPathway = null;
            return;
        }
        List<Long> listDBid = new ArrayList<Long>();
        List<Long> firstDBid = new ArrayList<Long>();

        Event e1 = thisListEvents.get(0);
        List<Event> child_list = e1.getEventOf();
        if (child_list == null) {
            parentPathway = null;
            return;
        }
        // list of possible parent pathways
        for (Event e : child_list) {
            if (e instanceof Pathway) {
                listDBid.add(e.getDbId());
                firstDBid.add(e.getDbId());
            }
        }

        // // TODO: 09/11/2016 must be a better way to do this
        int x = 0;
        while (x < thisListEvents.size()-1){
            x++;
            Event e_loop = thisListEvents.get(x);
            List<Event> loe_loop = e_loop.getEventOf();
            if (loe_loop == null) {
                parentPathway = null;
                return;
            }
            else {
                List<Long> thisDbId = new ArrayList<Long>();
                for (Event ee : loe_loop) {
                    if (ee instanceof Pathway) {
                        thisDbId.add(ee.getDbId());
                    }
                }
                for (Long p: firstDBid) {
                    if (!thisDbId.contains(p)) {
                        listDBid.remove(p);
                    }
                }
            }
        }

        if (listDBid.size() != 1) {
            parentPathway = null;
        }
        else {
            for (Event e : child_list) {
                if (e.getDbId().equals(listDBid.get(0))) {
                    parentPathway = (Pathway)(e);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

//    /**
//     * add SBML reactions to the model
//     * this function calls teh appropriate function dependent
//     * on whether we are working from a Pathway of a List<Event>
//     */
//    private void addAllReactions() {
//        if (thisPathway != null) {
//            addAllReactions(thisPathway);
//        }
//        else if (thisListEvents != null) {
//            addAllReactions(thisListEvents);
//        }
//    }
//
//
    /**
     * Add elements from the given Pathway. This will rescurse
     * through child Events that represent Pathways.
     *
     * @param pathway  Pathway from ReactomeDB
     */
    private void addAllReactions(Pathway pathway){
        if (pathway.getHasEvent() != null) {
            for (Event event : pathway.getHasEvent()) {
                addReaction(event);
                if (event instanceof Pathway){
                    Pathway path = ((Pathway)(event));
                    addAllReactions(path);
                }
            }
        }

    }
//
//    /**
//     * Add SBML Reactions from the given List<Event></Event>. This will rescurse
//     * through child Events that represent Pathways.
//     *
//     * @param eventList  List<Event></Event> from ReactomeDB
//     */
//    private void addAllReactions(List<Event> eventList){
//        for (Event event : eventList) {
//            addReaction(event);
//            if (event instanceof Pathway){
//                Pathway path = ((Pathway)(event));
//                addAllReactions(path);
//            }
//        }
//    }
//
    /**
     * Overloaded addReaction function to cast an Event to a Reaction.
     *
     * @param event  Event from ReactomeDB
     */
    private void addReaction(org.reactome.server.graph.domain.model.Event event){
        BiochemicalReaction r = thisModel.addNew(BiochemicalReaction.class, "r" + getNumber());
//        if (event instanceof org.reactome.server.graph.domain.model.ReactionLikeEvent) {
//            addReaction((org.reactome.server.graph.domain.model.ReactionLikeEvent ) (event));
//        }
    }

    /**
     * Adds the given Reactome Reaction to the SBML model as an SBML Reaction.
     * This in turn adds SBML species and SBML compartments.
     *
     * @param event  Reaction from ReactomeDB
     */
    private void addReaction(org.reactome.server.graph.domain.model.ReactionLikeEvent event){
        // will need to work out type
        BiochemicalReaction r = thisModel.addNew(BiochemicalReaction.class, "r" + getNumber());


//        Model model = sbmlDocument.getModel();
//
//        String id = "reaction_" + event.getDbId();
//        if (!loggedReactions.contains(id)) {
//            Reaction rn = model.createReaction(id);
//            setMetaid(rn);
//            rn.setFast(false);
//            rn.setReversible(false);
//            rn.setName(event.getDisplayName());
//            if (event.getInput() != null) {
//                for (PhysicalEntity pe : event.getInput()) {
//                    addParticipant("reactant", rn, pe, event.getDbId(), null);
//                }
//            }
//            if (event.getOutput() != null) {
//                for (PhysicalEntity pe : event.getOutput()) {
//                    addParticipant("product", rn, pe, event.getDbId(), null);
//                }
//            }
//            if (event.getCatalystActivity() != null) {
//                for (CatalystActivity cat : event.getCatalystActivity()) {
//                    if (cat.getPhysicalEntity() != null) {
//                        addParticipant("catalyst", rn, cat.getPhysicalEntity(), event.getDbId(), null);
//                    }
//                }
//            }
//            if (event.getPositivelyRegulatedBy() != null) {
//                for (PositiveRegulation reg : event.getPositivelyRegulatedBy()) {
//                    DatabaseObject pe = reg.getRegulator();
//                    if (pe instanceof PhysicalEntity) {
//                        addParticipant("pos_regulator", rn, (PhysicalEntity) (pe), event.getDbId(), reg);
//                    }
//                }
//            }
//            if (event.getNegativelyRegulatedBy() != null) {
//                for (NegativeRegulation reg : event.getNegativelyRegulatedBy()) {
//                    DatabaseObject pe = reg.getRegulator();
//                    if (pe instanceof PhysicalEntity) {
//                        addParticipant("neg_regulator", rn, (PhysicalEntity) (pe), event.getDbId(), reg);
//                    }
//                }
//            }
//            if (addAnnotations) {
//                CVTermBuilder cvterms = new CVTermBuilder(rn);
//                cvterms.createReactionAnnotations(event);
//                NotesBuilder notes = new NotesBuilder(rn);
//                notes.addPathwayNotes(event);
//            }
//
//            loggedReactions.add(id);
//        }
    }
//
//    /**
//     * Adds the participants in a Reaction to the SBML Reaction as speciesReferences
//     * and adds the associated SBML Species where necessary.
//     *
//     * @param type      String representing "reactant" or "product"
//     * @param rn        SBML Reaction to add to
//     * @param pe        PhysicalEntity from ReactomeDB - the participant being added
//     * @param event_no  Long number respresenting the ReactomeDB id of the Reactome Event being processed.
//     *                  (This is used in the speciesreference id.)
//     */
//    private void addParticipant(String type, Reaction rn, PhysicalEntity pe, Long event_no, Regulation reg) {
//
//        String speciesId = "species_" + pe.getDbId();
//        addSpecies(pe, speciesId);
//        if (type.equals("reactant")) {
//            String sr_id = "speciesreference_" + event_no + "_input_" + pe.getDbId();
//            if (!loggedSpeciesReferences.contains(sr_id)) {
//                SpeciesReference sr = rn.createReactant(sr_id, speciesId);
//                sr.setConstant(true);
//                sbo.setTerm(type, sr);
//                loggedSpeciesReferences.add(sr_id);
//            }
//        }
//        else if (type.equals("product")){
//            String sr_id = "speciesreference_" + event_no + "_output_" + pe.getDbId();
//            if (!loggedSpeciesReferences.contains(sr_id)) {
//                SpeciesReference sr = rn.createProduct(sr_id, speciesId);
//                sr.setConstant(true);
//                sbo.setTerm(type, sr);
//                loggedSpeciesReferences.add(sr_id);
//            }
//        }
//        else if (type.equals("catalyst")){
//            String sr_id = "modifierspeciesreference_" + event_no + "_catalyst_" + pe.getDbId();
//            if (!loggedSpeciesReferences.contains(sr_id)) {
//                ModifierSpeciesReference sr = rn.createModifier(sr_id, speciesId);
//                sbo.setTerm(type, sr);
//                loggedSpeciesReferences.add(sr_id);
//            }
//        }
//        else if (type.equals("pos_regulator")){
//            String sr_id = "modifierspeciesreference_" + event_no + "_positiveregulator_" + pe.getDbId();
//            if (!loggedSpeciesReferences.contains(sr_id)) {
//                ModifierSpeciesReference sr = rn.createModifier(sr_id, speciesId);
//                sbo.setTerm(type, sr);
//                if (addAnnotations && reg != null) {
//                    NotesBuilder notes = new NotesBuilder(sr);
//                    notes.createSpeciesReferenceNotes(reg);
//                    notes.addNotes();
//                }
//                loggedSpeciesReferences.add(sr_id);
//            }
//        }
//        else if (type.equals("neg_regulator")){
//            String sr_id = "modifierspeciesreference_" + event_no + "_negativeregulator_" + pe.getDbId();
//            if (!loggedSpeciesReferences.contains(sr_id)) {
//                ModifierSpeciesReference sr = rn.createModifier(sr_id, speciesId);
//                sbo.setTerm(type, sr);
//                if (addAnnotations && reg != null) {
//                    NotesBuilder notes = new NotesBuilder(sr);
//                    notes.createSpeciesReferenceNotes(reg);
//                    notes.addNotes();
//                }
//                loggedSpeciesReferences.add(sr_id);
//            }
//        }
//    }
//
//
//    /**
//     * Adds an SBML species to the model.
//     *
//     * @param pe    PhysicalEntity from ReactomeDB
//     * @param id    String representing the id to use for the SBML species.
//     *              (This was already created so may as well just pass as argument.)
//     */
//    private void addSpecies(PhysicalEntity pe, String id){
//        Model model = sbmlDocument.getModel();
//
//        // TODO: what if there is more than one compartment listed
//        org.reactome.server.graph.domain.model.Compartment comp = pe.getCompartment().get(0);
//        String comp_id = "compartment_" + comp.getDbId();
//
//        if (!loggedSpecies.contains(id)) {
//            Species s = model.createSpecies(id);
//            setMetaid(s);
//            s.setName(pe.getDisplayName());
//            s.setCompartment(comp_id);
//            // set other required fields for SBML L3
//            s.setBoundaryCondition(false);
//            s.setHasOnlySubstanceUnits(false);
//            s.setConstant(false);
//            sbo.setTerm(s, pe);
//
//            if (addAnnotations){
//                CVTermBuilder cvterms = new CVTermBuilder(s);
//                cvterms.createSpeciesAnnotations(pe);
//                NotesBuilder notes = new NotesBuilder(s);
//                notes.createSpeciesNotes(pe);
//                notes.addNotes();
//            }
//
//            loggedSpecies.add(id);
//        }
//
//        addCompartment(comp, comp_id);
//    }
//
//    /**
//     * Add an SBML compartment to the model.
//     *
//     * @param comp  Compartment from ReactomeDB
//     * @param id    String representing the id to use for the SBML compartment.
//     *              (This was already created so may as well just pass as argument.)
//     */
//    private void addCompartment(org.reactome.server.graph.domain.model.Compartment comp, String id){
//         Model model = sbmlDocument.getModel();
//
//         if (!loggedCompartments.contains(id)){
//             Compartment c = model.createCompartment(id);
//             setMetaid(c);
//             c.setName(comp.getDisplayName());
//             c.setConstant(true);
//             sbo.setTerm(c, comp);
//
//             if (addAnnotations){
//                 CVTermBuilder cvterms = new CVTermBuilder(c);
//                 cvterms.createCompartmentAnnotations(comp);
//             }
//
//            loggedCompartments.add(id);
//        }
//    }

//    /**
//     * Set the metaid of the object and increase the count to ensure uniqueness.
//     *
//     * @param object    SBML SBase object
//     */
////    private void setMetaid(SBase object){
////        object.setMetaId("metaid_" + metaid_count);
////        metaid_count++;
////    }

    private String getNumber() {
        String ret = String.valueOf(metaid_count);
        metaid_count++;
        return ret;
    }
}
