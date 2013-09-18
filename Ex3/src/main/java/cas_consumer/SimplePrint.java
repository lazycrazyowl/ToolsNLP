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

public class SimplePrint extends CasConsumer_ImplBase {
	String   outputDir;
  Boolean  oConsole;


	public void initialize(UimaContext context) throws ResourceInitializationException {
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

  	  String cleanedText = aJCas.getDocumentText();

      System.out.println("============================================================");
      System.out.println(cleanedText);
      System.out.println("============================================================");
        
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
