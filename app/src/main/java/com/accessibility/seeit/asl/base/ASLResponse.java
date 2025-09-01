
package com.accessibility.seeit.asl.base;

import java.util.List;

public class ASLResponse {

	private String sentence;
	private EnglishTagResponse englishTagResponse;
	private EnglishGrammarCheckResponse englishGrammarCheckResponse;



	public void setSentence(String sentence) {
		this.sentence = sentence;
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