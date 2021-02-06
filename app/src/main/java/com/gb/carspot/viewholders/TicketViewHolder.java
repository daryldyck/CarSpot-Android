package com.gb.carspot.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.models.ParkingTicket;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketViewHolder extends RecyclerView.ViewHolder
{
    private final String TAG = getClass().getCanonicalName();
    private ImageView imageView;
    private TextView address;
    private TextView date;
    private TextView length;

    public TicketViewHolder(@NonNull View itemView)
    {
        super(itemView);
        imageView = itemView.findViewById(R.id.ticket_imageView);
        address = itemView.findViewById(R.id.address_textView);
        date = itemView.findViewById(R.id.date_textView);
        length = itemView.findViewById(R.id.length_textView);
    }

    public void loadFields(ParkingTicket parkingTicket)
    {
        Picasso.get().load(parkingTicket.getImageUrl()).into(imageView);
        address.setText(parkingTicket.getLocation().getStreetAddress());
        date.setText(parkingTicket.getDateString());
        length.setText(parkingTicket.getLength());
    }
}
