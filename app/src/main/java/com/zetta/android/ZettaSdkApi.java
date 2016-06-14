package com.zetta.android;

import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKDeviceId;
import com.apigee.zettakit.ZIKException;
import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.ZIKStream;
import com.apigee.zettakit.ZIKStreamEntry;
import com.apigee.zettakit.interfaces.ZIKStreamListener;
import com.novoda.notils.exception.DeveloperError;
import com.novoda.notils.logger.simple.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * This singleton is not thread safe
 */
public enum ZettaSdkApi {
    INSTANCE;

    @Nullable
    private String rootUrl;
    @Nullable
    private ZIKRoot zikRoot;
    @Nullable
    private List<ZIKServer> zikServers;
    @Nullable
    private List<ZIKDevice> zikDevices;
    @Nullable
    private List<ZIKStream> deviceAllStreams;

    public void registerRoot(String url) {
        this.rootUrl = url;
        getRoot();
    }

    public ZIKRoot getRoot() {
        if (rootUrl == null) {
            throw new DeveloperError("Call registerRoot(url) before getRoot!");
        }
        final ZIKSession session = ZIKSession.getSharedSession();
        this.zikRoot = session.getRootSync(rootUrl);
        return zikRoot;
    }

    public List<ZIKServer> getServers() {
        if (zikRoot == null || !zikRoot.getSelfLink().getHref().equals(rootUrl)) {
            getRoot();
        }
        ZIKSession session = ZIKSession.getSharedSession();
        this.zikServers = session.getServersSync(zikRoot);
        return zikServers;
    }

    public List<ZIKDevice> getDevices(ZIKServer zikServer) {
        if (zikServers == null) {
            throw new DeveloperError("Ensure getServers is called and completes successfully first!");
        }
        ZIKSession session = ZIKSession.getSharedSession();
        this.zikDevices = session.getDevicesSync(zikServer);
        return zikDevices;
    }

    public ZIKDevice getDevice(ZIKDeviceId zikDeviceId) {
        if (zikDevices == null) {
            getDevices(getServerContaining(zikDeviceId));
        }
        for (ZIKDevice device : zikDevices) {
            if (zikDeviceId.equals(device.getDeviceId())) {
                return device;
            }
        }
        getDevices(getServerContaining(zikDeviceId));
        for (ZIKDevice device : zikDevices) {
            if (zikDeviceId.equals(device.getDeviceId())) {
                return device;
            }
        }
        throw new DeveloperError("A device should always be found, what did you do?");
    }

    public ZIKServer getServerContaining(ZIKDeviceId zikDeviceId) {
        if (zikServers == null) {
            throw new DeveloperError("Ensure getServers is called and completes successfully first!");
        }
        for (ZIKServer zikServer : zikServers) {
            for (ZIKDevice device : zikServer.getDevices()) {
                if (zikDeviceId.equals(device.getDeviceId())) {
                    return zikServer;
                }
            }
        }
        throw new DeveloperError("A server should always be found, what did you do?");
    }

    public void startMonitoringStreamsFor(final ZIKDeviceId deviceId, final ZikStreamEntryListener listener) {
        ZIKDevice device = getDevice(deviceId);
        deviceAllStreams = device.getAllStreams();
        for (ZIKStream zikStream : deviceAllStreams) {
            ZIKStream stream = device.stream(zikStream.getTitle());
            if (stream == null) {
                return;
            }
            stream.resume();
            stream.setStreamListener(new ZIKStreamListener() {
                @Override
                public void onUpdate(Object object) {
                    ZIKStreamEntry streamEntry = (ZIKStreamEntry) object;
                    listener.updateFor(deviceId, streamEntry);
                }

                @Override
                public void onOpen() {
                    // not used
                }

                @Override
                public void onError(IOException exception, Response response) {
                    Log.e(exception, "error");
                }

                @Override
                public void onPong() {
                    // not used
                }

                @Override
                public void onClose() {
                    // not used
                }
            });
        }
    }

    public void stopMonitoringStreams() {
        if (deviceAllStreams == null) {
            Log.e("Attempted to stop when you hadn't started");
            return;
        }
        for (ZIKStream stream : deviceAllStreams) {
            stream.close();
        }
    }

    public interface ZikStreamEntryListener {
        void updateFor(ZIKDeviceId deviceId, ZIKStreamEntry entry);
    }
}