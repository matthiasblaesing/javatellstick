/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.tellstick.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tellstick.JNA;
import org.tellstick.device.iface.DeviceChangeListener;
import org.tellstick.device.iface.SensorListener;
import org.tellstick.enums.ChangeType;
import org.tellstick.enums.DataType;
import org.tellstick.enums.Method;

import com.sun.jna.Pointer;

/**
 * A callback listener to telldus that distributes to other listeners.
 *
 * @author jarlebh
 * @since 1.5.0
 */
public class TellstickEventHandler {

    private static final Logger logger = Logger.getLogger(TellstickEventHandler.class.getName());
    List<TellstickDevice> list;
    List<EventListener> changeListeners = new ArrayList<EventListener>();

    private int handleSensor;
    private int handleDeviceChange;
    private JNA.CLibrary.TDDeviceChangeEvent deviceChangeHandler;
    private int handleDeviceEvent;
    private JNA.CLibrary.TDDeviceEvent deviceEventHandler;
    private JNA.CLibrary.TDSensorEvent sensorEventHandler;

    public TellstickEventHandler(List<TellstickDevice> deviceList) {
        this.list = java.util.Collections.synchronizedList(deviceList);
        this.setupListeners();
    }

    public void setDeviceList(List<TellstickDevice> deviceList) {
        list = java.util.Collections.synchronizedList(deviceList);
    }

    public void addListener(EventListener listener) {
        this.changeListeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        this.changeListeners.remove(listener);
    }

    public void remove() {
        JNA.CLibrary.INSTANCE.tdUnregisterCallback(handleDeviceEvent);
        JNA.CLibrary.INSTANCE.tdUnregisterCallback(handleDeviceChange);
        JNA.CLibrary.INSTANCE.tdUnregisterCallback(handleSensor);
        changeListeners.clear();
        sensorEventHandler = null;
        deviceChangeHandler = null;
        deviceEventHandler = null;
    }

    public List<EventListener> getAllListeners() {
        return changeListeners;
    }

    private void notifyListeners(TellstickDevice ts, Method m, ChangeType changeType, String dataStr) {
        for (EventListener changeListener : changeListeners) {
            if (changeListener instanceof DeviceChangeListener) {
                ((DeviceChangeListener) changeListener)
                        .onRequest(new TellstickDeviceEvent(ts, m, changeType, dataStr, System.currentTimeMillis()));
            }
        }
    }

    private void notifySensorListeners(int deviceId, String protocol, String model, DataType type, String dataStr,
            long timestamp) {
        for (EventListener changeListener : changeListeners) {
            if (changeListener instanceof SensorListener) {
                ((SensorListener) changeListener)
                        .onRequest(new TellstickSensorEvent(deviceId, dataStr, type, protocol, model, timestamp));
            }
        }
    }

    public void setupListeners() {
        deviceEventHandler = new JNA.CLibrary.TDDeviceEvent() {
            @Override
            public void invoke(int deviceId, int method, Pointer data, int callbackId, Pointer context)
                    throws SupportedMethodsException {

                try {
                    TellstickDevice ts = TellstickDevice.getDevice(deviceId);

                    int idx = Collections.binarySearch(list, ts);
                    if (idx > -1) {
                        list.set(idx, ts);
                    }
                    Method m = Method.getMethodById(method);
                    String dataStr = null;
                    if (m == Method.DIM) {
                        dataStr = data.getString(0);
                    }
                    notifyListeners(ts, m, ChangeType.UPDATED, dataStr);
                    // The device is not supported.
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed in TDDeviceEvent", e);
                }

            }

        };

        handleDeviceEvent = JNA.CLibrary.INSTANCE.tdRegisterDeviceEvent(deviceEventHandler, null);

        deviceChangeHandler = new JNA.CLibrary.TDDeviceChangeEvent() {
            @Override
            public void invoke(int deviceId, int changeEvent, int changeType, int callbackId, Pointer context)
                    throws SupportedMethodsException {
                try {

                    TellstickDevice ts = null;

                    if (changeEvent == JNA.CLibrary.TELLSTICK_DEVICE_CHANGED
                            || changeEvent == JNA.CLibrary.TELLSTICK_DEVICE_STATE_CHANGED) {
                        ts = TellstickDevice.getDevice(deviceId);
                        int idx = Collections.binarySearch(list, ts);
                        if (idx > -1) {
                            list.set(idx, ts);
                        }
                    }

                    if (changeEvent == JNA.CLibrary.TELLSTICK_DEVICE_ADDED) {
                        ts = TellstickDevice.getDevice(deviceId);
                        list.add(ts);
                    }
                    if (changeEvent == JNA.CLibrary.TELLSTICK_DEVICE_ADDED) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId() == deviceId) {
                                ts = list.remove(i);
                                return;
                            }
                        }
                    }
                    notifyListeners(ts, null, ChangeType.getChangeTypeById(changeEvent), null);
                    // The device is not supported.
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed in TDDeviceChangeEvent", e);

                }
            }
        };
        handleDeviceChange = JNA.CLibrary.INSTANCE.tdRegisterDeviceChangeEvent(deviceChangeHandler, null);
        sensorEventHandler = new JNA.CLibrary.TDSensorEvent() {
            @Override
            public void invoke(String protocol, String model, int deviceId, int dataType, Pointer value, int timeStamp,
                    int callbackId, Pointer context) throws SupportedMethodsException {
                try {
                    DataType[] m = DataType.getDataTypesById(dataType);
                    long eventtime = timeStamp * 1000L;
                    notifySensorListeners(deviceId, protocol, model, m[0], value.getString(0L), eventtime);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed in TDSensorEvent", e);
                }
            }
        };
        handleSensor = JNA.CLibrary.INSTANCE.tdRegisterSensorEvent(sensorEventHandler, null);

    }

}
