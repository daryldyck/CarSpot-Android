//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

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
import static com.gb.carspot.utils.Constants.TICKET_ADDED;
import static com.gb.carspot.utils.Constants.TICKET_FAILED;

public class ParkingTicketRepository
{
    private final String TAG = getClass().getCanonicalName();
    private static ParkingTicketRepository instance;
    private FirebaseFirestore firestore;
    private MutableLiveData<List<ParkingTicket>> tickets = new MutableLiveData<List<ParkingTicket>>();
    private MutableLiveData<Integer> ticketAdded = new MutableLiveData<Integer>();

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

    public MutableLiveData<Integer> getTicketAdded()
    {
        return ticketAdded;
    }

    public void addParkingTicket(String userId, final ParkingTicket parkingTicket)
    {
        Log.d(TAG, "addParkingTicket: ");
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
                            tickets.getValue().add(0, parkingTicket);
                            ticketAdded.postValue(TICKET_ADDED);
                            Log.d(TAG, "Parking ticket saved successfully.");
                        }
                        else
                        {
                            ticketAdded.postValue(TICKET_FAILED);
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
