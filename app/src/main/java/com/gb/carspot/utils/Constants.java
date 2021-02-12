//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

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

    public static final String EXTRA_PARKING_TICKET = "extraParkingTicket";

    public final static String LOCATION_LAT = "locationLat";
    public final static String LOCATION_LON = "locationLon";
    public final static String LOCATION_LAT_DEFAULT = "43.6532";
    public final static String LOCATION_LON_DEFAULT = "-79.3832";

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    public static final int TICKET_DEFAULT = 0;
    public static final int TICKET_ADDED = 1;
    public static final int TICKET_FAILED = 2;

    // Database Stuff
    public static final String COLLECTION_USERS = "collectionUsers";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_LAST_NAME = "lastName";
    public static final String FIELD_LICENSE_PLATES = "licensePlates";
    public static final String FIELD_PARKING_TICKETS = "parkingTickets";

    public static final String COLLECTION_PARKING_TICKETS = "collectionParkingTickets";
    public static final String FIELD_BUILDING_CODE = "buildingCode";
    public static final String FIELD_NO_OF_HOURS = "noOfHours";
    public static final String FIELD_LICENSE_PLATE = "licensePlate";
    public static final String FIELD_HOST_SUITE = "hostSuite";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_IMAGE_URL = "imageUrl";

}
