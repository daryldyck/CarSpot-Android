package com.gb.carspot.viewholders;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.adapters.TicketAdapter;
import com.gb.carspot.fragments.TicketDetailsFragment;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.utils.Utils;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.gb.carspot.utils.Constants.PAGE_TICKET_DETAILS;

public class TicketViewHolder extends RecyclerView.ViewHolder
{
    private final String TAG = getClass().getCanonicalName();
    private ConstraintLayout constraintLayout;
    private ImageView imageView;
    private TextView address;
    private TextView date;
    private TextView length;

    public TicketViewHolder(@NonNull View itemView)
    {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.ticketPreview_constraintLayout);
        Utils.setHaptic(constraintLayout);
        imageView = itemView.findViewById(R.id.ticket_imageView);
        address = itemView.findViewById(R.id.address_textView);
        date = itemView.findViewById(R.id.date_textView);
        length = itemView.findViewById(R.id.length_textView);
    }

    public void bind(final TicketAdapter ticketAdapter, final ParkingTicket parkingTicket)
    {
        // used for shared element animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            constraintLayout.setTransitionName("constraintLayout" + "_" + parkingTicket.getDate().getTime());
            imageView.setTransitionName("imageView" + "_" + parkingTicket.getDate().getTime());
            address.setTransitionName("address" + "_" + parkingTicket.getDate().getTime());
            date.setTransitionName("date" + "_" + parkingTicket.getDate().getTime());
            length.setTransitionName("length" + "_" + parkingTicket.getDate().getTime());
        }

        constraintLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TicketDetailsFragment ticketDetailsFragment = TicketDetailsFragment.newInstance(parkingTicket);

                // used for shared element animations
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    ticketAdapter.getMainActivity().getSupportFragmentManager().beginTransaction()
                            .addSharedElement(constraintLayout, ViewCompat.getTransitionName(constraintLayout))
                            .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                            .addSharedElement(address, ViewCompat.getTransitionName(address))
                            .addSharedElement(date, ViewCompat.getTransitionName(date))
                            .addSharedElement(length, ViewCompat.getTransitionName(length))
                            .addToBackStack(null)
                            .replace(R.id.main_container, ticketDetailsFragment)
                            .commit();
                }
                else
                {
                    ticketAdapter.getMainActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, ticketDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
                ticketAdapter.getMainActivity().setCurrentPage(PAGE_TICKET_DETAILS);
            }
        });

        Picasso.get()
                .load(parkingTicket.getImageUrl())
                .placeholder(R.drawable.ic_map_default)
                .into(imageView);
        address.setText(parkingTicket.getLocation().getStreetAddress());
        date.setText(parkingTicket.getDateString());
        length.setText(parkingTicket.getLength());
    }
}
