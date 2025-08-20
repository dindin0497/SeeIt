package com.accessibility.seeit.asl.net;

import java.util.List;

public class CheckResponse {
    public List<Match> matches;

    public static class Match {
        public int offset;
        public int length;
        public String message;
        public List<Replacement> replacements;
    }

    public static class Replacement {
        public String value;
    }
}