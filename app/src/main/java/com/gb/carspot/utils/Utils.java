package com.gb.carspot.utils;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.gb.carspot.R;
import com.google.android.material.textfield.TextInputLayout;

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

    // clear error on textView
    public synchronized static void clearError(TextInputLayout textInputLayout)
    {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    // set error on editText
    public synchronized static void setEditTextError(TextInputLayout textInputLayout, String errorString)
    {
        textInputLayout.setError(errorString);
        textInputLayout.setErrorEnabled(true);
    }

    public static boolean checkBuildingCode(TextInputLayout textInputLayout, String errorString)
    {
        if (textInputLayout.getEditText().getText().toString().length() != 5)
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    public static boolean checkSuiteNumber(TextInputLayout textInputLayout, String errorString)
    {
        String stringToCheck = textInputLayout.getEditText().getText().toString();

        if (stringToCheck.isEmpty() || stringToCheck.length() < 2 || stringToCheck.length() > 5)
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    public static boolean checkStreetAddress(TextInputLayout textInputLayout, String errorString)
    {
        String stringToCheck = textInputLayout.getEditText().getText().toString();

        if (stringToCheck.isEmpty() || stringToCheck.length() < 3)
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }
}