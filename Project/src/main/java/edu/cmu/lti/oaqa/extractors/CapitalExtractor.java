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

import java.util.ArrayList;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.NamedEntity;
import edu.cmu.lti.SemanticRole;

public class CapitalExtractor extends Extractor {
  // Just a fake constructor, to be able to create an instance 
  // using forName(...).newinstance()
  public CapitalExtractor() {}
  @Override
  void process(JCas aJCas, Annotation sentence, ArrayList<String> res) {
    FSIterator<Annotation> nerIter = AnnotUtils.getSubIterator(aJCas, 
                                                       NamedEntity.typeIndexID,
                                                       sentence, true);
    
    for (;nerIter.isValid(); nerIter.moveToNext()) {
      NamedEntity  ner = (NamedEntity) nerIter.get();
      
      if (ner.getEntityType().equals("LOC")) {
        FSIterator<Annotation> roleIter = AnnotUtils.getSubIterator(aJCas, 
                                              SemanticRole.typeIndexID,
                                              ner, true);
        boolean ok = false;
        for (;roleIter.isValid(); roleIter.moveToNext()) {
          SemanticRole role = (SemanticRole)roleIter.get();
          SemanticRole parent = role.getParent();
          String       parText = parent.getCoveredText();
          if (parText.toLowerCase().equals("is")) {
            FSArray arrChild = parent.getChildren();
            
            for (int i = 0; i < arrChild.size(); ++i) {
              SemanticRole child = (SemanticRole)arrChild.get(i);
              
              if (child != role && 
                  child.getCoveredText().toLowerCase().contains("capital")) {
                ok = true;
                break;
              }
            }
          }
        }
        if (ok) res.add(ner.getCoveredText());

      }
    }
  }

}
