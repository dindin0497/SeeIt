
package com.accessibility.seeit.asl.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.accessibility.seeit.asl.base.*;

public class EnglishParserService {

	private TrainedModelParser trainedModelParser = new TrainedModelParser();
	private EnglishGrammarCheck englishGrammarCheck = new EnglishGrammarCheck();

	/**
	 * Parses sentence {@link sentence} into words.
	 * 
	 * @param sentence     sentence to parse into words
	 * @param grammarCheck if the grammar of {@link sentence} should be checked
	 * @return EnglishTagResponse
	 * @throws Exception
	 */
	public EnglishTagResponse getParsedSentence(Context context, String sentence, boolean grammarCheck) throws Exception {
		EnglishTagResponse ret = new EnglishTagResponse();
		ret.setInputSentence(sentence);

		if (grammarCheck) {
			EnglishGrammarCheckResponse checkSentence = englishGrammarCheck.checkGrammar(sentence);
			sentence = checkSentence.getSentence();
			ret.setGrammarCorrected(sentence);
			ret.setEnglishGrammarCheckResponse(checkSentence);
		} else {
			ret.setEnglishGrammarCheckResponse(new EnglishGrammarCheckResponse());
			ret.setGrammarCorrected("Grammar Check = FALSE");
		}

		String[] tokens = sentence.split(" ");
		String[] tags = EnglishPOSTagger.tag(tokens);

		List<WordTagging> tagWords = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			WordTagging wt = new WordTagging();
			wt.setWord(tokens[i]);
			wt.setTag(tags[i]);
			tagWords.add(wt);
		}
		ret.setTagWords(tagWords);
/*
		Tree tree = trainedModelParser.getLexicalizedParserTree(context,sentence);
		if (tree != null) {
			List<WordTagging> tagWords = new ArrayList<>();
			List<Tree> leaves = tree.getLeaves();
			for (Tree leaf : leaves) {
				Tree parent = leaf.parent(tree);
				tagWords.add(new WordTagging(leaf.label().value(), parent.label().value()));
			}
			ret.setTagWords(tagWords);
		} else
			throw new Exception("Parsed Tree is Empty");*/
		return ret;
	}
}