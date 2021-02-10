package com.gb.carspot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.viewholders.TicketViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder>
{
    private final String TAG = getClass().getCanonicalName();
    private MainActivity mainActivity;
    private List<ParkingTicket> parkingTicketList = new ArrayList<ParkingTicket>();

    public TicketAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

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
        holder.loadFields(this, parkingTicketList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return parkingTicketList.size();
    }

    public void setTicketList(List<ParkingTicket> parkingTicketList)
    {
        this.parkingTicketList = parkingTicketList;
    }

    public MainActivity getMainActivity()
    {
        return mainActivity;
    }

}
