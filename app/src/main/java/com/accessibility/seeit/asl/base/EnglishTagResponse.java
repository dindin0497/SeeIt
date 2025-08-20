

package com.accessibility.seeit.asl.base;


import java.io.Serializable;
import java.util.List;

public class EnglishTagResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String inputSentence;
	private String grammarCorrected;
	private List<WordTagging> tagWords;
	private boolean question;
	private EnglishGrammarCheckResponse englishGrammarCheckResponse;

	public String getInputSentence() {
		return inputSentence;
	}

	public void setInputSentence(String inputSentence) {
		this.inputSentence = inputSentence;
	}

	public String getGrammarCorrected() {
		return grammarCorrected;
	}

	public void setGrammarCorrected(String grammarCorrected) {
		this.grammarCorrected = grammarCorrected;
	}

	public List<WordTagging> getTagWords() {
		return tagWords;
	}

	public void setTagWords(List<WordTagging> tagWords) {
		this.tagWords = tagWords;
	}

	public boolean isQuestion() {
		return question;
	}

	public void setQuestion(boolean question) {
		this.question = question;
	}

	public EnglishGrammarCheckResponse getEnglishGrammarCheckResponse() {
		return englishGrammarCheckResponse;
	}

	public void setEnglishGrammarCheckResponse(EnglishGrammarCheckResponse englishGrammarCheckResponse) {
		this.englishGrammarCheckResponse = englishGrammarCheckResponse;
	}
}