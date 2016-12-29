package org.reactome.server.tools.launcher;

import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.service.SpeciesService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reactome.server.tools.exporters.biopaxexport.WriteBioPAX3;
import org.reactome.server.tools.launcher.ExporterLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Sarah Keating <skeating@ebi.ac.uk>
 */
public class BioPAX3ExporterLauncher {

    private static int dbVersion;

    private static String outputdir;

    BioPAX3ExporterLauncher(int db, String output){
        dbVersion = db;
        outputdir = output;
    }

    /**
     * Create the output file and write the SBML file for this path
     *
     * @param path ReactomeDB Pathway to output
     */
    public static void outputPath(Pathway path) {
        String filename = path.getDbId() + ".owl";
        File out = new File(outputdir, filename);
        WriteBioPAX3 bp = new WriteBioPAX3(path, dbVersion);
        bp.createModel();
        bp.toStdOut();
//        WriteSBML sbml = new WriteSBML(path, dbVersion);
//        sbml.setAnnotationFlag(true);
//        sbml.createModel();
//        sbml.toStdOut();
        bp.toFile(out);
    }

    public static void outputEvents(List<Event> loe){
//        WriteSBML sbml = new WriteSBML(loe, dbVersion);
//        sbml.setAnnotationFlag(true);
//        sbml.createModel();
//        sbml.toStdOut();
//        String filename = sbml.getModelId() + ".xml";
//        File out = new File(outputdir, filename);
//        sbml.toFile(out.getPath());

    }
}

