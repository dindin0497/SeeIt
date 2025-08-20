

package com.accessibility.seeit.asl.base;

public class WordTagging {

	private String word;
	private String tag;

	public WordTagging() {
		super();
	}

	public WordTagging(String word, String tag) {
		this.word = word;
		this.tag = tag;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}