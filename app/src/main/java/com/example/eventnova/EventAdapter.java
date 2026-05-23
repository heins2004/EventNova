package com.example.eventnova;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        EventUtils.loadEventImage(holder.ivEventImage, event);
        holder.tvCategory.setText(event.getCategory().toUpperCase(Locale.getDefault()));
        holder.tvOrganizer.setText(event.getOrgName());
        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(EventUtils.formatEventDate(event.getDate()) + " | " + EventUtils.formatEventTime(event.getTime()));
        holder.tvLocation.setText(event.getLocation() + " | " + EventUtils.getEventPhase(event.getDate(), event.getTime()));
        holder.tvDescription.setText(event.getDescription());
        holder.tvPrice.setText(EventUtils.formatCurrency(event.getPrice()));
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEventImage;
        TextView tvCategory;
        TextView tvOrganizer;
        TextView tvTitle;
        TextView tvDate;
        TextView tvLocation;
        TextView tvDescription;
        TextView tvPrice;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvCategory = itemView.findViewById(R.id.tvEventCategory);
            tvOrganizer = itemView.findViewById(R.id.tvEventOrganizer);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            tvPrice = itemView.findViewById(R.id.tvEventPrice);
        }
    }
}
