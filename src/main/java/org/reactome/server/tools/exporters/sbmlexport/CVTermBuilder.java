package org.reactome.server.tools.exporters.sbmlexport;

import org.reactome.server.graph.domain.model.*;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.SBase;
import org.reactome.server.graph.domain.model.CandidateSet;

import java.util.List;

/**
 * @author Sarah Keating <skeating@ebi.ac.uk>
 */

class CVTermBuilder extends AnnotationBuilder {

    CVTermBuilder(SBase sbase) {

        super(sbase);
    }

    /**
     * Adds the resources for a model. This uses BQB_IS to link to the Reactome entry
     * and BQB_IS_DESCRIBED_BY to link to any relevant publications.
     *
     * @param path  Pathway instance from ReactomeDB
     */
    void createModelAnnotations(Pathway path) {
        addResource("reactome", CVTerm.Qualifier.BQB_IS, path.getStId());
        addPublications(path.getLiteratureReference());
        createCVTerms();
    }

    void createModelAnnotations(List<Event> listOfEvents) {
        for (Event e : listOfEvents) {
            addPublications(e.getLiteratureReference());
        }
        createCVTerms();
    }
    /**
     * Adds the resources for a SBML reaction. This uses BQB_IS to link to the Reactome entry;
     * BQB_IS to link to any GO biological processes
     * and BQB_IS_DESCRIBED_BY to link to any relevant publications.
     *
     * @param event   Event instance from ReactomeDB
     */
    void createReactionAnnotations(org.reactome.server.graph.domain.model.ReactionLikeEvent event) {
        addResource("reactome", CVTerm.Qualifier.BQB_IS, event.getStId());
        addGOTerm(event);
        addECNumber(event);
        addPublications(event.getLiteratureReference());
        createCVTerms();
    }

    /**
     * Adds the resources for a SBML species. This uses BQB_IS to link to the Reactome entry
     * and then calls createPhysicalEntityAnnotations to deal with the particular type.
     *
     * @param pe  PhysicalEntity from ReactomeDB
     */
    void createSpeciesAnnotations(PhysicalEntity pe){
        addResource("reactome", CVTerm.Qualifier.BQB_IS, pe.getStId());
        createPhysicalEntityAnnotations(pe, CVTerm.Qualifier.BQB_IS, true);
        createCVTerms();
    }

    /**
     * Adds the resources for a SBML compartment. This uses BQB_IS to link to the Reactome entry.
     *
     * @param comp  Compartment from ReactomeDB
     */
    void createCompartmentAnnotations(org.reactome.server.graph.domain.model.Compartment comp){
        addResource("go", CVTerm.Qualifier.BQB_IS, comp.getAccession());
        createCVTerms();
    }

    private void addPublications(List<Publication> publications) {
        if (publications == null || publications.size() == 0) {
            return;
        }
        for (Publication pub : publications) {
            if (pub instanceof LiteratureReference) {
                Integer pubmed = ((LiteratureReference) pub).getPubMedIdentifier();
                if (pubmed != null) {
                    addResource("pubmed", CVTerm.Qualifier.BQB_IS_DESCRIBED_BY, pubmed.toString());
                }
            }
        }

    }


    /**
     * Function to determine GO terms associated with the event
     *
     * @param event ReactionLikeEvent from ReactomeDB
     */
    private void addGOTerm(org.reactome.server.graph.domain.model.ReactionLikeEvent event){
        if (event.getGoBiologicalProcess() != null) {
            addResource("go", CVTerm.Qualifier.BQB_IS, event.getGoBiologicalProcess().getAccession());
        }
        else if (event.getCatalystActivity() != null && event.getCatalystActivity().size() > 0) {
            CatalystActivity cat = event.getCatalystActivity().get(0);
            GO_MolecularFunction goterm = cat.getActivity();
            if (goterm != null){
                addResource("go", CVTerm.Qualifier.BQB_IS, cat.getActivity().getAccession());
            }
        }
    }

