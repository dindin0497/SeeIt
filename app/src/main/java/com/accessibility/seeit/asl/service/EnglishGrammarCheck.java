
package com.accessibility.seeit.asl.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.accessibility.seeit.asl.base.EnglishGrammarCheckResponse;
import com.accessibility.seeit.asl.net.*;

import retrofit2.Response;

/**
 * Retrofit-based LanguageTool checker.
 * Replaces local JLanguageTool with HTTP calls.
 */
public class EnglishGrammarCheck {

	// Configurable:
	private final String language;   // e.g. "en-US" or "auto"
	private final String level;      // "default" or "picky"
	private final boolean enabledOnly;

	private final LanguageToolApi api;

	/** Default to public LT endpoint (rate limited). */
	public EnglishGrammarCheck() {
		this("https://api.languagetool.org", null, "en-US", "default", false);
	}

	/**
	 * @param baseUrl  e.g. "https://api.languagetool.org" or self-host "http://10.0.2.2:8081"
	 * @param apiKey   pass key for paid cloud (nullable)
	 * @param language "en-US" or "auto"
	 * @param level    "default" or "picky"
	 * @param enabledOnly if true, only enabled rules
	 */
	public EnglishGrammarCheck(String baseUrl, String apiKey,
							   String language, String level, boolean enabledOnly) {
		this.api = new LanguageToolClient(baseUrl, apiKey).api();
		this.language = language != null ? language : "en-US";
		this.level = level != null ? level : "default";
		this.enabledOnly = enabledOnly;
	}

	/**
	 * Checks the grammar of sentence {@link sentence}.
	 *
	 * @param sentence sentence to check grammar of
	 * @return EnglishGrammarCheckResponse
	 * @throws Exception on network/server errors
	 */
	public EnglishGrammarCheckResponse checkGrammar(String sentence) throws Exception {
		if (sentence == null)
			sentence = "";

		Response<CheckResponse> resp = api.check(
				sentence,
				language,
				level,
				enabledOnly
		).execute();

		if (!resp.isSuccessful()) {
			String err = (resp.errorBody() != null) ? resp.errorBody().string() : "";
			throw new IOException("LanguageTool HTTP " + resp.code() + " " + err);
		}

		CheckResponse body = resp.body();
		EnglishGrammarCheckResponse ret = new EnglishGrammarCheckResponse();

		// Suggestion map: "from-to" -> message (same as your original class)
		Map<String, String> suggestions = new HashMap<>();

		StringBuilder sb = new StringBuilder(sentence);
		int difference = 0;

		if (body != null && body.matches != null) {
			for (CheckResponse.Match m : body.matches) {
				int from = m.offset;
				int to = m.offset + m.length;
				if (from < 0 || to < from) continue;

				// record message
				suggestions.put(from + "-" + to, m.message != null ? m.message : "");

				// first replacement (if any)
				String replacement = null;
				if (m.replacements != null && !m.replacements.isEmpty() && m.replacements.get(0) != null) {
					replacement = m.replacements.get(0).value;
				}

				if (replacement != null) {
					int adjFrom = from + difference;
					int adjTo = to + difference;
					if (adjFrom >= 0 && adjTo <= sb.length() && adjFrom <= adjTo) {
						sb.replace(adjFrom, adjTo, replacement);
						difference += (replacement.length() - (to - from));
					}
				}
			}
		}

		ret.setSentence(sb.toString());
		ret.setSuggestions(suggestions);
		return ret;
	}
}
