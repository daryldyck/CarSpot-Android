package com.gb.carspot.viewmodels;


import android.app.Application;
import android.content.SharedPreferences;

import com.gb.carspot.fragments.MapFragment;
import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.fragments.TicketHistoryFragment;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.ParkingTicketRepository;
import com.gb.carspot.repositories.UserRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.PAGE_MAP;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

public class MainActivityViewModel extends AndroidViewModel
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;

    public boolean initialized = false;
    private int currentPage = PAGE_MAP;
    private String userEmail;

    private MapFragment mapFragment;
    private TicketHistoryFragment ticketHistoryFragment;
    private ProfileFragment profileFragment;

    private ParkingTicketRepository parkingTicketRepository;
    private UserRepository userRepository;

    private MutableLiveData<User> user;
    private MutableLiveData<List<ParkingTicket>> parkingTicketList;

    public MainActivityViewModel(@NonNull Application application)
    {
        super(application);
        sharedPrefs = getApplication().getSharedPreferences(SHARED_PREF_NAME, 0);
        this.userEmail = sharedPrefs.getString(LOGIN_CURRENT_USER, "");
        init();
    }

    public void init()
    {
        if (parkingTicketList != null && user != null)
        {
            return;
        }
        userRepository = UserRepository.getInstance();
        user = userRepository.getUser(userEmail);

        parkingTicketRepository = ParkingTicketRepository.getInstance();
        parkingTicketList = parkingTicketRepository.getParkingTicketList(userEmail);
    }

    public void addParkingTicket(ParkingTicket parkingTicket)
    {
        parkingTicketRepository.addParkingTicket(userEmail, parkingTicket);
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public MapFragment getMapFragment()
    {
        return mapFragment;
    }

    public void setMapFragment(MapFragment mapFragment)
    {
        this.mapFragment = mapFragment;
    }

    public TicketHistoryFragment getTicketHistoryFragment()
    {
        return ticketHistoryFragment;
    }

    public void setTicketHistoryFragment(TicketHistoryFragment ticketHistoryFragment)
    {
        this.ticketHistoryFragment = ticketHistoryFragment;
    }

    public ProfileFragment getProfileFragment()
    {
        return profileFragment;
    }

    public void setProfileFragment(ProfileFragment profileFragment)
    {
        this.profileFragment = profileFragment;
    }

    public MutableLiveData<User> getUser()
    {
        return user;
    }

    public MutableLiveData<List<ParkingTicket>> getParkingTicketList()
    {
        return parkingTicketList;
    }
}
