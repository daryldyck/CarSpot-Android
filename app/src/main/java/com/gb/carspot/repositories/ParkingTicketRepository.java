package com.gb.carspot.repositories;

import android.util.Log;

import com.gb.carspot.models.ParkingTicket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

import static com.gb.carspot.utils.Constants.COLLECTION_PARKING_TICKETS;
import static com.gb.carspot.utils.Constants.COLLECTION_USERS;

public class ParkingTicketRepository
{
    private final String TAG = getClass().getCanonicalName();
    private FirebaseFirestore firestore;

    public ParkingTicketRepository()
    {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addParkingTicket(String userEmail, ParkingTicket parkingTicket)
    {
        firestore.collection(COLLECTION_USERS)
                .document(userEmail)
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
}
