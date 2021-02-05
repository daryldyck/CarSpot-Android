package com.gb.carspot.utils;

import androidx.appcompat.app.AppCompatDelegate;

public class Constants
{
    private final String TAG = getClass().getCanonicalName();
    public final static String SHARED_PREF_NAME = "CarSpot_Prefs";
    public final static String THEME_PREFERENCE = "themePreference";
    public final static int THEME_PREFERENCE_DEFAULT = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    public final static String LOGIN_REMEMBER_ME = "loginRememberMe";
    public final static String LOGIN_CURRENT_USER = "loginCurrentUser";
    public final static Boolean LOGIN_REMEMBER_ME_DEFAULT = false;

    public final static int PAGE_MAP = 0;
    public final static int PAGE_TICKET_HISTORY = 1;
    public final static int PAGE_TICKET_DETAILS = 2;
    public final static int PAGE_PROFILE = 3;

    public static final String ACTION_DISPLAY_BACK_BUTTON = "actionDisplayBackButton";
    public static final String ACTION_LOAD_LOGIN_PAGE = "actionLoadLoginPage";
    public static final String ACTION_LOAD_PROFILE_PAGE = "actionLoadProfilePage";
    public static final String ACTION_LOAD_MAIN_PAGE = "actionLoadMainPage";

    public static final String INITIAL_FRAGMENT_LOAD = "initialFragmentLoad";

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
}
