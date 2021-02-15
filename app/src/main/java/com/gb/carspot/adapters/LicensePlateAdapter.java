package com.gb.carspot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.LicensePlate;
import com.gb.carspot.models.User;
import com.gb.carspot.viewholders.LicensePlateHolder;

import java.util.ArrayList;
import java.util.List;

public class LicensePlateAdapter extends ListAdapter<LicensePlate, LicensePlateHolder> {
    private List<LicensePlate> licensePlates = new ArrayList<>();
    private User user = new User();

    private final String TAG = getClass().getCanonicalName();
    private MainActivity mainActivity;

    public LicensePlateAdapter(MainActivity mainActivity) {
        super(DIFF_CALLBACK);
        this.mainActivity = mainActivity;
    }

    public static final DiffUtil.ItemCallback<LicensePlate> DIFF_CALLBACK = new DiffUtil.ItemCallback<LicensePlate>() {
        @Override
        public boolean areItemsTheSame(@NonNull LicensePlate oldItem, @NonNull LicensePlate newItem) {
            return oldItem.getPlateNumber().equals(newItem.getPlateNumber());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LicensePlate oldItem, @NonNull LicensePlate newItem) {
            return oldItem.getPlateNumber().equals(newItem.getPlateNumber());
        }
    };

    @NonNull
    @Override
    public LicensePlateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.license_plate_item, parent, false);
        return new LicensePlateHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LicensePlateHolder holder, int position) {
        holder.bind(this, getItem(position));
    }

    public void setLicensePlates(List<LicensePlate> licensePlates) {
        this.licensePlates = licensePlates;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }

    public List<LicensePlate> getLicensePlates() {
        return licensePlates;
    }

    public User getUser() {
        return user;
    }
}
