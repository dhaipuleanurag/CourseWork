package Trial1Package;
import java.io.*;
import opennlp.maxent.*;
import opennlp.maxent.io.*;

// reads line with tab separated features
//  writes feature[0] (token) and predicted tag

public class MEtag {

    public static void main (String[] args) {
	//if (args.length != 3) {
	//    System.err.println ("MEtag requires 3 arguments:  dataFile model responseFile");
	//    System.exit(1);
	//}
	String dataFileName = "D://Academics//Courses//NLP//hw5_programming//WSJ_CHUNK_CORPUS_FOR_STUDENTS//features_test.feature";
	String modelFileName = "D://Academics//Courses//NLP//hw5_programming//WSJ_CHUNK_CORPUS_FOR_STUDENTS//model.model";
	String responseFileName = "D://Academics//Courses//NLP//hw5_programming//WSJ_CHUNK_CORPUS_FOR_STUDENTS//result.chunk";
	try {
	    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
	    BufferedReader dataReader = new BufferedReader (new FileReader (dataFileName));
	    PrintWriter responseWriter = new PrintWriter (new FileWriter (responseFileName));
	    String priorTag = "#";
	    String p_priorTag = "##";
	    String line;
	    while ((line = dataReader.readLine()) != null) {
		if (line.equals("")) {
		    responseWriter.println();
		    priorTag = "#";
		    p_priorTag = "##";
		} else {
		    line = line.replaceAll("@@", priorTag);
		    line = line.replaceAll("&&", p_priorTag);
		    String[] features = line.split("\t");
		    String tag = m.getBestOutcome(m.eval(features));
		    responseWriter.println(features[0] + "\t" + tag);
		    p_priorTag = priorTag;
		    priorTag = tag;
		}
	    }
	    responseWriter.close();
	} catch (Exception e) {
	    System.out.print("Error in data tagging: ");
	    System.out.println(e);
	}
    }

}