//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.viewholders;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gb.carspot.R;
import com.gb.carspot.adapters.LicensePlateAdapter;
import com.gb.carspot.models.LicensePlate;

public class LicensePlateHolder extends RecyclerView.ViewHolder {

    private LicensePlateAdapter licensePlateAdapter;
    private TextView tvPlateNumber;
    private ConstraintLayout licensePlateItem;

    private final String TAG = this.getClass().getCanonicalName();

    public LicensePlateHolder(@NonNull View itemView) {
        super(itemView);
        tvPlateNumber = itemView.findViewById(R.id.tvPlateNumber);
        licensePlateItem = itemView.findViewById(R.id.licensePlate_layout);
    }

    public void bind(final LicensePlateAdapter licensePlateAdapter, final LicensePlate licensePlate) {
        this.licensePlateAdapter = licensePlateAdapter;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            tvPlateNumber.setTransitionName("plateNumber" + "_" + licensePlate.getPlateNumber());
        }

        tvPlateNumber.setText(licensePlate.getPlateNumber());
    }
}
