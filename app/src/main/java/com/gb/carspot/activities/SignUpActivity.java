package com.gb.carspot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = getClass().getCanonicalName();
    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editEmail;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirm;
    private TextInputEditText editPlateNumber;
    private MaterialButton btnSignUp;

    private FirebaseAuth mAuth;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();

        editFirstName = findViewById(R.id.firstName_textField);
        editLastName = findViewById(R.id.lastName_textField);
        editEmail = findViewById(R.id.email_textField);
        editPhoneNumber = findViewById(R.id.phoneNumber_textField);
        editPassword = findViewById(R.id.password_textField);
        editConfirm = findViewById(R.id.confirmPassword_textField);
        btnSignUp = findViewById(R.id.createAccount_button);
        btnSignUp.setText("Sign Up");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        loadLicensePlateFragment();
    }

    // load License Plate fragment
    private void loadLicensePlateFragment() {
        Log.d(TAG, "loadLicensePlateFragment()");

        ViewGroup addPlateFrame = findViewById(R.id.licensePlate_frame);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vi = inflater.inflate(R.layout.fragment_enter_license_plate, addPlateFrame, true);

        editPlateNumber = (TextInputEditText)vi.findViewById(R.id.enterPlate_textField);
        editPlateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "ENTER NEW PLATE.");
            }
        });
    }

    public void signUp() {
        String newEmail = editEmail.getText().toString();
        String newPassword = editPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(newEmail, newPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            addUserInfo();
                            Toast.makeText(SignUpActivity.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Account creation failed.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

    public void addUserInfo() {

        List<String> newPlateList = new ArrayList<>();
        newPlateList.add(editPlateNumber.getText().toString().toUpperCase());

        User newUser = new User(editEmail.getText().toString(), editPassword.getText().toString(),
                Long.parseLong(editPhoneNumber.getText().toString()), editFirstName.getText().toString(),
                editLastName.getText().toString(), newPlateList);

        userRepository.addUser(newUser);
    }

}