/**
 * The following code was adapted from:
 * Author: Harsh Bhavsar
 * Date: February 24, 2020
 * Availability: https://github.com/harshbits/english-asl-algorithm
 */
package com.accessibility.seeit.asl.base;

import java.util.Map;

public class EnglishGrammarCheckResponse {

	private String sentence;
	private Map<String, String> suggestions;

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public Map<String, String> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(Map<String, String> inSuggestions) {
		this.suggestions = inSuggestions;
	}
}