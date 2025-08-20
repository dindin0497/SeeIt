
package com.accessibility.seeit.asl.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import com.accessibility.seeit.asl.base.*;
import com.accessibility.seeit.asl.base.GrammarConfiguration.*;

import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;

public class ASLConversionService {

	private boolean isTimeItemFirst = false;
	private EnglishParserService englishParserService = new EnglishParserService();
	private TrainedModelParser trainedModelParser = new TrainedModelParser();
	private Contractions contractions = new Contractions();
	private VerbTags verbTags = new VerbTags();
	private NounTags nounTags = new NounTags();
	private BeWords beWords = new BeWords();
	private ValidPOS validPos = new ValidPOS();
	private TimeWords timeWords = new TimeWords();
	private NegationWords negationWords = new NegationWords();

	/**
	 * Converts english sentence {@link sentence} into a grammatically correct ASL
	 * sentence.
	 * <p>
	 * Step 1 - Replaces contractions in sentence with their original words.
	 * 
	 * Step 2 - Determines context of sentence (i.e. subject, predicate,
	 * active/inactive).
	 * 
	 * Step 3 - Brings time items first.
	 * 
	 * Step 4 - Brings past tense items first if time items are not already.
	 * 
	 * Step 5 - Pushes negative items to the end of sentence.
	 * 
	 * Step 6 - Deletes articles (determiners, prepositions, and subordinating
	 * conjunctions) from the sentence.
	 * 
	 * Step 7 - Deletes be words from the sentence.
	 * 
	 * Step 8 - Gets the 'true' form of verbs and converts any plural nouns to
	 * singular.
	 * 
	 * Step 9 - Replaces the verb-adverb and adjective-noun order.
	 * 
	 * @param sentence english sentence to convert to an ASL sentence
	 * @return ASLResponse
	 * @throws Exception
	 */
	public ASLResponse getASLSentence(Context context, String sentence) throws Exception {
		ASLResponse response = new ASLResponse();

		sentence = replaceContractions(sentence);

		EnglishTagResponse englishTagResponse = englishParserService.getParsedSentence(context, sentence, true);
		response.setEnglishTagResponse(englishTagResponse);
		response.setEnglishGrammarCheckResponse(englishTagResponse.getEnglishGrammarCheckResponse());

		List<WordTagging> tagWords = englishTagResponse.getTagWords();

		tagWords = deleteInvalidPOSCapitalize(tagWords);

		tagWords = bringTimeItemsFirst(tagWords);

		if (!isTimeItemFirst)
			tagWords = bringPastItemsFirst(tagWords);

		tagWords = pushNegationItemsLast(tagWords);

		tagWords = deleteArticles(tagWords);

		tagWords = deleteBeWords(tagWords);

		tagWords = replaceVerbWords(tagWords);
		tagWords = replacePluralNounWords(tagWords);

		tagWords = replaceVerbAdverb(tagWords);
		tagWords = replaceAdjectiveNoun(tagWords);

		String aslSentence = getASLSentence(tagWords);
		response.setSentence(aslSentence);
		response.setTagWords(tagWords);
		return response;
	}

	/**
	 * Replaces all contractions in sentence {@link sentence} with their original
	 * words.
	 * 
	 * @param sentence sentence to replace contractions of
	 * @return {@link sentence} with its contractions replaced
	 */
	private String replaceContractions(String sentence) {
		StringJoiner joiner = new StringJoiner(" ");

		List<String> words = Arrays.asList(sentence.split(" "));

		for (String word : words) {
			if (contractions.getContractions().containsKey(word))
				joiner.add(contractions.getContractions().get(word));
			else
				joiner.add(word);
		}
		return joiner.toString();
	}

	/**
	 * Deletes invalid POS tagging from {@link tagWords} and capitalizes all words.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} with invalid POS deleted and all words capitalized
	 */
	private List<WordTagging> deleteInvalidPOSCapitalize(List<WordTagging> tagWords) {
		ArrayList<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (validPos.getValidPos().stream().anyMatch(str -> str.trim().equals(wordTag.getTag()))) {
				WordTagging wordTagging = new WordTagging();
				wordTagging.setWord(wordTag.getWord().toUpperCase());
				wordTagging.setTag(wordTag.getTag());
				ret.add(wordTagging);
			}
		return ret;
	}

