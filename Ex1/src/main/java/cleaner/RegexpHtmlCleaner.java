/*
 * Regexp-based, simple, HTML-cleaner
 *
 * Author: Leonid Boytsov
 * Copyright (c) 2013
 *
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */

package cleaner;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

public class RegexpHtmlCleaner extends AbstractHtmlCleaner {

  public RegexpHtmlCleaner(UimaContext aContext) throws ResourceInitializationException {
  }

  @Override
  public String CleanText(String html, String encoding) {
    return LeoCleanerUtil.SimpleProc(html).getSecond();
  }

}
