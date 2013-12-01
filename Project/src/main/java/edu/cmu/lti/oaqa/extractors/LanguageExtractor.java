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

import edu.cmu.lti.Chunk;
import edu.cmu.lti.NamedEntity;
import edu.cmu.lti.SemanticRole;

public class LanguageExtractor extends Extractor {
  // Just a fake constructor, to be able to create an instance 
  // using forName(...).newinstance()
  public LanguageExtractor() {}
  @Override
  public void process(JCas aJCas, Annotation sentence, ArrayList<String> res) {
    FSIterator<Annotation> nerIter = AnnotUtils.getSubIterator(aJCas, 
                                                       NamedEntity.typeIndexID,
                                                       sentence, true);
    
    for (;nerIter.isValid(); nerIter.moveToNext()) {
      NamedEntity  ner = (NamedEntity) nerIter.get();
      
      if (ner.getEntityType().equals("MISC")) {
        // 1st heuristic is based on the connection through the role labels
        FSIterator<Annotation> roleIter = AnnotUtils.getSubIterator(aJCas, 
                                              SemanticRole.typeIndexID,
                                              sentence, true);
        boolean ok = false;
        for (;roleIter.isValid(); roleIter.moveToNext()) {
          SemanticRole role = (SemanticRole)roleIter.get();
          if (ner.getBegin() >= role.getBegin() &&
              ner.getEnd() <= role.getEnd()) {
            if (!role.getLabel().equals("A1")) continue;
            SemanticRole parent = role.getParent();
            if (parent == null) continue;
            String       parText = parent.getCoveredText();
            if (parText.toLowerCase().equals("is") ||
                parText.toLowerCase().equals("are")) {
              FSArray arrChild = parent.getChildren();
              
              for (int i = 0; i < arrChild.size(); ++i) {
                SemanticRole child = (SemanticRole)arrChild.get(i);

                if (/*child != role && */ // sometimes maybe in the same argument
                    child.getCoveredText().toLowerCase().contains("official language")) {
                  ok = true;
                  break;
                }
              }
            }
          }
        }
        // 2d heuristic is based on occurrence in the same noun phrase
        FSIterator<Annotation> chunkIter = AnnotUtils.getSubIterator(aJCas, 
            Chunk.typeIndexID,
            sentence, true);
        for (;chunkIter.isValid(); chunkIter.moveToNext()) {
          Chunk chunk = (Chunk)chunkIter.get();
          if (chunk.getChunkType().equals("NP")) {
              if (chunk.getCoveredText().toLowerCase().contains("official language") &&
              ner.getBegin() >= chunk.getBegin() &&
              ner.getEnd() <= chunk.getEnd()) {
              ok = true;
              break;
            }
          }
        }
        
        String key = ner.getCoveredText();
        if (ok) res.add(key);
        if (ok) System.out.println("## language '" + key + "' found in ## " + sentence.getCoveredText());

      }
    }
  }

}