    private void addECNumber(org.reactome.server.graph.domain.model.ReactionLikeEvent event) {
        if (event.getCatalystActivity() != null && event.getCatalystActivity().size() > 0) {
            for (CatalystActivity cat : event.getCatalystActivity()) {
                String ecnum = cat.getActivity().getEcNumber();
                if (ecnum != null) {
                    addResource("ec-code", CVTerm.Qualifier.BQB_IS, ecnum);
                }
            }
        }
    }
    /**
     * Adds the resources relating to different types of PhysicalEntity. In the case of a Complex
     * it will iterate through all the components.
     *
     * @param pe            PhysicalEntity from ReactomeDB
     * @param qualifier     The MIRIAM qualifier for the reference
     */
    private void createPhysicalEntityAnnotations(PhysicalEntity pe, CVTerm.Qualifier qualifier, boolean recurse){
        if (pe instanceof SimpleEntity){
            SimpleEntity se = (SimpleEntity)(pe);
            if (se.getReferenceEntity() != null) {
                addResource("chebi", qualifier, (se.getReferenceEntity().getIdentifier()));
            }
            String ref = getKeggReference(se.getCrossReference());
            if (ref.length() > 0){
                addResource("kegg", qualifier, ref);
            }
        }
        else if (pe instanceof EntityWithAccessionedSequence){
            ReferenceEntity ref = ((EntityWithAccessionedSequence)(pe)).getReferenceEntity();
            if (ref != null) {
                addResource(ref.getDatabaseName(), qualifier, ref.getIdentifier());
            }
            if (recurse) {
                List<PhysicalEntity> inferences = pe.getInferredTo();
                if (inferences != null) {
                    for (PhysicalEntity inf : inferences) {
                        addResource("reactome", CVTerm.Qualifier.BQB_IS_HOMOLOG_TO, inf.getStId());
                        // could add nested annotation but decided not to at present
                    }
                }
                inferences = pe.getInferredFrom();
                if (inferences != null) {
                    for (PhysicalEntity inf : inferences) {
                        addResource("reactome", CVTerm.Qualifier.BQB_IS_HOMOLOG_TO, inf.getStId());
                        // could add nested annotation but decided not to at present
                    }
                }
                List<AbstractModifiedResidue> mods = ((EntityWithAccessionedSequence) pe).getHasModifiedResidue();
                if (mods != null) {
                    for (AbstractModifiedResidue inf : mods) {
                        if ((inf instanceof TranslationalModification) && ((TranslationalModification)(inf)).getPsiMod() != null){
                            PsiMod psi = ((TranslationalModification)(inf)).getPsiMod();
                            addResource(psi.getDatabaseName(), CVTerm.Qualifier.BQB_HAS_VERSION, psi.getIdentifier());
                        }
                    }
                }
            }
        }
        else if (pe instanceof Complex){
            if (((Complex)(pe)).getHasComponent() != null) {
                for (PhysicalEntity component : ((Complex) (pe)).getHasComponent()) {
                    createPhysicalEntityAnnotations(component, CVTerm.Qualifier.BQB_HAS_PART, false);
                }
            }
        }
        else if (pe instanceof EntitySet){
            if (((EntitySet)(pe)).getHasMember() != null) {
                for (PhysicalEntity member : ((EntitySet) (pe)).getHasMember()) {
                    createPhysicalEntityAnnotations(member, CVTerm.Qualifier.BQB_HAS_PART, false);
                }
            }
        }
        else if (pe instanceof Polymer){
            if (((Polymer) pe).getRepeatedUnit() != null) {
                for (PhysicalEntity component : ((Polymer) (pe)).getRepeatedUnit()) {
                    createPhysicalEntityAnnotations(component, CVTerm.Qualifier.BQB_HAS_PART, false);
                }
            }
        }
        else {
            // a GenomeEncodedEntity adds no additional annotation
            if (!(pe instanceof GenomeEncodedEntity)){
                // the only thing left should be an OtherEntity which
                // also adds no further annotation
                if (!(pe instanceof OtherEntity)) {
                    System.err.println("Encountered unrecognised physical entity");
                }
            }
        }
    }

    /**
     * Find the KEGG compound reference
     *
     * @param references List<DatabaseIdentifiers> from ReactomeDB that might contain a kegg
     *
     * @return the KEGG reference identifier or empty string if none present
     */
    private String getKeggReference(List<DatabaseIdentifier> references){
        if (references != null) {
            for (DatabaseIdentifier ref : references) {
                if (ref.getDatabaseName().equals("COMPOUND")) {
                    return ref.getIdentifier();
                }
            }
        }
        return "";
    }
}
