/** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.syntax.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.cleartk.util.IOUtil;
import org.cleartk.util.ParamUtil;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.TypeCapability;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.annotations.Beta;

/**
 * Modified by Leonid Boytsov, so that the class can be used with UIMA-ECD.
 *
 * <br>
 * Copyright (c) 2007-2008, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * 
 * @author Philip Ogren
 * @author Leonid Boytsov (UIMA-ECD adaptation)
 * 
 */
@Beta
@TypeCapability(inputs = { "org.cleartk.token.type.Sentence", "org.cleartk.token.type.Token" })
public class PosTaggerAnnotatorECD extends JCasAnnotator_ImplBase {
  public static final String PARAM_POSTAG_MODEL_FILE = "postagModelFile";

  @ConfigurationParameter(
      name = PARAM_POSTAG_MODEL_FILE,
      mandatory = true,
      description = "provides the path of the OpenNLP part-of-speech tagger model file, e.g.  resources/models/OpenNLP.POSTags.English.bin.gz.  See javadoc for opennlp.maxent.io.SuffixSensitiveGISModelReader.")
  private String postagModelFile;

  protected POSTagger posTagger;

  @Override
  public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
    super.initialize(uimaContext);
    try {
      postagModelFile = (String) uimaContext.getConfigParameterValue(PARAM_POSTAG_MODEL_FILE);

      if (null == postagModelFile) {
        throw new ResourceInitializationException(new Exception("Parameter " + PARAM_POSTAG_MODEL_FILE + " should not be empty!"));
      }

      InputStream modelInputStream = IOUtil.getInputStream(
          PosTaggerAnnotator.class,
          postagModelFile);
      POSModel posModel = new POSModel(modelInputStream);
      posTagger = new POSTaggerME(posModel);
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }
  }

  @Override
  public void process(JCas jCas) throws AnalysisEngineProcessException {
    for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
      List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sentence);
      String[] tokenTexts = new String[tokens.size()];
      for (int i = 0; i < tokenTexts.length; ++i) {
        tokenTexts[i] = tokens.get(i).getCoveredText();
      }
      String[] tags = posTagger.tag(tokenTexts);
      for (int i = 0; i < tags.length; ++i) {
        tokens.get(i).setPos(tags[i]);
      }
    }
  }
}
