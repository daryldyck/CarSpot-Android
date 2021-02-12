//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.activities.LoginActivity;
import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.fragments.MapFragment;
import com.gb.carspot.fragments.TicketHistoryFragment;
import com.gb.carspot.models.User;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.gb.carspot.utils.Constants.*;

// the app architecture was setup prior to learning navigation components
public class MainActivity extends AppCompatActivity
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    private MainActivityViewModel viewModel;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //Get FirebaseAuth for logging out
        mAuth = FirebaseAuth.getInstance();

        // needed to ensure vector drawable compatibility
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setup();

        if (!viewModel.isInitialized())
        {
            loadPage(viewModel.getCurrentPage());
            viewModel.setInitialized(true);
        }
    }

    // initialize views
    private void setup()
    {
        toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            toolbar.setTransitionName("toolbar");
        }
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // setup actions for nav buttons
        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.action_map:
                        loadMapPage();
                        break;
                    case R.id.action_ticket_history:
                        loadTicketHistoryPage();
                        break;
                    case R.id.action_profile:
                        loadProfilePage();
                        break;
                }
                return true;
            }
        });

        // get user object when loading complete
        viewModel.getUser().observe(this, new Observer<User>()
        {
            @Override
            public void onChanged(User user)
            {
                Log.d(TAG, "User onChanged");
                if (user != null)
                {
                    Log.d(TAG, "User: " + user.getEmail());
                }
            }
        });
    }

    @Override // create toolbar menu
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override // setup actions for overflow menu
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                returnToTicketHistory();
                return true;
            case R.id.theme_light:
                prefEditor.putInt(THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_NO).commit();
                Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
                return true;
            case R.id.theme_dark:
                prefEditor.putInt(THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_YES).commit();
                Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
                return true;
            case R.id.theme_default:
                prefEditor.putInt(THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).commit();
                Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
                return true;
            case R.id.action_about:
                displayAboutDialog();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Load fragment selector
     *
     * @param pageToLoad - fragment page you would like to load
     */
    private void loadPage(int pageToLoad)
    {
        Log.d(TAG, "loadPage: ");
        switch (pageToLoad)
        {
            case PAGE_MAP:
                loadMapPage();
                break;
            case PAGE_TICKET_HISTORY:
                loadTicketHistoryPage();
                break;
            case PAGE_PROFILE:
                loadProfilePage();
                break;
        }
    }

    // load Home fragment
    private void loadMapPage()
    {
        if (viewModel.getMapFragment() == null)
        {
            viewModel.setMapFragment(MapFragment.newInstance(viewModel));
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().getFragments().size() == 0)
        {
            transaction.add(R.id.main_container, viewModel.getMapFragment()).commit();
        }
        else
        {
            transaction.replace(R.id.main_container, viewModel.getMapFragment()).commit();
        }
        viewModel.setCurrentPage(PAGE_MAP);
        bottomNavigationView.getMenu().findItem(R.id.action_map).setChecked(true);
        resetBackButton();
    }

    // load TicketHistory fragment
    private void loadTicketHistoryPage()
    {
        if (viewModel.getTicketHistoryFragment() == null)
        {
            viewModel.setTicketHistoryFragment(TicketHistoryFragment.newInstance(viewModel));
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, viewModel.getTicketHistoryFragment()).commit();
        viewModel.setCurrentPage(PAGE_TICKET_HISTORY);
        bottomNavigationView.getMenu().findItem(R.id.action_ticket_history).setChecked(true);
        resetBackButton();
    }

    // load Profile fragment
    private void loadProfilePage()
    {
        if (viewModel.getProfileFragment() == null)
        {
            viewModel.setProfileFragment(ProfileFragment.newInstance());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, viewModel.getProfileFragment()).commit();
        viewModel.setCurrentPage(PAGE_PROFILE);
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
        resetBackButton();
    }

    // logout of main activity
    private void logout()
    {
        prefEditor.putBoolean(LOGIN_REMEMBER_ME, false).commit();
        prefEditor.putString(LOGIN_CURRENT_USER, null).commit();

        //Sign out current firebase user
        mAuth.signOut();

        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    // display about dialog
    private void displayAboutDialog()
    {
        final Dialog aboutDialog = new Dialog(MainActivity.this, R.style.AlertDialogFullScreen);
        aboutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        aboutDialog.setContentView(R.layout.dialog_about);
        aboutDialog.setCancelable(true);

        TextView versionTextView = aboutDialog.findViewById(R.id.aboutVersion_textView);
        try
        {
            // load current version number
            versionTextView.setText(
                    getString(R.string.version_) + " " +
                            getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        final MaterialButton closeButton = aboutDialog.findViewById(R.id.close_button);
        Utils.setHaptic(closeButton);
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                aboutDialog.dismiss();
            }
        });

        aboutDialog.show();
    }

    /**
     * Called only when ticket details page is open
     * to go back to list of tickets
     */
    private void returnToTicketHistory()
    {
        super.onBackPressed();
        resetBackButton();
        viewModel.setCurrentPage(PAGE_TICKET_HISTORY);
    }

    // Turn back button off
    private void resetBackButton()
    {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    /**
     * @param currentPage - set reference for current fragment loaded
     */
    public void setCurrentPage(int currentPage)
    {
        viewModel.setCurrentPage(currentPage);
    }

    public MainActivityViewModel getViewModel()
    {
        return viewModel;
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction() != null)
        {
            switch (intent.getAction())
            {
                // display back button in toolbar
                case ACTION_DISPLAY_BACK_BUTTON:
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    break;
            }
        }
    }

    @Override // back affordance
    public void onBackPressed()
    {
        if (viewModel.getCurrentPage() == PAGE_TICKET_DETAILS)
        {
            returnToTicketHistory();
        }
        else if (viewModel.getCurrentPage() != PAGE_MAP)
        {
            loadMapPage();
        }
        else if (viewModel.getCurrentPage() == PAGE_MAP)
        {
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }
}