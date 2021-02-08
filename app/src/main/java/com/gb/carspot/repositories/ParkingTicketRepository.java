package com.gb.carspot.repositories;

import android.util.Log;

import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static com.gb.carspot.utils.Constants.COLLECTION_PARKING_TICKETS;
import static com.gb.carspot.utils.Constants.COLLECTION_USERS;
import static com.gb.carspot.utils.Constants.FIELD_DATE;

public class ParkingTicketRepository
{
    private final String TAG = getClass().getCanonicalName();
    private static ParkingTicketRepository instance;
    private FirebaseFirestore firestore;
    private MutableLiveData<List<ParkingTicket>> tickets = new MutableLiveData<List<ParkingTicket>>();

    public ParkingTicketRepository()
    {
        firestore = FirebaseFirestore.getInstance();
    }

    public static ParkingTicketRepository getInstance()
    {
        if (instance == null)
        {
            instance = new ParkingTicketRepository();
        }
        return instance;
    }

    public MutableLiveData<List<ParkingTicket>> getParkingTicketList(String userId)
    {
        getParkingTickets(userId);
        return tickets;
    }

    public void addParkingTicket(String userId, ParkingTicket parkingTicket)
    {
        firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_PARKING_TICKETS)
                .document(String.valueOf(parkingTicket.getDate().getTime()))
                .set(parkingTicket)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "Parking ticket saved successfully.");
                        }
                        else
                        {
                            Log.d(TAG, "There was a error. Parking ticket failed to save.");
                        }
                    }
                });
    }

    private void getParkingTickets(String userId)
    {
        try
        {
            // first we get their parking tickets
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_PARKING_TICKETS)
                    .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                ArrayList<ParkingTicket> parkingTicketArrayList = new ArrayList<ParkingTicket>();
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    ParkingTicket parkingTicket = document.toObject(ParkingTicket.class);
                                    parkingTicketArrayList.add(parkingTicket);
                                }
                                tickets.postValue(parkingTicketArrayList);
                                Log.d(TAG, "Parking tickets retrieved successfully.");
                            }
                            else
                            {
                                Log.d(TAG, "There was a error retrieving their parking tickets.");
                            }
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
            Log.d(TAG, e.getLocalizedMessage());
        }
    }
}
