//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.viewmodels;

import com.gb.carspot.models.ParkingTicket;

import androidx.lifecycle.ViewModel;

// viewModel for ticketDetails fragment
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
