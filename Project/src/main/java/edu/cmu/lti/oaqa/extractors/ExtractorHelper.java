/*
 *  Copyright 2013 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cmu.lti.oaqa.extractors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.cleartk.token.type.Sentence;

import edu.cmu.lti.oaqa.framework.types.InputElement;

/**
 * A helper class to facilitate extraction of information from
 * an annotated text.
 * 
 * @author Leonid Boytsov
 */
public class ExtractorHelper extends JCasAnnotator_ImplBase {
  private ArrayList<Extractor>    extrList;
  private final String            EXTR_LIST = "extractor_list";
  
  @Override
  public void initialize(UimaContext aContext)
  throws ResourceInitializationException
  {
    super.initialize(aContext);
    
    getContext().getLogger().setLevel(Level.INFO);
    
    String listconf = (String) aContext.getConfigParameterValue(EXTR_LIST);
    
    if (listconf == null) {
      throw new ResourceInitializationException( 
                               new Exception("Missing the list of extractors: '" 
                                              + EXTR_LIST + "'"));
    }    
    
    String[] names = listconf.split(",");
    
    extrList = new ArrayList<Extractor>();
    
    for (String extrName: names) {
      try {
        extrList.add((Extractor) Class.forName(extrName).newInstance());
      } catch (InstantiationException | 
               IllegalAccessException | ClassNotFoundException e) {
        e.printStackTrace();
        throw new ResourceInitializationException( 
                         new Exception("Cannot create extractor: " + extrName));  
      }
    }
  }
  
  @Override
  public void process(JCas aJCas)
  throws AnalysisEngineProcessException {
    try {  
      String quiid = null;
      FSIterator<Annotation> it = aJCas.getAnnotationIndex(InputElement.type).iterator();
      if (it.hasNext()) {
        InputElement elem = (InputElement)it.next();
        quiid = elem.getQuuid();
      } else {
        throw new Exception("Cannot find the docUri!");
      }
      
      FSIterator<Annotation> sentenceIterator = AnnotUtils.getIterator(aJCas, 
                                                        Sentence.typeIndexID); 
      
      ArrayList<ArrayList<String>> resList = new ArrayList<ArrayList<String>>();
      
      for (int j = 0; j < extrList.size(); j++) {
        resList.add(new ArrayList<String>());
      }
      
      String FileName = new File(quiid).getName();
      
      String FileNameParts[] = FileName.split("\\.");
      String CountryName = FileNameParts[0].replace('_', ' ');
      
      TreeSet<String> exceptions = new TreeSet<String>();
      exceptions.add(CountryName);
      
      getContext().getLogger().log(Level.INFO, "DOCUMENT: " 
          + FileName + " Country: " + CountryName);

      
      for (int counter = 1; 
           sentenceIterator.isValid(); 
           counter++, sentenceIterator.moveToNext()) {
        Annotation  sentence = sentenceIterator.get();            
        /*
        getContext().getLogger().log(Level.INFO, "SENTENCE " + counter + 
                " begin: " + sentence.getBegin() + " end: " + sentence.getEnd());
        */
        for (int i = 0; i < extrList.size(); ++i) {
          Extractor           e   = extrList.get(i);
          ArrayList<String>   res = resList.get(i); 
          
          e.process(aJCas, sentence, res);
        }               
      }
      
      for (int i = 0; i < extrList.size(); ++i) {
        Extractor           e   = extrList.get(i);
        ArrayList<String>   res = resList.get(i);
        Set<String>         setRes = e.aggregate(res, exceptions);
        
        getContext().getLogger().log(Level.INFO, FileName + "#"  
                                     + e.getClass().getName() + " : "+ 
                                     StringUtils.join(setRes.toArray(), ';'));
      }
      
    } catch (Exception e) {
        throw new AnalysisEngineProcessException(e);
    } 

  }  
}
