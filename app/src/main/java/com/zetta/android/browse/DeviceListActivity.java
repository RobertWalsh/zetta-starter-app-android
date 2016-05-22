package com.zetta.android.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zetta.android.ImageLoader;
import com.zetta.android.R;
import com.zetta.android.device.DeviceDetailsActivity;
import com.zetta.android.settings.ApiUrlFetcher;
import com.zetta.android.settings.SettingsActivity;

import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    private ApiUrlFetcher apiUrlFetcher;
    private DeviceListAdapter adapter;

    private RecyclerView recyclerView;
    private EmptyLoadingView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiUrlFetcher = ApiUrlFetcher.newInstance(this);

        setContentView(R.layout.device_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        emptyView = (EmptyLoadingView) findViewById(R.id.device_list_empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceListAdapter(new ImageLoader(), onDeviceClickListener);
    }

    private final DeviceListAdapter.OnDeviceClickListener onDeviceClickListener = new DeviceListAdapter.OnDeviceClickListener() {
        @Override
        public void onDeviceClick() {
            Intent intent = new Intent(DeviceListActivity.this, DeviceDetailsActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = apiUrlFetcher.getUrl();
        Log.d("xxx", "got url " + url);

        List<ListItem> items = MockZettaService.getListItems();
        adapter.updateAll(items);
        recyclerView.setAdapter(adapter);

        if (!items.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else if (items.isEmpty() && apiUrlFetcher.hasUrl()) {
            emptyView.setStateLoading(apiUrlFetcher.getUrl());
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setStateEmpty();
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
