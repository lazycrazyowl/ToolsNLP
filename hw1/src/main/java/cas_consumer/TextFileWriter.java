/*
 * A writer of text obtained by document cleaning, i.e.,
 * by removing HTML tags.
 *
 * Author: Leonid Boytsov (the code is based on the UIMA example AnnotationPrinter.
 * Copyright (c) 2013
 *
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */

package cas_consumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import org.uimafit.component.CasConsumer_ImplBase;

import ToolsNLP.Document;

import edu.cmu.lti.oaqa.framework.types.InputElement;

public class TextFileWriter extends CasConsumer_ImplBase {
	String   outputDir;
  Boolean  oConsole;

	public TextFileWriter() {
	}

	/**
	 * Initializes this CAS Consumer with the parameters specified in the
	 * descriptor.
	 * 
	 * @throws ResourceInitializationException
	 *             if there is error in initializing the resources
	 */
	public void initialize(UimaContext context) throws ResourceInitializationException {

		// extract configuration parameter settings
		outputDir = (String) context.getConfigParameterValue("outputDir");
		oConsole = (Boolean) context.getConfigParameterValue("printToConsole");

    if (oConsole == null) {
      oConsole = false;
    }

		// Output file should be specified in the descriptor
		if (outputDir == null) {
			throw new ResourceInitializationException(
					ResourceInitializationException.CONFIG_SETTING_ABSENT,
					new Object[] { "outputDir" });
		}
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(CAS)
	 */
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
      JCas aJCas = null;
      try {
        aJCas = aCAS.getJCas();
      } catch (CASException e) {
        throw new AnalysisEngineProcessException(e);
      }

      String fileName, cleanedText;
  
      FSIndex<Annotation> index1 = aJCas.getAnnotationIndex(InputElement.type);    
      FSIterator<Annotation> iter1 = index1.iterator();
          
      if (iter1.hasNext()) {
        InputElement elem = (InputElement)iter1.next();
              
  			fileName = elem.getQuuid();
  		} else {
        throw new AnalysisEngineProcessException(new Exception("Missing document info!"));
      }
  
      FSIndex<Annotation> index = aJCas.getAnnotationIndex(Document.type);    
      FSIterator<Annotation> iter = index.iterator();
          
      if (iter.hasNext()) {
        Document elem = (Document)iter.next();
              
  			cleanedText = elem.getText();
  		} else {
        throw new AnalysisEngineProcessException(new Exception("Missing document text!"));
      }
  
      try {
        File f = new File(fileName);
        String nm = f.getName();
  
        int ind = nm.lastIndexOf(".");
        if (ind != -1) {
          nm = nm.substring(0, ind);
        }
  
        nm = nm + ".txt";
  
        File outFile = new File(outputDir + "/" + nm);
        System.out.println("Writing to the file: '" + outFile.getCanonicalPath() + "'");
  
  	  FileWriter writer = new FileWriter(outFile);
  
  	  writer.write(cleanedText);
  
        if (oConsole) {
          System.out.println("============================================================");
          System.out.println(cleanedText);
          System.out.println("============================================================");
        }
        
  	  writer.close();
      } catch (IOException e) {
        throw new AnalysisEngineProcessException(e);
      }
	}

	/**
	 * Called when a batch of processing is completed.
	 * @see org.apache.uima.collection.CasConsumer#batchProcessComplete(ProcessTrace)
	 */
	public void batchProcessComplete(ProcessTrace aTrace)
			throws ResourceProcessException, IOException {
		// nothing to do in this case so far
	}

	/**
	 * Called when the entire collection is completed.
	 * @see org.apache.uima.collection.CasConsumer#collectionProcessComplete(ProcessTrace)
	 */
	public void collectionProcessComplete(ProcessTrace aTrace)
			throws ResourceProcessException, IOException {
		// nothing to do in this case so far
	}

	/**
	 * @see org.apache.uima.resource.ConfigurableResource#reconfigure()
	 */
	public void reconfigure(UimaContext context) throws ResourceConfigurationException {
    throw new ResourceConfigurationException(new Exception("Not implemented!"));
	}

	/**
	 * Called if clean up is needed in case of exit under error conditions.
	 * 
	 * @see org.apache.uima.resource.Resource#destroy()
	 */
	public void destroy() {
    // nothing to do so far
	}

}
