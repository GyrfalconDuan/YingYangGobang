package com.example.rancho.chess.utils;
import android.content.Context;
import android.widget.Toast;
public class AlertUtils {

    /**
     * @param context
     * @param mess
     */
    public static void toastMess(Context context, String mess) {
        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
    }
}