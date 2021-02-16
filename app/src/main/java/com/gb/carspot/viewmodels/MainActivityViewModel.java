//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.viewmodels;


import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.gb.carspot.fragments.ManagePlatesFragment;
import com.gb.carspot.fragments.MapFragment;
import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.fragments.TicketHistoryFragment;
import com.gb.carspot.models.LicensePlate;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.ParkingTicketRepository;
import com.gb.carspot.repositories.UserRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.PAGE_MAP;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

// viewModel for mainActivity - bound to activity life cycle. All fragments inside activity have access to this viewModel
public class MainActivityViewModel extends AndroidViewModel
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;

    public boolean initialized = false;
    private int currentPage = PAGE_MAP;
    private String userId;

    private MapFragment mapFragment;
    private TicketHistoryFragment ticketHistoryFragment;
    private ProfileFragment profileFragment;
    private ManagePlatesFragment managePlatesFragment;

    private ParkingTicketRepository parkingTicketRepository;
    private UserRepository userRepository;

    private MutableLiveData<User> user;
    private MutableLiveData<List<ParkingTicket>> parkingTicketList;
    private MutableLiveData<Integer> ticketAdded;

    public MainActivityViewModel(@NonNull Application application)
    {
        super(application);
        sharedPrefs = getApplication().getSharedPreferences(SHARED_PREF_NAME, 0);
        this.userId = sharedPrefs.getString(LOGIN_CURRENT_USER, "");
        init();
    }

    // initialize vieModel
    public void init()
    {
        if (parkingTicketList != null && user != null)
        {
            return;
        }
        userRepository = UserRepository.getInstance();
        user = userRepository.getUser(userId);

        parkingTicketRepository = ParkingTicketRepository.getInstance();
        parkingTicketList = parkingTicketRepository.getParkingTicketList(userId);
        ticketAdded = parkingTicketRepository.getTicketAdded();
    }

    public void addParkingTicket(ParkingTicket parkingTicket)
    {
        parkingTicketRepository.addParkingTicket(userId, parkingTicket);
    }

    public void saveParkingTicket(ParkingTicket parkingTicket)
    {
        parkingTicketRepository.saveParkingTicket(userId, parkingTicket);
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

    public void setManagePlatesFragment(ManagePlatesFragment managePlatesFragment) {
        this.managePlatesFragment = managePlatesFragment;
    }

    public void createAccount(String uid, User newUser)
    {
        userRepository.addUser(uid, newUser);
    }

    public void updateUserPlates(String uid, List<String> newPlateList) {
        userRepository.updateLicensePlates(uid, newPlateList);
    }

    public void updateUserField(String uid, String field, String newValue) {
        userRepository.updateUserInfo(uid, field, newValue);
    }

    public void deleteUser(String uid) {
        userRepository.deleteUser(uid);
    }

    public ManagePlatesFragment getManagePlatesFragment() { return managePlatesFragment; }

    public MutableLiveData<User> getUser()
    {
        return user;
    }

    public MutableLiveData<List<ParkingTicket>> getParkingTicketList()
    {
        return parkingTicketList;
    }

    public MutableLiveData<Integer> getTicketAdded()
    {
        return ticketAdded;
    }

}
