//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.viewholders.TicketViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

// using a ListAdapter to ensure updates to the list are correctly animated in the recyclerView
public class TicketAdapter extends ListAdapter<ParkingTicket, TicketViewHolder>
{
    private final String TAG = getClass().getCanonicalName();
    private MainActivity mainActivity;

    public TicketAdapter(MainActivity mainActivity)
    {
        super(DIFF_CALLBACK);
        this.mainActivity = mainActivity;
    }

    // this callback controls differences in the list and its objects
    public static final DiffUtil.ItemCallback<ParkingTicket> DIFF_CALLBACK = new DiffUtil.ItemCallback<ParkingTicket>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull ParkingTicket oldItem, @NonNull ParkingTicket newItem)
        {
            return oldItem.getDate().getTime() == newItem.getDate().getTime();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ParkingTicket oldItem, @NonNull ParkingTicket newItem)
        {
            return itemHasNotChanged(oldItem, newItem);
        }
    };

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position)
    {
        holder.bind(this, getItem(position));
    }

    public MainActivity getMainActivity()
    {
        return mainActivity;
    }

    public static boolean itemHasNotChanged(ParkingTicket oldItem, ParkingTicket newItem)
    {
        if (!oldItem.getBuildingCode().equals(newItem.getBuildingCode()))
        {
            return false;
        }
        else if (oldItem.getNoOfHours() != newItem.getNoOfHours())
        {
            return false;
        }
        else if (!oldItem.getLicensePlate().equals(newItem.getLicensePlate()))
        {
            return false;
        }
        else if (!oldItem.getHostSuite().equals(newItem.getHostSuite()))
        {
            return false;
        }
        else if (!oldItem.getImageUrl().equals(newItem.getImageUrl()))
        {
            return false;
        }
        else if (oldItem.getLocation().getLat() != newItem.getLocation().getLat())
        {
            return false;
        }
        else if (oldItem.getLocation().getLon() != newItem.getLocation().getLon())
        {
            return false;
        }
        else if (!oldItem.getLocation().getStreetAddress().equals(newItem.getLocation().getStreetAddress()))
        {
            return false;
        }
        else if (!oldItem.getLocation().getCity().equals(newItem.getLocation().getCity()))
        {
            return false;
        }
        else if (!oldItem.getLocation().getCountry().equals(newItem.getLocation().getCountry()))
        {
            return false;
        }
        else if (oldItem.getLocation().isCurrentLocation() != newItem.getLocation().isCurrentLocation())
        {
            return false;
        }

        return true;
    }
}
