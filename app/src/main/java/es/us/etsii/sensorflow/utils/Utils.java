package es.us.etsii.sensorflow.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import es.us.etsii.sensorflow.R;

public abstract class Utils {

    /**
     * Converter and formatter from seconds to hours, minutes and seconds.
     *
     * @param totalSeconds The number of seconds to convert and format
     * @return Formatted CharSequence with custom sizes, colors and content
     */
    public static CharSequence getCustomTime(Context context, double totalSeconds){

        // Convert totalSeconds to hours:minutes:seconds            //22304s    example
        int totalMinutes = (int) Math.floor(totalSeconds/60);
        int hours = (int) Math.floor(totalMinutes/60.0);            //6h        21600s
        int minutes = totalMinutes%60;                              //11m       660s
        int seconds = (int) Math.floor(totalSeconds)%60;            //44s       44s

        String adjustMin = minutes<10 && hours>0 ? "0" : "";
        String adjustSec = seconds<10 && (hours>0 || minutes>0) ? "0" : "";

        // Only show those units with relevant information
        List<String> items = new ArrayList<>();
        if(hours > 0) {
            items.add(String.valueOf(hours));
            items.add(context.getString(R.string.hour_unit));
        }
        if(minutes > 0){
            items.add(adjustMin + String.valueOf(minutes));
            items.add(context.getString(R.string.minute_unit));
        }
        if(seconds > 0 || (minutes == 0 && hours == 0)){
            items.add(adjustSec + String.valueOf(seconds));
            items.add(context.getString(R.string.second_unit));
        }

        return buildCustomTimeString(context, items);
    }

    private static CharSequence buildCustomTimeString(Context context, List<String> items){
        CharSequence finalText = "";

        // Concatenate all the items to form the hour, color and resize depending on the item
        Resources resources = context.getResources();
        int timeSize = resources.getDimensionPixelSize(R.dimen.time_size);
        int unitSize = resources.getDimensionPixelSize(R.dimen.unit_size);
        for (int i = 0; i < items.size(); i++) {

            int textSize, textColor;
            if (i==0 || i%2 ==0){
                textSize = timeSize;
                textColor = Color.BLACK;
            } else {
                textSize = unitSize;
                textColor = Color.GRAY;
            }

            String item = items.get(i);
            SpannableString span = new SpannableString(item);
            span.setSpan(new AbsoluteSizeSpan(textSize), 0, item.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            span.setSpan(new ForegroundColorSpan(textColor), 0, item.length(), 0);

            finalText = TextUtils.concat(finalText, span);
        }

        return finalText;
    }

    public static long getDayStart(){
        // Get the current day/time and restart the values to 0h 0m 0s 0ms
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        return todayCal.getTimeInMillis();
    }

    public static String longToDateString(Context context, long date){
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
    }

    /**
     * Check whether the device is connected to the internet or not
     *
     * @param activity The activity you wish to check from
     * @return true if the device is connected, false otherwise
     */
    public static boolean isNetworkAvailable(AppCompatActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        for (String permission : permissions)
            if(context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public static void requestPermission(AppCompatActivity activity, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permissions) {
            if (!activity.shouldShowRequestPermissionRationale(permission))
                continue;
            Toast.makeText(activity, R.string.permissions_required, Toast.LENGTH_LONG).show();
            break;
        }
        activity.requestPermissions(permissions, requestCode);
    }
}
