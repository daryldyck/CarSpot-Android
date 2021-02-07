package com.gb.carspot.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.viewmodels.TicketDetailsFragmentViewModel;
import com.google.android.gms.maps.MapView;

import static com.gb.carspot.utils.Constants.ACTION_DISPLAY_BACK_BUTTON;
import static com.gb.carspot.utils.Constants.EXTRA_PARKING_TICKET;
import static com.gb.carspot.utils.Constants.INITIAL_FRAGMENT_LOAD;

public class TicketDetailsFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;
    private TicketDetailsFragmentViewModel viewModel;

    private MapView mapView;
    private ConstraintLayout constraintLayout;
    private TextView address;
    private TextView address2;
    private TextView buildingCode;
    private TextView aptNo;
    private TextView date;
    private TextView start;
    private TextView end;
    private TextView license;
    private TextView length;

    // used to track first load of fragment
    private boolean initialFragmentLoad = true;

    public TicketDetailsFragment()
    {
    }

    public static TicketDetailsFragment newInstance(ParkingTicket parkingTicket)
    {
        Log.d("TicketDetailsFragment", "newInstance: ");
        TicketDetailsFragment fragment = new TicketDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_PARKING_TICKET, parkingTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TicketDetailsFragmentViewModel.class);

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }

        if (getArguments() != null)
        {
            viewModel.setParkingTicket((ParkingTicket) getArguments().getSerializable(EXTRA_PARKING_TICKET));

            Log.d(TAG, "onCreate: " + viewModel.getParkingTicket().getLocation().getStreetAddress());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        setup();
        setupBackButton();
        return rootView;
    }

    // setup and load fields with ticket data
    private void setup()
    {
        if (getContext() != null)
        {
            constraintLayout = rootView.findViewById(R.id.ticketDetails_constraintLayout);
            mapView = rootView.findViewById(R.id.mapView);
            address = rootView.findViewById(R.id.address_textView);
            address2 = rootView.findViewById(R.id.address2_textView);
            buildingCode = rootView.findViewById(R.id.buildingCode_textView);
            aptNo = rootView.findViewById(R.id.suiteNo_textView);
            date = rootView.findViewById(R.id.dateValue_textView);
            start = rootView.findViewById(R.id.startValue_textView);
            end = rootView.findViewById(R.id.endValue_textView);
            license = rootView.findViewById(R.id.license_textView);
            length = rootView.findViewById(R.id.length_textView);

            // used for shared element animations
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                constraintLayout.setTransitionName("constraintLayout" + "_" + viewModel.getParkingTicket().getDate().getTime());
                mapView.setTransitionName("imageView" + "_" + viewModel.getParkingTicket().getDate().getTime());
                address.setTransitionName("address" + "_" + viewModel.getParkingTicket().getDate().getTime());
                date.setTransitionName("date" + "_" + viewModel.getParkingTicket().getDate().getTime());
                length.setTransitionName("length" + "_" + viewModel.getParkingTicket().getDate().getTime());
            }

            //TODO - load map

            address.setText(viewModel.getParkingTicket().getLocation().getStreetAddress());
            address2.setText(viewModel.getParkingTicket().getLocation().getCity() + ", " +
                    viewModel.getParkingTicket().getLocation().getCountry());
            buildingCode.setText(viewModel.getParkingTicket().getBuildingCode());
            aptNo.setText(viewModel.getParkingTicket().getHostSuite());
            date.setText(viewModel.getParkingTicket().getDateString());
            start.setText(viewModel.getParkingTicket().getStartString());
            end.setText(viewModel.getParkingTicket().getEndString());
            license.setText(viewModel.getParkingTicket().getLicensePlate());
            length.setText(viewModel.getParkingTicket().getLength());
        }
    }

    // turn on back button in toolbar from MainActivity
    private void setupBackButton()
    {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(ACTION_DISPLAY_BACK_BUTTON);
        startActivity(intent);
    }

}