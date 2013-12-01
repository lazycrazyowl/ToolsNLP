ToolsNLP
========

UIMA-ECD wrappers for various basic NLP tools. This was used for a course-project. For more details on UIMA-ECD, please, see https://github.com/oaqa/oaqa-tutorial

Pre-requisits: Java, Python (nltk.util.clean_html should be installed), and the Unix command-line utility html2text

Sub-project:

1. Ex1: five simple HTML cleaners (regexp, my own cleaner, Apache Tika, NLTK, and Unix html2text). You can run them all by executing the script launches/runAll.sh
2. Ex2: wrappers for sentence segmenters and tokenizers. You can run the sample pipeline by executing the script launches/run_ex2.sh
3. Ex3: the wrapper for clearTK/OpenNLP POS tagger.
4. Project: a rudimentary proof-of-concept information extractor. It attemps to extract the following information from Wikipedia descriptions of countries: capital, languages spoken, religion.


Additional requirements:

1. Unix utility html2text
2. Python + nltk.util.html_clean
3. Compiled Senna parser (http://ml.nec-labs.com/senna/).
