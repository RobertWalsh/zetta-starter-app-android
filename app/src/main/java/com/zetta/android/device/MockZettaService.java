package com.zetta.android.device;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class MockZettaService {

    /**
     * TODO note this structure is only the UI structure and it is not what I expect to be return from the 'zetta library'
     */
    public static void getDetails(final Callback callback) {
        final List<ListItem> items = new ArrayList<>();

        items.add(new ListItem.HeaderListItem("Actions"));
        items.add(new ListItem.ActionListItem("color", "set-color"));
        items.add(new ListItem.ActionListItem("brightness", "set-brightness"));
        items.add(new ListItem.ActionListItem("blink", "set-blinker"));
        items.add(new ListItem.ActionListItem("turn-off", "turn-off"));
        items.add(new ListItem.HeaderListItem("Streams"));
        items.add(new ListItem.StreamListItem("state", "on"));
        items.add(new ListItem.HeaderListItem("Properties"));
        items.add(new ListItem.PropertyListItem("type", "light"));
        items.add(new ListItem.PropertyListItem("style", ""));
        items.add(new ListItem.PropertyListItem("brightness", ""));
        items.add(new ListItem.PropertyListItem("name", "Porch Light"));
        items.add(new ListItem.PropertyListItem("id", "5113a9d2-0dfa-4061-8034-8cde5bbb41b2"));
        items.add(new ListItem.PropertyListItem("state", "on"));
        items.add(new ListItem.PropertyListItem("color", ""));
        items.add(new ListItem.PropertyListItem("blink", ""));
        items.add(new ListItem.HeaderListItem("Events"));
        items.add(new ListItem.EventsListItem("View Events (42)"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(TimeUnit.SECONDS.toMillis(1));

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.on("Porch Light", "neworleans", items);
                    }
                });
            }
        }).start();
    }

    interface Callback {
        void on(String deviceName, String serverName, List<ListItem> listItems);
    }
}
