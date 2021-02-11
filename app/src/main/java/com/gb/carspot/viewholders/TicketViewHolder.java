package com.gb.carspot.viewholders;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.adapters.TicketAdapter;
import com.gb.carspot.fragments.TicketDetailsFragment;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.gb.carspot.utils.Constants.PAGE_TICKET_DETAILS;

public class TicketViewHolder extends RecyclerView.ViewHolder
{
    private final String TAG = getClass().getCanonicalName();
    private final Float DEFAULT_ZOOM = 14.0f;
    private ImageView background;
    private View gradient;
    private MapView mapView;
    private TextView address;
    private TextView date;
    private TextView length;

    public TicketViewHolder(@NonNull View itemView)
    {
        super(itemView);
        background = itemView.findViewById(R.id.background_imageView);
        Utils.setHaptic(background);
        gradient = itemView.findViewById(R.id.gradient_view);
        mapView = itemView.findViewById(R.id.ticket_mapView);
        address = itemView.findViewById(R.id.address_textView);
        date = itemView.findViewById(R.id.date_textView);
        length = itemView.findViewById(R.id.length_textView);
    }

    public void bind(final TicketAdapter ticketAdapter, final ParkingTicket parkingTicket)
    {
        // used for shared element animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            background.setTransitionName("background" + "_" + parkingTicket.getDate().getTime());
            gradient.setTransitionName("gradient" + "_" + parkingTicket.getDate().getTime());
            mapView.setTransitionName("mapView" + "_" + parkingTicket.getDate().getTime());
            address.setTransitionName("address" + "_" + parkingTicket.getDate().getTime());
            date.setTransitionName("date" + "_" + parkingTicket.getDate().getTime());
            length.setTransitionName("length" + "_" + parkingTicket.getDate().getTime());
        }

        background.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TicketDetailsFragment ticketDetailsFragment = TicketDetailsFragment.newInstance(parkingTicket);

                // used for shared element animations
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    ticketAdapter.getMainActivity().getSupportFragmentManager().beginTransaction()
                            .addSharedElement(background, ViewCompat.getTransitionName(background))
                            .addSharedElement(gradient, ViewCompat.getTransitionName(gradient))
                            //.addSharedElement(mapView, ViewCompat.getTransitionName(mapView))
                            .addSharedElement(address, ViewCompat.getTransitionName(address))
                            .addSharedElement(date, ViewCompat.getTransitionName(date))
                            .addSharedElement(length, ViewCompat.getTransitionName(length))
                            .addToBackStack(null)
                            .replace(R.id.main_container, ticketDetailsFragment)
                            .commit();
                }
                else
                {
                    ticketAdapter.getMainActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, ticketDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
                ticketAdapter.getMainActivity().setCurrentPage(PAGE_TICKET_DETAILS);
            }
        });

        mapView.onCreate(null);
        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Log.d(TAG, "onMapReady: ");
                if (googleMap != null)
                {
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(parkingTicket.getLocation().getLat(),
                                    parkingTicket.getLocation().getLon()))
                            .icon(Utils.getBitmapDescriptor(ticketAdapter.getMainActivity(), R.drawable.ic_map_ticket_car))
                            .anchor(0f, 0.5f));


                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(parkingTicket.getLocation().getLat(),
                                    parkingTicket.getLocation().getLon()), DEFAULT_ZOOM));
                }
            }
        });
        mapView.onResume();

        address.setText(parkingTicket.getLocation().getStreetAddress());
        date.setText(parkingTicket.getDateString());
        length.setText(parkingTicket.getLength());
    }


}
