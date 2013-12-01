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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.cleartk.token.type.Sentence;

import edu.cmu.lti.SemanticRole;
import edu.cmu.lti.POS;
import edu.cmu.lti.NamedEntity;
import edu.cmu.lti.Chunk;


/**
 * A helper class to align different word annotations.
 * This annotations should be produced by the class SennaAnnotator.
 * 
 * @author Leonid Boytsov
 */
public class AlignSennaAnnotations extends JCasAnnotator_ImplBase {
  @Override
  public void initialize(UimaContext aContext)
  throws ResourceInitializationException
  {
    super.initialize(aContext);
    
    getContext().getLogger().setLevel(Level.INFO);
  }
  
  FSIterator<Annotation> getIterator(JCas aJCas, int typeIndexID) {
    AnnotationIndex<Annotation> Index = 
        aJCas.getJFSIndexRepository().getAnnotationIndex(typeIndexID);
    return Index.iterator();           
  }
  
  FSIterator<Annotation> getSubIterator(JCas aJCas, int typeIndexID,
                               Annotation Cover, boolean strict) {
    AnnotationIndex<Annotation> Index = 
        aJCas.getJFSIndexRepository().getAnnotationIndex(typeIndexID);
    return Index.subiterator(Cover, false, strict);           
  }  
  
  @Override
  public void process(JCas aJCas)
  throws AnalysisEngineProcessException {
    try {      
      FSIterator<Annotation> sentenceIterator = getIterator(aJCas, 
                                                             Sentence.typeIndexID); 
      
      for (int counter = 1; 
           sentenceIterator.isValid(); 
           counter++, sentenceIterator.moveToNext()) {
        Annotation  sentence = sentenceIterator.get();            

        getContext().getLogger().log(Level.INFO, "SENTENCE " + counter + 
                " begin: " + sentence.getBegin() + " end: " + sentence.getEnd());
        
        FSIterator<Annotation> wordIterator = getSubIterator(aJCas, POS.typeIndexID,
                                                             sentence, true);
        
        for (int wordNo = 1; 
             wordIterator.isValid(); 
             ++wordNo, wordIterator.moveToNext()) {
          POS pos = (POS)wordIterator.get();
          
          getContext().getLogger().log(Level.INFO, 
                                       "Word # " + wordNo + " : " + 
                                       pos.getCoveredText() + ":" + pos.getTag());
          
        }        
      }
      
    } catch (Exception e) {
        throw new AnalysisEngineProcessException(e);
    } 

  }  
}
