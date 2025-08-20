
package com.accessibility.seeit.asl.base;

import java.util.List;

public class ASLResponse {

	private String sentence;
	private EnglishTagResponse englishTagResponse;
	private EnglishGrammarCheckResponse englishGrammarCheckResponse;

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public EnglishTagResponse getEnglishTagResponse() {
		return englishTagResponse;
	}

	public void setEnglishTagResponse(EnglishTagResponse englishTagResponse) {
		this.englishTagResponse = englishTagResponse;
	}

	public EnglishGrammarCheckResponse getEnglishGrammarCheckResponse() {
		return englishGrammarCheckResponse;
	}

	public void setEnglishGrammarCheckResponse(EnglishGrammarCheckResponse englishGrammarCheckResponse) {
		this.englishGrammarCheckResponse = englishGrammarCheckResponse;
	}

	public void setTagWords(List<WordTagging> tagWords) {
		englishTagResponse.setTagWords(tagWords);
	}

	public List<WordTagging> getTagWords( ) {
		return englishTagResponse.getTagWords();
	}
}