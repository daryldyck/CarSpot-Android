//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.viewholders;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.gb.carspot.utils.Constants.PAGE_TICKET_DETAILS;

public class TicketViewHolder extends RecyclerView.ViewHolder
{
    private final String TAG = getClass().getCanonicalName();
    private TicketAdapter ticketAdapter;
    private final Float DEFAULT_ZOOM = 15.0f;
    private ImageView background;
    private View gradient;
    private MapView mapView;
    private ImageView mapImageView;
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
        mapImageView = itemView.findViewById(R.id.map_imageView);
        address = itemView.findViewById(R.id.address_textView);
        date = itemView.findViewById(R.id.date_textView);
        length = itemView.findViewById(R.id.length_textView);
    }

    // bind all values to current ticket
    public void bind(final TicketAdapter ticketAdapter, final ParkingTicket parkingTicket)
    {
        this.ticketAdapter = ticketAdapter;

        // used for shared element animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            background.setTransitionName("background" + "_" + parkingTicket.getDate().getTime());
            gradient.setTransitionName("gradient" + "_" + parkingTicket.getDate().getTime());
            mapImageView.setTransitionName("mapView" + "_" + parkingTicket.getDate().getTime());
            address.setTransitionName("address" + "_" + parkingTicket.getDate().getTime());
            date.setTransitionName("date" + "_" + parkingTicket.getDate().getTime());
            length.setTransitionName("length" + "_" + parkingTicket.getDate().getTime());
        }

        // parking ticket on click listener to load ticket details page
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
                            .addSharedElement(mapImageView, ViewCompat.getTransitionName(mapImageView))
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

        // check if map image exists, if not - load mapView
        if (!parkingTicket.getImageUrl().isEmpty())
        {
            // load map image from Google Storage
            mapView.setVisibility(View.INVISIBLE);
            mapImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(parkingTicket.getImageUrl()).placeholder(R.drawable.ic_map_default).into(mapImageView);
        }
        else
        {
            // setup Google MapView preview
            mapView.onCreate(null);
            mapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(final GoogleMap googleMap)
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

                        // listen for map to be fully rendered
                        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                        {
                            @Override
                            public void onMapLoaded()
                            {
                                // take snapshot of complete map fully rendered
                                googleMap.snapshot(new GoogleMap.SnapshotReadyCallback()
                                {
                                    @Override
                                    public void onSnapshotReady(Bitmap bitmap)
                                    {
                                        // save snapshot
                                        saveBitmapToCloud(bitmap, parkingTicket);
                                    }
                                });
                            }
                        });

                    }
                }
            });
            mapView.onResume();
        }

        address.setText(parkingTicket.getLocation().getStreetAddress());
        date.setText(parkingTicket.getDateString());
        length.setText(parkingTicket.getLength());
    }

    // save map image to Google Cloud Storage
    private void saveBitmapToCloud(Bitmap bitmap, final ParkingTicket parkingTicket)
    {
        final String fileName = "" + parkingTicket.getDate().getTime() + "_map";
        FirebaseStorage storage = FirebaseStorage.getInstance();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        final StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("images/" + fileName + ".jpg");

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            // save parking ticket to Firestore
                            parkingTicket.setImageUrl(uri.toString());
                            ticketAdapter.getMainActivity().getViewModel().saveParkingTicket(parkingTicket);

                            // load map image from Google Storage into imageView
                            Picasso.get().load(parkingTicket.getImageUrl()).into(mapImageView, new Callback()
                            {
                                @Override
                                public void onSuccess()
                                {
                                    // set imageView visible when image loaded completely.
                                    mapImageView.setVisibility(View.VISIBLE);

                                    // fade out the mapView to avoid any flickering
                                    mapView.animate().alpha(0).setDuration(3000).setListener(new Animator.AnimatorListener()
                                    {
                                        @Override
                                        public void onAnimationStart(Animator animator)
                                        {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator)
                                        {
                                            // set invisible one animation complete.
                                            mapView.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator)
                                        {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator)
                                        {

                                        }
                                    });

                                    Log.d(TAG, "onSuccess: Mapview gone");
                                }

                                @Override
                                public void onError(Exception e)
                                {
                                    Log.d(TAG, "onError: ");
                                }
                            });
                            Log.d(TAG, "UploadTask: " + fileName + " saved successfully!");
                        }
                    });
                }
                else
                {
                    Log.d(TAG, "UploadTask: Error: " + fileName + " save unsuccessful.");
                }
            }
        });
    }
}
