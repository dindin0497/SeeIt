
package com.accessibility.seeit.asl.service;

import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;


public class TrainedModelParser {

	/**
	 * Gets MorphologicalProcessor for verb manipulation.
	 * 
	 * @return MorphologicalProcessor
	 */
	public MorphologicalProcessor getMorphologicalProcessor() {
		try {
			Dictionary wordnet = Dictionary.getDefaultResourceInstance();
			return wordnet.getMorphologicalProcessor();
		} catch (Exception e) {
			return null;
		}
	}
}