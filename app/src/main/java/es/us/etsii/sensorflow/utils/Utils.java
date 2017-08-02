package es.us.etsii.sensorflow.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import es.us.etsii.sensorflow.R;

public class Utils {

    /**
     * Converter and formatter from seconds to hours, minutes and seconds.
     *
     * @param totalSeconds The number of seconds to convert and format
     * @return Formatted CharSequence with custom sizes, colors and content
     */
    public static CharSequence getCustomHHmmssString(Context context, double totalSeconds){

        // Convert totalSeconds to hours:minutes:seconds            //22304s
        int totalMinutes = (int) Math.floor(totalSeconds/60);
        int hours = (int) Math.floor(totalMinutes/60.0);            //6h        21600s
        int minutes = totalMinutes%60;                              //11m       660s
        int seconds = (int) Math.floor(totalSeconds)%60;            //44s       44s

        String adjustMin = minutes<10 ? "0" : "", adjustSec = seconds<10 ? "0" : "";

        String[] items = {String.valueOf(hours), context.getString(R.string.hour_unit),
                adjustMin + String.valueOf(minutes), context.getString(R.string.minute_unit),
                adjustSec + String.valueOf(seconds), context.getString(R.string.second_unit)};

        return buildCustomTimeString(context, items);
    }

    /**
     * Converter and formatter from minutes to hours and minutes.
     *
     * @param totalMinutes The number of minutes we wish to convert to HH:mm
     * @return Formatted CharSequence with custom sizes, colors and content
     */
    public static CharSequence getCustomHHmmString(Context context, int totalMinutes){
        int hours = (int) Math.floor(totalMinutes/60.0);
        int minutes = totalMinutes%60;
        String adjust = minutes<10 ? "0" : "";

        String[] items = {String.valueOf(hours), context.getString(R.string.hour_unit),
                adjust + String.valueOf(minutes), context.getString(R.string.minute_unit)};

        return buildCustomTimeString(context, items);
    }

    private static CharSequence buildCustomTimeString(Context context, String[] items){
        CharSequence finalText = "";

        // Concatenate all the items to form the hour, color and resize depending on the item
        Resources resources = context.getResources();
        int timeSize = resources.getDimensionPixelSize(R.dimen.time_size);
        int unitSize = resources.getDimensionPixelSize(R.dimen.unit_size);
        for (int i = 0; i < items.length; i++) {

            int textSize, textColor;
            if (i==0 || i%2 ==0){
                textSize = timeSize;
                textColor = Color.BLACK;
            } else {
                textSize = unitSize;
                textColor = Color.GRAY;
            }

            String item = items[i];
            SpannableString span = new SpannableString(item);
            span.setSpan(new AbsoluteSizeSpan(textSize), 0, item.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            span.setSpan(new ForegroundColorSpan(textColor), 0, item.length(), 0);

            finalText = TextUtils.concat(finalText, span);
        }
        return finalText;
    }
}
