//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.utils;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.gb.carspot.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

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

    // validate building code input
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

    // validate suite number input
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

    // validate street address input
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

    // validate name input
    public static boolean checkName(TextInputLayout textInputLayout, String errorString)
    {
        if (textInputLayout.getEditText().getText().toString().isEmpty())
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    // validate email input
    public static boolean checkEmail(TextInputLayout textInputLayout, String errorString)
    {
        //Regular expression for email address
        String regex = "(.+)@(.+)\\.([a-zA-Z]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textInputLayout.getEditText().getText().toString());
        if (textInputLayout.getEditText().getText().toString().isEmpty() || !matcher.matches())
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    // validate phone input
    public static boolean checkPhone(TextInputLayout textInputLayout, String errorString)
    {
        //Regular expression for phone number
        String regex = "^[0-9]{10}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textInputLayout.getEditText().getText().toString());
        if (textInputLayout.getEditText().getText().toString().isEmpty() || !matcher.matches())
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    // validate password input
    public static boolean checkPassword(TextInputLayout textInputLayout, String errorString)
    {
        //Regular expression for phone number
        if (textInputLayout.getEditText().getText().toString().isEmpty() ||
                textInputLayout.getEditText().getText().toString().length() < 6)
        {
            setEditTextError(textInputLayout, errorString);
            return false;
        }

        clearError(textInputLayout);
        return true;
    }

    // validate password input
    public static boolean checkConfirm(TextInputLayout passwordInputLayout, TextInputLayout confirmInputLayout, String errorString)
    {

        String chosenPassword = passwordInputLayout.getEditText().getText().toString();
        String confirmPassword = confirmInputLayout.getEditText().getText().toString();

        //Regular expression for phone number
        if (confirmPassword.isEmpty() ||
                confirmPassword.length() < 6 ||
                !chosenPassword.equals(confirmPassword))
        {
            setEditTextError(confirmInputLayout, errorString);
            return false;
        }

        clearError(confirmInputLayout);
        return true;
    }

    // get icon for map markers
    public static synchronized BitmapDescriptor getBitmapDescriptor(Context context, int id)
    {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, (int) (canvas.getWidth() * 0.6f), (int) (canvas.getHeight() * 0.6f));
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // get map center point offset based on device orientation
    public static Double getMapOffset(Context context)
    {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            return 0.0;
        }
        else
        {
            return 0.002;
        }
    }

    public void clearFieldError(TextInputLayout textInputLayout) {
        clearError(textInputLayout);
    }
}