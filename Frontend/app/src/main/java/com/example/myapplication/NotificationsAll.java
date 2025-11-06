package com.example.myapplication;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAll extends Fragment {

    private ListView listView;
    private List<String> notifications = new ArrayList<>();
    private List<String> category_notifications = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Spinner categorySpinner;
    private SearchView search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications_all, container, false);
        listView = v.findViewById(R.id.notification_list);
        categorySpinner = v.findViewById(R.id.category_spinner);
        search = v.findViewById(R.id.notification_search);

        // Get notifications from WebSocketManager
        notifications.addAll(WebSocketManager.getInstance().getNotificationHistory());
        category_notifications.addAll(notifications);


        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, category_notifications);
        listView.setAdapter(adapter);

        String[] filters = {"All", "Articles", "Appointments"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, filters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                category_notifications.clear();

                for (String notif : notifications) {
                    if (selected.equals("All")) {
                        category_notifications.add(notif);
                    } else if (selected.equals("Articles") && notif.contains("Article")) {
                        category_notifications.add(notif);
                    } else if (selected.equals("Appointments") && notif.contains("APPOINTMENT_BOOKED")) {
                        category_notifications.add(notif);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String selected = (String) categorySpinner.getSelectedItem();
                category_notifications.clear();

                for (String notif : notifications) {
                    boolean matchesCategory =
                            selected.equals("All") ||
                                    (selected.equals("Articles") && notif.contains("Article")) ||
                                    (selected.equals("Appointments") && notif.contains("APPOINTMENT_BOOKED"));

                    if (matchesCategory && notif.toLowerCase().contains(query.toLowerCase())) {
                        category_notifications.add(notif);
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String selected = (String) categorySpinner.getSelectedItem();
                category_notifications.clear();

                for (String notif : notifications) {
                    boolean matchesCategory =
                            selected.equals("All") ||
                                    (selected.equals("Articles") && notif.contains("Article")) ||
                                    (selected.equals("Appointments") && notif.contains("APPOINTMENT_BOOKED"));

                    if (matchesCategory && notif.toLowerCase().contains(newText.toLowerCase())) {
                        category_notifications.add(notif);
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            }
        });



        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            String notifToDelete = category_notifications.get(position);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Notification")
                    .setMessage("Are you sure you want to delete this notification?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        category_notifications.remove(notifToDelete);
                        notifications.remove(notifToDelete);
                        WebSocketManager.getInstance().removeNotification(notifToDelete);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Notification deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        return v;
    }

}
