package com.example.eventnova;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.IdRes;

import com.google.android.material.button.MaterialButton;

public final class TopNavHelper {

    private TopNavHelper() {
    }

    public static void setupUserNav(Activity activity, @IdRes int selectedId) {
        bindButton(activity, R.id.btnNavUserHome, UserDashboardActivity.class, selectedId);
        bindButton(activity, R.id.btnNavUserBookings, MyBookingsActivity.class, selectedId);
        bindButton(activity, R.id.btnNavUserProfile, UserProfileActivity.class, selectedId);
    }

    public static void setupOrgNav(Activity activity, @IdRes int selectedId) {
        bindButton(activity, R.id.btnNavOrgEvents, OrgDashboardActivity.class, selectedId);
        bindButton(activity, R.id.btnNavOrgBookings, OrgBookingsActivity.class, selectedId);
        bindButton(activity, R.id.btnNavOrgProfile, OrgProfileActivity.class, selectedId);
    }

    private static void bindButton(Activity activity, @IdRes int buttonId, Class<?> destination, @IdRes int selectedId) {
        MaterialButton button = activity.findViewById(buttonId);
        if (button == null) {
            return;
        }

        boolean selected = buttonId == selectedId;
        styleButton(button, selected, activity);

        button.setOnClickListener(v -> {
            if (selected) {
                return;
            }
            Intent intent = new Intent(activity, destination);
            activity.startActivity(intent);
        });
    }

    private static void styleButton(MaterialButton button, boolean selected, Activity activity) {
        int primary = activity.getColor(R.color.colorPrimary);
        int white = activity.getColor(R.color.white);
        int text = activity.getColor(R.color.colorTextPrimary);
        int stroke = activity.getColor(R.color.colorStroke);

        if (selected) {
            button.setBackgroundTintList(ColorStateList.valueOf(primary));
            button.setStrokeColor(ColorStateList.valueOf(primary));
            button.setTextColor(white);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(white));
            button.setStrokeColor(ColorStateList.valueOf(stroke));
            button.setTextColor(text);
        }
    }
}
