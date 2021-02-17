package com.gb.carspot.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.activities.LoginActivity;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.UserRepository;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.LoginActivityViewModel;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;

import java.util.ArrayList;
import java.util.List;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_LOGIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_MANAGE_PLATES_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOGOUT;
import static com.gb.carspot.utils.Constants.FIELD_EMAIL;
import static com.gb.carspot.utils.Constants.FIELD_FIRST_NAME;
import static com.gb.carspot.utils.Constants.FIELD_LAST_NAME;
import static com.gb.carspot.utils.Constants.FIELD_PASSWORD;
import static com.gb.carspot.utils.Constants.FIELD_PHONE;
import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.LOGIN_REMEMBER_ME;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    private MainActivityViewModel mainActivityViewModel;
    private LoginActivityViewModel loginActivityViewModel;
    private View rootView;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;

    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editEmail;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirm;
    private TextInputEditText editPlateNumber;
    private TextInputLayout inputLayoutEnterPlate;
    private MaterialButton btnManagePlates;
    private MaterialButton btnSignUp;
    private MaterialButton btnDelete;

    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmInputLayout;

    private User currentUserInfo;
    private boolean okayToError = false;
    boolean fieldChanged = false;
    private FirebaseUser currentFirebaseUser;

    public ProfileFragment()
    {
    }

    public static ProfileFragment newInstance()
    {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        userRepository = new UserRepository();

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }
        Log.d(TAG, "DONE");

    }

    @Override
    public void onStart() {
        super.onStart();

        //Check to see if there is a current user
        currentFirebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();

        String cu = sharedPrefs.getString(LOGIN_CURRENT_USER, null);
        if(cu != null && getActivity() instanceof MainActivity) {
            setupUserInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        editFirstName = rootView.findViewById(R.id.firstName_textField);
        editLastName = rootView.findViewById(R.id.lastName_textField);
        editEmail = rootView.findViewById(R.id.email_textField);
        editPhoneNumber = rootView.findViewById(R.id.phoneNumber_textField);
        editPassword = rootView.findViewById(R.id.password_textField);
        editConfirm = rootView.findViewById(R.id.confirmPassword_textField);
        btnManagePlates = rootView.findViewById(R.id.managePlates_button);
        inputLayoutEnterPlate = rootView.findViewById(R.id.enterPlate_InputLayout);
        editPlateNumber = rootView.findViewById(R.id.enterPlate_textField);
        btnManagePlates = rootView.findViewById(R.id.managePlates_button);
        btnManagePlates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLicensePlateFragment();
            }
        });

        btnSignUp = rootView.findViewById(R.id.createAccount_button);
        btnDelete = rootView.findViewById(R.id.delete_button);

        if(sharedPrefs.getString(LOGIN_CURRENT_USER, null) != null && getActivity() instanceof MainActivity){
            //if user is already in go to main activity
            btnManagePlates.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            inputLayoutEnterPlate.setVisibility(View.INVISIBLE);
            setupUserInfo();
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser();
                }
            });
            btnSignUp.setText("Update Account Info");
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Updating account");
                    updateAccount();
                }
            });
        } else {
            setupTextFieldsForEditing();
            btnDelete.setVisibility(View.INVISIBLE);
            btnManagePlates.setVisibility(View.INVISIBLE);
            inputLayoutEnterPlate.setVisibility(View.VISIBLE);
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAccount();
                }
            });
        }

        return rootView;
    }

    // load License Plate fragment
    private void loadLicensePlateFragment() {
        Log.d(TAG, "loadLicensePlateFragment()");
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(ACTION_LOAD_MANAGE_PLATES_PAGE);
        startActivity(intent);
    }

    private void setupUserInfo() {
        if(getContext() != null) {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();
            setupTextFieldsForEditing();
        }

        mainActivityViewModel.getUser().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                editFirstName.setText(user.getFirstName());
                editLastName.setText(user.getLastName());
                editEmail.setText(user.getEmail());
                editPhoneNumber.setText(Long.toString(user.getPhone()));
                editPassword.setText(user.getPassword());
                editConfirm.setText(user.getPassword());

                currentUserInfo = new User(user.getEmail(), user.getPassword(),
                        user.getPhone(), user.getFirstName(), user.getLastName(),
                        user.getLicensePlates());
            }
        });

        Log.d(TAG, "Loaded user: " + currentUserInfo.toString());
    }

    private void setupTextFieldsForEditing() {
        okayToError = false;

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                okayToError = true;
            }
        });

        firstNameInputLayout = rootView.findViewById(R.id.firstName_InputLayout);
        firstNameInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkName(firstNameInputLayout, getString(R.string.cannot_be_empty));
                }
            }
        });

        lastNameInputLayout = rootView.findViewById(R.id.lastName_InputLayout);
        lastNameInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkName(lastNameInputLayout, getString(R.string.cannot_be_empty));
                }
            }
        });

        emailInputLayout = rootView.findViewById(R.id.email_InputLayout);
        emailInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkEmail(emailInputLayout, getString(R.string.invalid_email));
                }
            }
        });

        phoneInputLayout = rootView.findViewById(R.id.phoneNumber_InputLayout);
        phoneInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkPhone(phoneInputLayout, getString(R.string.invalid_phone_number));
                }
            }
        });

        passwordInputLayout = rootView.findViewById(R.id.password_InputLayout);
        passwordInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkPassword(passwordInputLayout, getString(R.string.invalid_password));
                }
            }
        });

        confirmInputLayout = rootView.findViewById(R.id.confirmPassword_InputLayout);
        confirmInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(okayToError) {
                    Utils.checkConfirm(passwordInputLayout, confirmInputLayout,
                            getString(R.string.passwords_do_not_match));
                }
            }
        });
    }

    public void updateAccount() {
        fieldChanged = false;

        if (!editFirstName.getText().toString().equals(currentUserInfo.getFirstName())
                && Utils.checkName(firstNameInputLayout, getString(R.string.cannot_be_empty))) {

            String newFirstName = editFirstName.getText().toString();

            Log.d(TAG, "First name update to " + newFirstName
                    + " from " + currentUserInfo.getFirstName());

            currentUserInfo.setFirstName(newFirstName);
            mainActivityViewModel.getUser().getValue().setFirstName(newFirstName);
            fieldChanged = true;
            Log.d(TAG, "Firstname change");
        } else {
            Log.d(TAG, "No change in first name.");
        }

        if (!editLastName.getText().toString().equals(currentUserInfo.getLastName())
                && Utils.checkName(lastNameInputLayout, getString(R.string.cannot_be_empty))) {

            String newLastName = editLastName.getText().toString();

            Log.d(TAG, "First name update to " + newLastName
                    + " from " + currentUserInfo.getLastName());

            currentUserInfo.setLastName(newLastName);
            mainActivityViewModel.getUser().getValue().setLastName(newLastName);
            fieldChanged = true;
            Log.d(TAG, "Lastname change");
        } else {
            Log.d(TAG, "No change in last name.");
        }

        if (Long.valueOf(editPhoneNumber.getText().toString()) != currentUserInfo.getPhone()
                && Utils.checkPhone(phoneInputLayout, getString(R.string.invalid_phone_number))) {

            String newPhone = editPhoneNumber.getText().toString();

            Log.d(TAG, "Phone update to " + newPhone
                    + " from " + currentUserInfo.getPhone());

            currentUserInfo.setPhone(Long.valueOf(newPhone));
            mainActivityViewModel.getUser().getValue().setPhone(Long.valueOf(newPhone));
            fieldChanged = true;
            Log.d(TAG, "Phone change");
        } else {
            Log.d(TAG, "No change in phone number.");
        }

        updatePassword();
        updateEmail();

        if(fieldChanged) {
            mainActivityViewModel.updateUserFields(currentFirebaseUser.getUid(),
                    mainActivityViewModel.getUser().getValue());
            Toast.makeText(getActivity(), "Account updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccount() {
        String fName = editFirstName.getText().toString();
        String lName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String phone = editPhoneNumber.getText().toString();
        String password = editPassword.getText().toString();
        String confirm = editConfirm.getText().toString();
        String lPlate = editPlateNumber.getText().toString();

        if(fName.isEmpty() && lName.isEmpty() && email.isEmpty() && phone.isEmpty()
                && password.isEmpty() && confirm.isEmpty() && lPlate.isEmpty()) {
            Toast.makeText(getActivity(), "Fields should not be empty", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Fields should not be empty");
        } else {
            mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                String id = mAuth.getCurrentUser().getUid();
                                Log.d(TAG, "createUserWithEmail:success. UID: " + id);

                                addUserToFirebase(id);
                                Toast.makeText(getActivity(), "Account Created", Toast.LENGTH_LONG).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Log.d(TAG, task.getResult().toString());
                                Toast.makeText(getActivity(), "Could not create account", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }

    public void addUserToFirebase(String uid) {
        if(getContext() != null) {
            this.loginActivityViewModel = ((LoginActivity) getActivity()).getLoginActivityViewModel();
        }

        List<String> newPlateList = new ArrayList<>();
        newPlateList.add(editPlateNumber.getText().toString().toUpperCase());

        User newUser = new User(editEmail.getText().toString().toLowerCase(), editPassword.getText().toString(),
                Long.parseLong(editPhoneNumber.getText().toString()), editFirstName.getText().toString(),
                editLastName.getText().toString(), newPlateList);

        loginActivityViewModel.createAccount(uid ,newUser);

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setAction(ACTION_LOAD_LOGIN_PAGE);
        startActivity(intent);
    }

    public void updatePassword() {
        if (!editPassword.getText().toString().equals(currentUserInfo.getPassword())
                && Utils.checkConfirm(passwordInputLayout, confirmInputLayout, getString(R.string.invalid_email))) {

            String newPassword = editPassword.getText().toString();

            Log.d(TAG, "Password update to " + newPassword
                    + " from " + currentUserInfo.getPassword());

            currentFirebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "FirebaseAuth password Changed.");

//                    mainActivityViewModel.updateUserField(currentFirebaseUser.getUid(),
//                            FIELD_PASSWORD, editPassword.getText().toString());

                    currentUserInfo.setPassword(editPassword.getText().toString());

                    mainActivityViewModel.getUser().getValue().setPassword(editPassword.getText().toString());

                    mAuth.signInWithEmailAndPassword(currentFirebaseUser.getEmail(), editPassword.getText().toString());
                    mainActivityViewModel.updateUserField(currentFirebaseUser.getUid(), FIELD_PASSWORD,
                            editPassword.getText().toString());
                    fieldChanged = true;
                    Log.d(TAG, "Password change");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FirebaseAuth password change error");
                    Log.e(TAG, e.getLocalizedMessage());
                }
            });
        } else {
            Log.d(TAG, "No change in password.");
        }
    }

    public void updateEmail() {
        //Email change
        if (!editEmail.getText().toString().equals(currentUserInfo.getEmail())
                && Utils.checkName(emailInputLayout, getString(R.string.invalid_email))) {

            String newEmail = editEmail.getText().toString().toLowerCase();

            Log.d(TAG, "Email update to " + newEmail
                    + " from " + currentUserInfo.getEmail());

            currentFirebaseUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "FirebaseAuth email Changed.");

                    //set new email
                    currentUserInfo.setEmail(editEmail.getText().toString().toLowerCase());

//                    mainActivityViewModel.updateUserField(currentFirebaseUser.getUid(),
//                            FIELD_EMAIL, editEmail.getText().toString().toLowerCase());

                    mainActivityViewModel.getUser().getValue().setEmail(editEmail.getText().toString().toLowerCase());

                    prefEditor.putString(LOGIN_CURRENT_USER, currentFirebaseUser.getUid());
                    prefEditor.apply();
                    fieldChanged = true;
                    Log.d(TAG, "Email change");
                    mainActivityViewModel.updateUserField(currentFirebaseUser.getUid(), FIELD_EMAIL,
                            editEmail.getText().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FirebaseAuth email change error");
                    Log.e(TAG, e.getLocalizedMessage());
                }
            });
        } else {
            Log.d(TAG, "No change in email.");
        }
    }

    public void updateEmailAndPassword() {
        //Email change
        if (!editEmail.getText().toString().equals(currentUserInfo.getEmail())
                && Utils.checkName(emailInputLayout, getString(R.string.invalid_email))) {

            String newEmail = editEmail.getText().toString().toLowerCase();

            Log.d(TAG, "Email update to " + newEmail
                    + " from " + currentUserInfo.getEmail());

            currentFirebaseUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "FirebaseAuth email Changed.");

                    //set new email
                    currentUserInfo.setEmail(editEmail.getText().toString().toLowerCase());

                    mainActivityViewModel.getUser().getValue().setEmail(editEmail.getText().toString().toLowerCase());

                    prefEditor.putString(LOGIN_CURRENT_USER, currentFirebaseUser.getUid());
                    prefEditor.apply();
                    fieldChanged = true;
                    Log.d(TAG, "Email change");

                    currentFirebaseUser.updatePassword(editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String newPassword = editPassword.getText().toString();

                            Log.d(TAG, "Password update to " + newPassword
                                    + " from " + currentUserInfo.getPassword());

                            currentFirebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "FirebaseAuth password Changed.");

                                    currentUserInfo.setPassword(editPassword.getText().toString());

                                    mainActivityViewModel.getUser().getValue().setPassword(editPassword.getText().toString());

                                    mAuth.signInWithEmailAndPassword(currentFirebaseUser.getEmail(), editPassword.getText().toString());

                                    mainActivityViewModel.updateUserFields(currentFirebaseUser.getUid(),
                                            mainActivityViewModel.getUser().getValue());

                                    fieldChanged = true;
                                    Log.d(TAG, "Password change");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Could not update password after email");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Could not update email before password");
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FirebaseAuth email change error");
                    Log.e(TAG, e.getLocalizedMessage());
                }
            });
        } else {
            Log.d(TAG, "No change in email.");
        }
    }

    public void deleteUser() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Deleting user");

                        currentFirebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mainActivityViewModel.deleteUser(currentFirebaseUser.getUid());
                                Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG);

                                //Sign out current firebase user
                                mAuth.signOut();

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setAction(ACTION_LOGOUT);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error deleting account", Toast.LENGTH_LONG);
                            }
                        });
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}