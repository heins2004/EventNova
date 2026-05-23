package com.example.eventnova;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private final boolean allowCancel;
    private final OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onCancelClick(Booking booking);
        void onViewBillClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, boolean allowCancel, OnBookingActionListener listener) {
        this.bookingList = bookingList;
        this.allowCancel = allowCancel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvTitle.setText(booking.getEventTitle());
        holder.tvDate.setText(EventUtils.formatEventDate(booking.getEventDate()) + " | " + EventUtils.formatEventTime(booking.getEventTime()));
        holder.tvVenue.setText(booking.getLocation() + " | " + booking.getCategory());
        holder.tvOrganizer.setText("By " + booking.getOrganizerName());
        holder.tvTickets.setText("Tickets: " + booking.getNumTickets());
        holder.tvTotal.setText(EventUtils.formatCurrency(booking.getTotalPrice()));
        holder.tvStatus.setText((booking.getStatus() + " | " + EventUtils.getEventPhase(booking.getEventDate(), booking.getEventTime())).toUpperCase(Locale.getDefault()));

        if (booking.getStatus().equalsIgnoreCase("cancelled") || EventUtils.isPastEvent(booking.getEventDate(), booking.getEventTime())) {
            holder.tvStatus.setTextColor(0xFFC62828);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(0xFF2E7D32);
            holder.btnCancel.setVisibility(allowCancel ? View.VISIBLE : View.GONE);
        }

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(booking);
            }
        });
        holder.btnViewBill.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewBillClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDate;
        TextView tvVenue;
        TextView tvOrganizer;
        TextView tvTickets;
        TextView tvTotal;
        TextView tvStatus;
        MaterialButton btnCancel;
        MaterialButton btnViewBill;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookingEventTitle);
            tvDate = itemView.findViewById(R.id.tvBookingDate);
            tvVenue = itemView.findViewById(R.id.tvBookingVenue);
            tvOrganizer = itemView.findViewById(R.id.tvBookingOrganizer);
            tvTickets = itemView.findViewById(R.id.tvBookingTickets);
            tvTotal = itemView.findViewById(R.id.tvBookingTotal);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
            btnViewBill = itemView.findViewById(R.id.btnViewBill);
        }
    }
}
