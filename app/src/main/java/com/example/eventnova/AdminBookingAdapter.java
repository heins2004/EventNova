package com.example.eventnova;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder> {

    private final List<Booking> bookingList;

    public AdminBookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public AdminBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_booking, parent, false);
        return new AdminBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvUser.setText(booking.getUserName());
        holder.tvEvent.setText(booking.getEventTitle() + " • " + booking.getOrganizerName());
        holder.tvMeta.setText(EventUtils.formatEventDate(booking.getEventDate()) + " • " + booking.getLocation());
        holder.tvTickets.setText("Tickets: " + booking.getNumTickets());
        holder.tvTotal.setText(EventUtils.formatCurrency(booking.getTotalPrice()));
        holder.tvStatus.setText((booking.getStatus() + " | " + EventUtils.getEventPhase(booking.getEventDate(), booking.getEventTime())).toUpperCase(Locale.getDefault()));
        holder.tvStatus.setTextColor(booking.getStatus().equalsIgnoreCase("confirmed") ? 0xFF2E7D32 : 0xFFC62828);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class AdminBookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        TextView tvEvent;
        TextView tvMeta;
        TextView tvTickets;
        TextView tvTotal;
        TextView tvStatus;

        AdminBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvAdminBookingUser);
            tvEvent = itemView.findViewById(R.id.tvAdminBookingEvent);
            tvMeta = itemView.findViewById(R.id.tvAdminBookingMeta);
            tvTickets = itemView.findViewById(R.id.tvAdminBookingTickets);
            tvTotal = itemView.findViewById(R.id.tvAdminBookingTotal);
            tvStatus = itemView.findViewById(R.id.tvAdminBookingStatus);
        }
    }
}
