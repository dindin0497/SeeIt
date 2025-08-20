

package com.accessibility.seeit.asl.base;

import java.util.*;

public class GrammarConfiguration {

	public static class BeWords {

		private ArrayList<String> beWords = new ArrayList<String>(
				Arrays.asList(new String[] { "IS", "ARE", "AM", "WAS", "WERE", "BEEN", "BEING", "BE", "WILL", "WOULD",
						"SHOULD", "COULD", "CAN", "HAVE", "HAD", "HAS", "SHALL", "MUST", "MAY", "MIGHT" }));

		public ArrayList<String> getBeWords() {
			return beWords;
		}
	}

	public static class VerbTags {

		private ArrayList<String> verbTags = new ArrayList<String>(
				Arrays.asList(new String[] { "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" }));
		private ArrayList<String> adverbTags = new ArrayList<String>(
				Arrays.asList(new String[] { "RB", "RBR", "RBS" }));

		public ArrayList<String> getVerbTags() {
			return verbTags;
		}

		public ArrayList<String> getAdverbTags() {
			return adverbTags;
		}
	}

	public static class NounTags {

		private ArrayList<String> nounTags = new ArrayList<String>(
				Arrays.asList(new String[] { "NN", "NNS", "NNP", "NNPS" }));
		private ArrayList<String> pluralNounTags = new ArrayList<String>(
				Arrays.asList(new String[] { "NN", "NNS", "NNPS" }));
		private ArrayList<String> adjectiveTags = new ArrayList<String>(
				Arrays.asList(new String[] { "JJ", "JJR", "JJS" }));

		public ArrayList<String> getNounTags() {
			return nounTags;
		}

		public ArrayList<String> getPluralNounTags() {
			return pluralNounTags;
		}

		public ArrayList<String> getAdjectiveTags() {
			return adjectiveTags;
		}
	}

	public static class ValidPOS {

		private ArrayList<String> validPos = new ArrayList<String>(
				Arrays.asList(new String[] { "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN",
						"NNS", "NNP", "NNPS", "PDT", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB",
						"VBD", "VBG", "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB" }));

		public ArrayList<String> getValidPos() {
			return validPos;
		}
	}

	public static class TimeWords {

		private ArrayList<String> timeWords = new ArrayList<String>(Arrays.asList(
				new String[] { "YESTERDAY", "MORNING", "AFTERNOON", "EVENING", "NIGHT", "YESTERDAY", "TOMORROW" }));

		public ArrayList<String> getTimeWords() {
			return timeWords;
		}
	}

	public static class NegationWords {

		private ArrayList<String> negationWords = new ArrayList<String>(
				Arrays.asList(new String[] { "NOT", "NONE", "NEVER", "NOBODY", "NOTHING", "NOONE", "N'T" }));

		public ArrayList<String> getNegationWords() {
			return negationWords;
		}
	}

	public static class Contractions {

		Map<String, String> contractions = new HashMap<String, String>();

		public Contractions() {
			contractions.put("aren't", "are");
			contractions.put("can't", "cannot");
			contractions.put("could've", "could have");
			contractions.put("couldn't", "could not");
			contractions.put("didn't", "did not");
			contractions.put("doesn't", "does not");
			contractions.put("don't", "do not");
			contractions.put("everybody's", "everybody is");
			contractions.put("everyone's", "everyone is");
			contractions.put("gonna", "going to");
			contractions.put("hadn't", "had not");
			contractions.put("hasn't", "has not");
			contractions.put("haven't", "have not");
			contractions.put("how'll", "how will");
			contractions.put("I'm", "I am");
			contractions.put("I've", "I have");
			contractions.put("isn't", "is not");
			contractions.put("it'd", "it would");
			contractions.put("let's", "let us");
			contractions.put("may've", "may have");
			contractions.put("might've", "might have");
			contractions.put("mustn't", "must not");
			contractions.put("must've", "must have");
			contractions.put("needn't", "need not");
			contractions.put("should've", "should have");
			contractions.put("shouldn't", "should not");
			contractions.put("they've", "they have");
			contractions.put("those've", "those have");
			contractions.put("'twas", "it was");
			contractions.put("wanna", "want to");
			contractions.put("wasn't", "was not");
			contractions.put("we're", "we are");
			contractions.put("weren't", "were not");
			contractions.put("what've", "what have");
			contractions.put("where'd", "where did");
			contractions.put("who're", "who are");
			contractions.put("who've", "who have");
			contractions.put("why'd", "why did");
			contractions.put("why're", "why are");
			contractions.put("won't", "will not");
			contractions.put("would've", "would have");
			contractions.put("wouldn't", "would not");
			contractions.put("you've", "you");
		}

		public Map<String, String> getContractions() {
			return contractions;
		}
	}
}