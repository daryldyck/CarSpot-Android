package com.gb.carspot.fragments;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.adapters.LicensePlateAdapter;
import com.gb.carspot.models.LicensePlate;
import com.gb.carspot.models.User;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

public class ManagePlatesFragment extends Fragment {
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    private MainActivityViewModel mainActivityViewModel;
    private View rootView;
    private MaterialButton btnAddPlate;
    private TextInputEditText editNewPlate;
    private RecyclerView platesRecyclerView;
    private LicensePlateAdapter licensePlateAdapter;
    private ItemTouchHelper touchHelper;

    private FirebaseAuth mAuth;

    private User userInfo;
    private List<LicensePlate> userPlates;

    public ManagePlatesFragment() {}

    public static ManagePlatesFragment newInstance(MainActivityViewModel mainActivityViewModel) {
        return new ManagePlatesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

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
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_manage_plates, container, false);
        btnAddPlate = rootView.findViewById(R.id.addPlate_button);
        btnAddPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlate();
            }
        });
        editNewPlate = rootView.findViewById(R.id.newPlate_textField);

        userPlates = new ArrayList<LicensePlate>();

        setupUserPlates(savedInstanceState);

        return rootView;
    }

    private void setupUserPlates(Bundle savedInstanceState) {
        if(getContext() != null) {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();

            platesRecyclerView = rootView.findViewById(R.id.licensePlates_recyclerView);
            platesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            platesRecyclerView.setHasFixedSize(true);
            licensePlateAdapter = new LicensePlateAdapter((MainActivity) getActivity());
            platesRecyclerView.setAdapter(licensePlateAdapter);
            touchHelper = new ItemTouchHelper(simpleCallback);
            touchHelper.attachToRecyclerView(platesRecyclerView);

            mainActivityViewModel.getUser().observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User user) {

                    for(int i = 0; i < user.getLicensePlates().size(); i++) {
                        userPlates.add(new LicensePlate(user.getLicensePlates().get(i)));
                    }
                    licensePlateAdapter.submitList(userPlates);

                    userInfo = new User(user.getEmail(), user.getPassword(), user.getPhone(),
                            user.getFirstName(), user.getLastName(), user.getLicensePlates());

                    licensePlateAdapter.setLicensePlates(userPlates);
                }
            });
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            List<String> newPlateList = userInfo.getLicensePlates();
            newPlateList.remove(viewHolder.getAdapterPosition());

            userPlates.remove(viewHolder.getAdapterPosition());
            licensePlateAdapter.setLicensePlates(userPlates);

            userInfo.setLicensePlates(newPlateList);
            licensePlateAdapter.setLicensePlates(userPlates);

            mainActivityViewModel.updateUserPlates(userInfo, newPlateList);
        }
    };

    private void addPlate() {
        Log.d(TAG, "Adding new plate: " + editNewPlate.getText().toString());

        List<String> newPlateList = userInfo.getLicensePlates();

        newPlateList.add(editNewPlate.getText().toString().toUpperCase());
        userPlates.add(new LicensePlate(editNewPlate.getText().toString().toUpperCase()));

        userInfo.setLicensePlates(newPlateList);
        licensePlateAdapter.setLicensePlates(userPlates);

        editNewPlate.setText("");
        mainActivityViewModel.updateUserPlates(userInfo, newPlateList);
    }

}
