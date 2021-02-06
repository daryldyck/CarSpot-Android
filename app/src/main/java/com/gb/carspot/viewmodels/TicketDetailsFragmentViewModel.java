package com.gb.carspot.viewmodels;

import com.gb.carspot.models.ParkingTicket;

import androidx.lifecycle.ViewModel;

public class TicketDetailsFragmentViewModel extends ViewModel
{
    private final String TAG = getClass().getCanonicalName();

    private ParkingTicket parkingTicket;

    public ParkingTicket getParkingTicket()
    {
        return parkingTicket;
    }

    public void setParkingTicket(ParkingTicket parkingTicket)
    {
        this.parkingTicket = parkingTicket;
    }
}
