package com.gb.carspot.utils;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatDelegate;

public class Utils
{
    private final String TAG = getClass().getCanonicalName();

    /**
     * Apply dark or light theme
     *
     * @param mode - theme to change to
     */
    public static void applyTheme(int mode)
    {
        if (AppCompatDelegate.getDefaultNightMode() != mode)
        {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    /**
     * Set haptic feedback for view
     *
     * @param view - view to enable haptic feedback on
     */
    public static void setHaptic(View view)
    {
        view.setHapticFeedbackEnabled(true);
        view.setOnTouchListener(new HapticListener());
    }

}