package com.devivan.economicsupervision.UtilityClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.Objects.Designer.Works.Work;
import com.devivan.economicsupervision.R;

import java.util.ArrayList;

public class Images extends Designers {

    public static Designer by(Context context, String designer) {
        switch (designer) {
            case SRIP: return new Designer(SRIP, SRIP_URL, works(context, R.array.srip_urls, R.array.srip_resources));
            case FREEPIK: return new Designer(FREEPIK, SRIP_URL, works(context, R.array.freepik_urls, R.array.freepik_resources));
            case PIXELPERFECT: return new Designer(PIXELPERFECT, PIXELPERFECT_URL, works(context, R.array.pixelperfect_urls, R.array.pixelperfect_resources));
            case STOCKIO: return new Designer(STOCKIO, STOCKIO_URL, works(context, R.array.stockio_urls, R.array.stockio_resources));
            case THOSEICONS: return new Designer(THOSEICONS, THOSEICONS_URL, works(context, R.array.thoseicons_urls, R.array.thoseicons_resources));
            case SMASHICONS: return new Designer(SMASHICONS, SMASHICONS_URL, works(context, R.array.smashicons_urls, R.array.smashicons_resources));
            case APIEN: return new Designer(APIEN, APIEN_URL, works(context, R.array.apien_urls, R.array.apien_resources));
            case DAVEGANDY: return new Designer(DAVEGANDY, DAVEGANDY_URL, works(context, R.array.davegandy_urls, R.array.davegandy_resources));
            case KERISMAKER: return new Designer(KERISMAKER, KERISMAKER_URL, works(context, R.array.kerismaker_urls, R.array.kerismaker_resources));
            case ROUNDICONS: return new Designer(ROUNDICONS, ROUNDICONS_URL, works(context, R.array.roundicons_urls, R.array.roundicons_resources));
            case GOOGLE: return new Designer(GOOGLE, GOOGLE_URL, works(context, R.array.google_urls, R.array.google_resources));
        }
        return null;
    }

    private static ArrayList<Work> works(Context context, int urls, int resources) {
        String[] urlArr = context.getResources().getStringArray(urls);
        @SuppressLint("Recycle") TypedArray resArr = context.getResources().obtainTypedArray(resources);

        ArrayList<Work> works = null;
        if (urlArr.length == resArr.length()) {
            works = new ArrayList<>();
            for (int i = 0; i < urlArr.length; i++) works.add(new Work(resArr.getResourceId(i, -1), urlArr[i]));
        }
        return works;
    }
}
