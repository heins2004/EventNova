package com.example.eventnova;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.ImageView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class EventUtils {

    private static final SimpleDateFormat STORAGE_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat STORAGE_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_TIME = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    private EventUtils() {
    }

    public static String formatCurrency(double amount) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return currency.format(amount);
    }

    public static Date parseEventDate(String date) {
        try {
            return STORAGE_DATE.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseEventDateTime(String date, String time) {
        try {
            return STORAGE_DATE_TIME.parse(date + " " + time);
        } catch (ParseException e) {
            return parseEventDate(date);
        }
    }

    public static String formatEventDate(String date) {
        Date parsed = parseEventDate(date);
        return parsed == null ? date : DISPLAY_DATE.format(parsed);
    }

    public static String formatEventTime(String time) {
        try {
            Date parsed = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time);
            return parsed == null ? time : DISPLAY_TIME.format(parsed);
        } catch (ParseException e) {
            return time;
        }
    }

    public static boolean isPastEvent(String date, String time) {
        Date eventDate = parseEventDateTime(date, time);
        return eventDate != null && eventDate.before(new Date());
    }

    public static boolean isToday(String date) {
        Date parsed = parseEventDate(date);
        if (parsed == null) {
            return false;
        }
        Calendar eventCalendar = Calendar.getInstance();
        eventCalendar.setTime(parsed);
        Calendar today = Calendar.getInstance();
        return eventCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && eventCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    public static String getEventPhase(String date, String time) {
        if (isPastEvent(date, time)) {
            return "Completed";
        }
        if (isToday(date)) {
            return "Happening Today";
        }
        return "Booking Open";
    }

    public static long getEventStartMillis(String date, String time) {
        Date eventDate = parseEventDateTime(date, time);
        return eventDate == null ? System.currentTimeMillis() : eventDate.getTime();
    }

    public static long getEventEndMillis(String date, String time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(getEventStartMillis(date, time));
        calendar.add(Calendar.HOUR_OF_DAY, 3);
        return calendar.getTimeInMillis();
    }

    public static Intent createCalendarIntent(Event event) {
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                event.getDescription() + "\nOrganizer: " + event.getOrgName() + "\nCategory: " + event.getCategory());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getEventStartMillis(event.getDate(), event.getTime()));
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getEventEndMillis(event.getDate(), event.getTime()));
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        return intent;
    }

    public static Intent createPreferredCalendarIntent(Context context, Event event) {
        Intent preferredIntent = createCalendarIntent(event);
        preferredIntent.setPackage("com.google.android.calendar");
        PackageManager packageManager = context.getPackageManager();
        if (preferredIntent.resolveActivity(packageManager) != null) {
            return preferredIntent;
        }
        // Don't lock to a package — let Android pick any calendar
        return createCalendarIntent(event);
    }

    public static void loadEventImage(ImageView imageView, Event event) {
        if (event == null) {
            imageView.setImageResource(R.drawable.event_other_banner);
            return;
        }

        String imageUri = event.getImage();
        if (imageUri != null && !imageUri.trim().isEmpty()) {
            try {
                imageView.setImageURI(Uri.parse(imageUri));
                if (imageView.getDrawable() != null) {
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        imageView.setImageResource(getEventImageRes(event.getCategory()));
    }

    public static int getEventImageRes(String category) {
        if (category == null) {
            return R.drawable.event_other_banner;
        }

        switch (category.trim().toLowerCase(Locale.ROOT)) {
            case "music":
                return R.drawable.event_music_banner;
            case "sports":
                return R.drawable.event_sports_banner;
            case "tech":
            case "education":
                return R.drawable.event_tech_banner;
            case "art":
                return R.drawable.event_art_banner;
            case "food":
                return R.drawable.event_food_banner;
            default:
                return R.drawable.event_other_banner;
        }
    }
}
