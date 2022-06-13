package com.devivan.economicsupervision.UtilityClasses;

import android.content.Context;

import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.Objects.Designer.Works.Work;
import com.devivan.economicsupervision.R;

import java.util.ArrayList;

public class Animations extends Designers {

    public static Designer by(Context context, String designer) {
        switch (designer) {
            case VIK4GRAPHIC: return new Designer(VIK4GRAPHIC, VIK4GRAPHIC_URL, works(context, R.array.vik4graphic_urls, R.array.vik4graphic_resources));
            case LOTTIEFILEZ: return new Designer(LOTTIEFILES, LOTTIEFILEZ_URL, works(context, R.array.lottiefilez_urls, R.array.lottiefilez_resources));
            case SAMYMENAY: return new Designer(SAMYMENAY, SAMYMENAY_URL, works(context, R.array.samymenai_urls, R.array.samymenai_resources));
            case LOTTIEFILES: return new Designer(LOTTIEFILES, LOTTIEFILES_URL, works(context, R.array.LottieFiles_urls, R.array.LottieFiles_resources));
            case V3UT3N7A2O: return new Designer(V3UT3N7A2O, V3UT3N7A2O_URL, works(context, R.array.v3ut3n7a2o_urls, R.array.v3ut3n7a2o_resources));
            case KOBRO: return new Designer(KOBRO, KOBRO_URL, works(context, R.array.kobro_urls, R.array.kobro_resources));
            case EMCKEE: return new Designer(EMCKEE, EMCKEE_URL, works(context, R.array.emckee_urls, R.array.emckee_resources));
            case COLORSTREAK: return new Designer(COLORSTREAK, COLORSTREAK_URL, works(context, R.array.colorstreak_urls, R.array.colorstreak_resources));
            case USER90710: return new Designer(USER90710, USER90710_URL, works(context, R.array.user90710_urls, R.array.user90710_resources));
            case USER762847: return new Designer(USER762847, USER762847_URL, works(context, R.array.user762847_urls, R.array.user762847_resources));
            case TUUA9XVVX2: return new Designer(TUUA9XVVX2, TUUA9XVVX2_URL, works(context, R.array.tuua9xvvx2_urls, R.array.tuua9xvvx2_resources));
            case SPLASHANIMATION: return new Designer(SPLASHANIMATION, SPLASHANIMATION_URL, works(context, R.array.splashanimation_urls, R.array.splashanimation_resources));
            case NIKHITA: return new Designer(NIKHITA, NIKHITA_URL, works(context, R.array.nikhita_urls, R.array.nikhita_resources));
            case TANJIL: return new Designer(TANJIL, TANJIL_URL, works(context, R.array.tanjil_urls, R.array.tanjil_resources));
        }
        return null;
    }

    private static ArrayList<Work> works(Context context, int urls, int resources) {
        String[] urlArr = context.getResources().getStringArray(urls);
        String[] resArr = context.getResources().getStringArray(resources);

        ArrayList<Work> works = null;
        if (urlArr.length == resArr.length) {
            works = new ArrayList<>();
            for (int i = 0; i < urlArr.length; i++) works.add(new Work(resArr[i], urlArr[i]));
        }
        return works;
    }
}
