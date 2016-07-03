package com.zetta.android.browse;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKDeviceId;
import com.apigee.zettakit.ZIKServer;
import com.zetta.android.ListItem;
import com.zetta.android.ZettaDeviceId;
import com.zetta.android.ZettaSdkApi;
import com.zetta.android.ZettaStyle;
import com.zetta.android.browse.DeviceListService.DeviceListItemListener;
import com.zetta.android.device.actions.ActionListItemParser;

import java.util.List;

class DeviceListSdkService {

    @NonNull private final ZettaSdkApi zettaSdkApi;
    @NonNull private final DeviceList.Parser deviceListParser;

    public DeviceListSdkService() {
        zettaSdkApi = ZettaSdkApi.INSTANCE;
        deviceListParser = new DeviceList.Parser(new ZettaStyle.Parser(), new ActionListItemParser());
    }

    @NonNull
    public List<ListItem> getListItems(@NonNull String url) {
        zettaSdkApi.registerRoot(url);
        List<ZIKServer> zikServers = zettaSdkApi.getServers();
        return deviceListParser.createListItems(zikServers);
    }

    @NonNull
    public List<ListItem> getQuickActions(@NonNull ZettaDeviceId deviceId) {
        ZIKDeviceId zikDeviceId = new ZIKDeviceId(deviceId.getUuid().toString());
        ZIKServer zikServer = zettaSdkApi.getServerContaining(zikDeviceId);
        ZIKDevice zikDevice = zettaSdkApi.getFullDevice(zikDeviceId);
        return deviceListParser.createQuickActions(zikServer, zikDevice);
    }

    public void startMonitorDeviceUpdates(@NonNull String url, @NonNull final DeviceListItemListener listener) {
        zettaSdkApi.registerRoot(url);
        zettaSdkApi.startMonitoringAllServerAllDevices(new ZettaSdkApi.ZikDeviceListener() {
            @Override
            public void updateFor(@NonNull ZIKServer server, @NonNull ZIKDevice device) {
                DeviceListItem listItem = deviceListParser.createDeviceListItem(server, device);
                listener.onUpdated(listItem);
            }
        });
    }

    public void stopMonitoringStreamedUpdates() {
        zettaSdkApi.stopMonitoringAllServerDeviceStreams();
    }

}
