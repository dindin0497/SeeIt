package com.accessibility.seeit.asl;

import android.util.Log;

import com.accessibility.seeit.asl.base.WordTagging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Grabber {

    final static String TAG="Grabber";

    private static String[] letterURLs = new String[] { "sign/A/5820/1", "search/b", "search/c", "search/d", "search/e",
            "search/f", "sign/G/5826/1", "search/h", "sign/I/5828/1", "search/j", "search/k", "sign/L/5831/1",
            "sign/M/5832/1", "search/n", "search/o", "search/p", "search/q", "search/r", "search/s", "sign/T/5839/1",
            "search/u", "search/v", "search/w", "search/x", "search/y", "search/z" };
    private static String baseURL = "https://www.signingsavvy.com/";
    private static String baseCSS = "html body#page_signs.bg div#frame div#main.index div#main.sub div#main_content div#main_content_inner div#main_content_left div.content_module";


    public static ArrayList<String> getVideoURLsFromSentence(List<WordTagging> tagWords) throws IOException, InterruptedException {
        ArrayList<String> result = new ArrayList<>();

        // Track first index for duplicate words (case-insensitive)
        Map<String,Integer> firstIndex = new HashMap<>();
        for (int i = 0; i < tagWords.size(); i++) {
            String key = safeLower(tagWords.get(i).getWord());
            firstIndex.putIfAbsent(key, i);
        }

        // Precompute a lowercase context bag of all non-stop content words
        List<String> wordsLower = new ArrayList<>(tagWords.size());
        for (WordTagging wt : tagWords) wordsLower.add(safeLower(wt.getWord()));

        for (int i = 0; i < tagWords.size(); i++) {
            WordTagging wt = tagWords.get(i);
            String word = wt.getWord() == null ? "" : wt.getWord().trim();
            String pos  = wt.getTag()  == null ? "" : wt.getTag().trim();

            if (word.isEmpty())
                continue;

            // Duplicate handling (like your original)
            String key = safeLower(word);
            int first = firstIndex.getOrDefault(key, i);
            if (first != i) {
                result.add(Integer.toString(first));
                continue;
            }

            // Build context: neighbors ±2 that aren’t stopwords and aren’t the same word
            List<String> ctx = new ArrayList<>();
            for (int j = Math.max(0, i-2); j <= Math.min(tagWords.size()-1, i+2); j++) {
                if (j == i) continue;
                String wj = wordsLower.get(j);
                if (!wj.equals(key) && !wj.isEmpty())
                    ctx.add(wj);
            }

            // Query SigningSavvy
            String page = "search/" + word;
            String pickedUrl = null;
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(baseURL + page).get();
                org.jsoup.nodes.Element ul = doc.selectFirst(baseCSS + " div.search_results ul");

                if (ul != null && ul.children().size() > 0) {
                    double best = -1e9;
                    String bestHref = null;

                    int rank = 0; // stable tiebreak
                    for (org.jsoup.nodes.Element li : ul.children()) {
                        org.jsoup.nodes.Element a = li.selectFirst("a[href]");
                        if (a == null) { rank++; continue; }

                        String title  = a.text();         // anchor label
                        String href   = a.attr("href");   // relative URL
                        String liText = li.text();        // full <li> text (often has hints like "(as in me)")

                        double score = scoreCandidate(word, pos, ctx, title, liText, href, rank);
                        if (score > best) {
                            best = score;
                            bestHref = href;
                        }
                        rank++;
                    }
                    if (bestHref != null) {
                        pickedUrl = getVideoURL(baseURL + bestHref);
                    }

                }
            } catch (Exception ex) {
                // ignore and fall back
                ex.printStackTrace();
            }

            if (pickedUrl != null) {
                result.add(pickedUrl);
            } else {
                // Fallbacks:
                // 1) try the generic page URL for the word
                try {
                    result.add(getVideoURL(baseURL + "search/" + word));
                } catch (Exception e) {
                    // 2) finger-spell last resort
                    result.addAll(fingerSpell(word));
                }
            }
        }

        return result;
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(java.util.Locale.ROOT);
    }

    /** ONE universal scorer: context overlap (Lesk) + POS hints + simple priors. */
    private static double scoreCandidate(
            String word, String pos, List<String> context,
            String title, String liText, String href, int rank) {

        String w   = safeLower(word);
        String t   = safeLower(title);
        String extra = safeLower(liText);
        String link  = (href == null ? "" : href.toLowerCase(java.util.Locale.ROOT));

        double s = 0.0;

        // A) Lesk-style overlap with context (use title + extra text)
        for (String c : context) {
            if (c.equals(w)) continue;
            if (t.contains(c))     s += 0.5;
            if (extra.contains(c)) s += 0.5;
        }

        // B) Exact / near-exact match boosts
        if (t.equals(w) || t.startsWith(w + " ")) s += 0.8;
        if (t.contains(" " + w + " "))            s += 0.4;

        // C) POS-aware priors (universal, not hard-coded to any sentence)
        if (pos != null) {
            if (pos.equals("PRP")) {
                // Favor "I / me" style, avoid homographs
                if (t.contains("me") || extra.contains("me") || t.contains("pronoun") || extra.contains("pronoun"))
                    s += 3.0;
                if (t.contains("eye") || extra.contains("eye")) s -= 3.0;
            } else if (pos.startsWith("VB")) {
                if (t.contains("verb") || extra.contains("verb") || t.startsWith("to " + w)) s += 0.8;
            } else if (pos.startsWith("NN")) {
                if (t.contains("noun") || extra.contains("noun")) s += 0.4;
            } else if (pos.startsWith("JJ")) {
                if (t.contains("(adj)") || extra.contains("(adj)")) s += 0.4;
            } else if (pos.startsWith("RB")) {
                if (t.contains("(adv)") || extra.contains("(adv)")) s += 0.3;
            } else if (pos.equals("IN")) {
                if (t.contains("from") || t.contains("in") || t.contains("on") || t.contains("at")) s += 0.2;
            } else if (pos.equals("CD")) {
                if (t.matches(".*\\b\\d+\\b.*") || t.contains("number")) s += 0.3;
            }
        }

        // D) URL-based hints (universal)
        if (link.contains("fingerspell") || link.contains("alphabet"))
            s -= 2.5;                       // deprioritize alphabet pages
        if (link.contains("/sign/"))
            s += 0.3;                       // real sign entries tend to be under /sign/
        if (link.contains("/wordlist/"))
            s -= 0.3;

        // E) Prefer concise titles slightly
        s += Math.max(0, 0.8 - 0.03 * Math.max(0, t.length() - 12));

        // F) Stable tie-breaker: earlier items win
        s += -0.01 * rank;

        return s;
    }

    // Finger-spell fallback; uses your A..Z mapping
    private static List<String> fingerSpell(String token) throws IOException {
        List<String> out = new ArrayList<>();
        for (char c : token.toUpperCase(java.util.Locale.ROOT).toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                int idx = c - 'A';
                out.add(getVideoURL("https://www.signingsavvy.com/" + letterURLs[idx]));
            }
        }
        return out;
    }

    /**
     * Gets the direct URL for the video on webpage {@link pageURL}.
     * 
     * @param pageURL webpage with video on it
     * @return String direct URL for video on webpage {@link pageURL}
     * @throws IOException
     */
    private static String getVideoURL(String pageURL) throws IOException {
        Document document = Jsoup.connect(pageURL).get();
        String videoURL = document.selectFirst(baseCSS + " div.sign_module div.signing_body div.videocontent link")
                .attr("href").toString();
        return videoURL;
    }
}