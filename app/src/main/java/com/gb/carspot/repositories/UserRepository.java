package com.gb.carspot.repositories;

import android.util.Log;
import android.widget.Toast;

import com.gb.carspot.models.LicensePlate;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static com.gb.carspot.utils.Constants.COLLECTION_PARKING_TICKETS;
import static com.gb.carspot.utils.Constants.COLLECTION_USERS;
import static com.gb.carspot.utils.Constants.FIELD_DATE;
import static com.gb.carspot.utils.Constants.FIELD_EMAIL;
import static com.gb.carspot.utils.Constants.FIELD_FIRST_NAME;
import static com.gb.carspot.utils.Constants.FIELD_LAST_NAME;
import static com.gb.carspot.utils.Constants.FIELD_LICENSE_PLATES;
import static com.gb.carspot.utils.Constants.FIELD_PASSWORD;
import static com.gb.carspot.utils.Constants.FIELD_PHONE;

public class UserRepository
{
    private final String TAG = getClass().getCanonicalName();
    private static UserRepository instance;
    private FirebaseFirestore firestore;
    private MutableLiveData<User> userData = new MutableLiveData<User>();
    private MutableLiveData<List<LicensePlate>> licensePlateList = new MutableLiveData<List<LicensePlate>>();
    private FirebaseAuth mAuth;

    public UserRepository()
    {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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

    public void addUser(final String uid, final User newUser) {
        firestore.collection(COLLECTION_USERS)
                .document(uid)
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

    public void updateUserInfo(String uid, final String field, String newValue)
    {
        DocumentReference userRef = firestore.collection(COLLECTION_USERS)
                .document(uid);

        if(field.equals(FIELD_PHONE)) {

            long phoneNumber = Long.valueOf(newValue);

            userRef
                    .update(FIELD_PHONE, phoneNumber)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "User successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating user field " + field, e);
                        }
                    });
        } else {
            userRef
                    .update(field, newValue)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "User successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error updating user field " + field, e);
                        }
                    });
        }
    }

    public void updateUserFields(String uid, User newUserInfoObject) {
        DocumentReference userRef = firestore.collection(COLLECTION_USERS)
                .document(uid);

        userRef.update(
                FIELD_EMAIL, newUserInfoObject.getEmail(),
                FIELD_FIRST_NAME, newUserInfoObject.getFirstName(),
                FIELD_LAST_NAME, newUserInfoObject.getLastName(),
                FIELD_PASSWORD, newUserInfoObject.getPassword(),
                FIELD_PHONE, newUserInfoObject.getPhone()
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to update user.");
            }
        });
    }

    public void updateLicensePlates(String uid, List<String> newPlateList)
    {
        DocumentReference userRef = firestore.collection(COLLECTION_USERS)
                .document(uid);

        userRef
                .update(FIELD_LICENSE_PLATES, newPlateList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "License plates successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating license plates. ", e);
                    }
                });

    }

    public void deleteUser(String uid)
    {
        firestore.collection(COLLECTION_USERS).document(uid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully deleted old user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to delete user.");
            }
        });
    }
}
