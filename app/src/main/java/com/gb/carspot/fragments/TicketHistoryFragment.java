package com.gb.carspot.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.adapters.TicketAdapter;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.viewmodels.MainActivityViewModel;

import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketHistoryFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private MainActivityViewModel mainActivityViewModel;
    private View rootView;

    private RecyclerView recyclerView;
    private TicketAdapter ticketAdapter;

    public TicketHistoryFragment()
    {
    }

    public static TicketHistoryFragment newInstance(MainActivityViewModel mainActivityViewModel)
    {
        return new TicketHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_ticket_history, container, false);
        setup();
        return rootView;
    }

    // initialize views
    private void setup()
    {
        if (getContext() != null)
        {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();

            recyclerView = rootView.findViewById(R.id.ticketHistory_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            ticketAdapter = new TicketAdapter((MainActivity) getActivity());
            recyclerView.setAdapter(ticketAdapter);

            mainActivityViewModel.getParkingTicketList().observe(getActivity(), new Observer<List<ParkingTicket>>()
            {
                @Override
                public void onChanged(List<ParkingTicket> parkingTickets)
                {
                    Log.d(TAG, "ParkingTicketList onChanged: " + parkingTickets.size());
                    ticketAdapter.setTicketList(parkingTickets);
                    ticketAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}