	/**
	 * Reorders {@link tagWords} to bring time items first.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@tagWord} reordered with time items first
	 */
	private List<WordTagging> bringTimeItemsFirst(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (timeWords.getTimeWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord()))) {
				ret.add(wordTag);
				isTimeItemFirst = true;
			}
		for (WordTagging wordTag : tagWords)
			if (!(timeWords.getTimeWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord()))))
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Reorders {@link tagWords} to bring past tense items first (if not already).
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} reordered with past tense items first
	 */
	private List<WordTagging> bringPastItemsFirst(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (timeWords.getTimeWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord()))) {
				ret.add(wordTag);
				isTimeItemFirst = true;
			}
		for (WordTagging wordTag : tagWords)
			if (!(timeWords.getTimeWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord()))))
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Reorders {@link tagWords} to push negative items last.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@tagWords} reordered with negative items last
	 */
	private List<WordTagging> pushNegationItemsLast(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (!negationWords.getNegationWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord())))
				ret.add(wordTag);
		for (WordTagging wordTag : tagWords)
			if (negationWords.getNegationWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord())))
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Deletes articles (determiners, prepositions, and subordinating conjunctions)
	 * from {@link tagWords}.
	 * 
	 * DT - determiner
	 * 
	 * IN - Preposition or subordinating conjunction
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} with articles removed
	 */
	private List<WordTagging> deleteArticles(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (!((wordTag.getTag().equals("DT"))
					|| (wordTag.getTag().equals("TO") || (wordTag.getTag().equals("IN")))))
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Deletes be words from {@link tagWords}.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} with be words removed
	 */
	private List<WordTagging> deleteBeWords(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (!(beWords.getBeWords().stream().anyMatch(str -> str.trim().equals(wordTag.getWord()))))
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Replaces verbs in {@link tagWords} with the 'true' form of these verbs.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@tagWords} with verbs replaces with their 'true' forms
	 */
	private List<WordTagging> replaceVerbWords(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (verbTags.getVerbTags().stream().anyMatch(str -> str.trim().equals(wordTag.getTag()))) {
				WordTagging newTag = new WordTagging();
				String word = getVerbWord(wordTag.getWord());
				newTag.setWord(word);
				newTag.setTag(wordTag.getTag());
				ret.add(newTag);
			} else
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Gets the 'true' form of verb {@link verb}.
	 * 
	 * @param verb verb to get true form of
	 * @return the 'true' form of verb {@link verb}
	 */
	private String getVerbWord(String verb) {
		try {
			MorphologicalProcessor morphologicalProcessor = trainedModelParser.getMorphologicalProcessor();
			IndexWord word = morphologicalProcessor.lookupBaseForm(POS.VERB, verb);
			return word.getLemma().toString().toUpperCase();
		} catch (Exception e) {
			return verb.toUpperCase();
		}
	}

	/**
	 * Converts any plural nouns to singular nouns.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@tagWords} with plural nouns replaces with singular nouns
	 */
	private List<WordTagging> replacePluralNounWords(List<WordTagging> tagWords) {
		List<WordTagging> ret = new ArrayList<>();

		for (WordTagging wordTag : tagWords)
			if (nounTags.getPluralNounTags().stream().anyMatch(str -> str.trim().equals(wordTag.getTag()))) {
				WordTagging newTag = new WordTagging();
				String word = getSingularNoun(wordTag.getWord());
				newTag.setWord(word);
				newTag.setTag(wordTag.getTag());
				ret.add(newTag);
			} else
				ret.add(wordTag);
		return ret;
	}

	/**
	 * Gets singular form of plural noun {@link noun}.
	 * 
	 * @param noun plural noun to get singular form of
	 * @return singular form of plural noun {@link noun}
	 */
	private String getSingularNoun(String noun) {
		try {
			MorphologicalProcessor morphologicalProcessor = trainedModelParser.getMorphologicalProcessor();
			IndexWord word = morphologicalProcessor.lookupBaseForm(POS.NOUN, noun);
			return word.getLemma().toString().toUpperCase();
		} catch (Exception e) {
			return noun.toUpperCase();
		}
	}

	/**
	 * Replaces verb-adverb order.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} with replaced verb-adverb order
	 */
	private List<WordTagging> replaceVerbAdverb(List<WordTagging> tagWords) {
		WordTagging[] ret = tagWords.toArray(new WordTagging[tagWords.size()]);

		for (int i = 0; i < ret.length; i++) {
			WordTagging wordTagging1 = ret[i];
			if (verbTags.getAdverbTags().stream().anyMatch(str -> str.trim().equals(wordTagging1.getTag())))
				if (i != ret.length - 1) {
					WordTagging wordTagging2 = ret[i + 1];
					if (verbTags.getVerbTags().stream().anyMatch(str -> str.trim().equals(wordTagging2.getTag()))) {
						ret[i] = wordTagging2;
						ret[i + 1] = wordTagging1;
					}
				}
		}
		return Arrays.asList(ret);
	}

	/**
	 * Replaces adjective-noun order.
	 * 
	 * @param tagWords sentence separated into words
	 * @return {@link tagWords} with replaced adjective-noun order
	 */
	private List<WordTagging> replaceAdjectiveNoun(List<WordTagging> tagWords) {
		WordTagging[] ret = tagWords.toArray(new WordTagging[tagWords.size()]);

		for (int i = 0; i < ret.length; i++) {
			WordTagging wordTagging1 = ret[i];
			if (nounTags.getAdjectiveTags().stream().anyMatch(str -> str.trim().equals(wordTagging1.getTag())))
				if (i != ret.length - 1) {
					WordTagging wordTagging2 = ret[i + 1];
					if (nounTags.getNounTags().stream().anyMatch(str -> str.trim().equals(wordTagging2.getTag()))) {
						ret[i] = wordTagging2;
						ret[i + 1] = wordTagging1;
					}
				}
		}
		return Arrays.asList(ret);
	}

	/**
	 * Combines all elements (words) of {@link tagWords} to form a sentence.
	 * 
	 * @param tagWords sentence separated into words
	 * @return elements of {@link tagWords} combine into a sentence
	 */
	private String getASLSentence(List<WordTagging> tagWords) {
		StringJoiner joiner = new StringJoiner(" ");

		for (WordTagging wordTag : tagWords)
			joiner.add(wordTag.getWord());
		return joiner.toString();
	}
}