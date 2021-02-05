package com.gb.carspot.viewmodels;


import com.gb.carspot.fragments.MapFragment;
import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.fragments.TicketHistoryFragment;

import androidx.lifecycle.ViewModel;

import static com.gb.carspot.utils.Constants.PAGE_MAP;

public class MainActivityViewModel extends ViewModel
{
    private final String TAG = getClass().getCanonicalName();

    public boolean initialized = false;
    private int currentPage = PAGE_MAP;

    private MapFragment mapFragment;
    private TicketHistoryFragment ticketHistoryFragment;
    private ProfileFragment profileFragment;

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

}
