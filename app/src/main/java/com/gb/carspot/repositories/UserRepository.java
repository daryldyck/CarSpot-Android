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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static com.gb.carspot.utils.Constants.COLLECTION_PARKING_TICKETS;
import static com.gb.carspot.utils.Constants.COLLECTION_USERS;
import static com.gb.carspot.utils.Constants.FIELD_DATE;
import static com.gb.carspot.utils.Constants.FIELD_EMAIL;

public class UserRepository
{
    private final String TAG = getClass().getCanonicalName();
    private FirebaseFirestore firestore;

    public UserRepository()
    {
        firestore = FirebaseFirestore.getInstance();
    }

    // get user from Firestore
    public void getUser(final String userEmail)
    {
        // need to add snapshot listeners later.
        final List<ParkingTicket> parkingTicketList = new ArrayList<ParkingTicket>();
        try
        {
            // first we get their parking tickets
            firestore.collection(COLLECTION_USERS)
                    .document(userEmail)
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
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    ParkingTicket parkingTicket = document.toObject(ParkingTicket.class);
                                    parkingTicketList.add(parkingTicket);
                                }
                                Log.d(TAG, "Parking tickets retrieved successfully.");
                            }
                            else
                            {
                                Log.d(TAG, "There was a error retrieving their parking tickets.");
                            }

                            // after parking ticket retrieval done - get user and add ticket list to it.
                            firestore.collection(COLLECTION_USERS)
                                    .document(userEmail)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                User user = task.getResult().toObject(User.class);
                                                user.setParkingTickets(parkingTicketList);
                                                Log.d(TAG, "User retrieved successfully.");
                                            }
                                            else
                                            {
                                                Log.d(TAG, "User retrieval unsuccessful.");
                                            }
                                        }
                                    });
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public void updateUserInfo()
    {

    }

    public void addLicensePlate()
    {

    }

    public void removeLicensePlate()
    {

    }

    public void deleteUser()
    {

    }
}
