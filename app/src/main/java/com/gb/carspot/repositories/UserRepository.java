package com.gb.carspot.repositories;

import android.util.Log;

import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static com.gb.carspot.utils.Constants.COLLECTION_PARKING_TICKETS;
import static com.gb.carspot.utils.Constants.COLLECTION_USERS;
import static com.gb.carspot.utils.Constants.FIELD_DATE;
import static com.gb.carspot.utils.Constants.FIELD_EMAIL;

public class UserRepository
{
    private final String TAG = getClass().getCanonicalName();
    private static UserRepository instance;
    private FirebaseFirestore firestore;
    private MutableLiveData<User> userData = new MutableLiveData<User>();

    public UserRepository()
    {
        firestore = FirebaseFirestore.getInstance();
    }


    public void addUser(final User newUser) {
        firestore.collection(COLLECTION_USERS)
                .document(newUser.getEmail())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully added new user for " + newUser.getEmail());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Could not add new user " + newUser.getEmail());
                        Log.e(TAG, e.toString());
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                });
    }

    public static UserRepository getInstance()
    {
        if (instance == null)
        {
            instance = new UserRepository();
        }
        return instance;
    }

    public MutableLiveData<User> getUser(String userId)
    {
        getUserFromFirestore(userId);
        return userData;
    }

    // get user from Firestore
    public void getUserFromFirestore(final String userId)
    {
        Log.d(TAG, "getUserFromFirestore: User: " + userId);
        final List<ParkingTicket> parkingTicketList = new ArrayList<ParkingTicket>();
        try
        {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                userData.postValue(task.getResult().toObject(User.class));
                                Log.d(TAG, "User retrieved successfully.");
                            }
                            else
                            {
                                Log.d(TAG, "User retrieval unsuccessful.");
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
