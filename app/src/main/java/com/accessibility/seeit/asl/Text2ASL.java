package com.accessibility.seeit.asl;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.accessibility.seeit.asl.base.ASLResponse;
import com.accessibility.seeit.asl.service.ASLConversionService;

public class Text2ASL {

	static final String TAG = "Text2ASL";

	public static List<String> getUrl(Context context, String sentence) {
		Log.d(TAG,"getUrl "+sentence);

		List<String> lists = new ArrayList<>();
		try {

			ASLConversionService alsConversionService = new ASLConversionService();
			ASLResponse result = alsConversionService.getASLSentence(context, sentence);

			lists = Grabber.getVideoURLsFromSentence(result.getTagWords());
		} catch (Exception e) {
			Log.e(TAG,"getUrl "+e);
			e.printStackTrace();
		}
		return lists;

	}
}