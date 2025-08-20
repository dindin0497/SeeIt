package com.accessibility.seeit.asl.service;

import android.content.Context;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.IOException;
import java.io.InputStream;

public class EnglishPOSTagger {
    private static POSTaggerME posTagger=null;

    public static void init(Context context) throws Exception {
        if (posTagger==null) {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            try (InputStream modelIn = context.getAssets().open("en-pos-maxent.bin")) {
                if (modelIn == null)
                    throw new Exception("null bin");
                POSModel model = new POSModel(modelIn);
                posTagger = new POSTaggerME(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] tag(String[] words) {
        return posTagger.tag(words);
    }
}