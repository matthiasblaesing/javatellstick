/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.tellstick.device;

import org.tellstick.device.iface.Device;
import org.tellstick.device.iface.TellstickEvent;
import org.tellstick.enums.ChangeType;
import org.tellstick.enums.Method;

/**
 * A event received by callback and resent to listeners.
 *
 * @author jarlebh
 * @since 1.5.0
 */
public class TellstickDeviceEvent implements TellstickEvent {

    private Device device;
    private Method method; // Look in JNA -TELLSTICK_TURNON and below
    private ChangeType changeType;
    private String data;
    private long timestamp;

    public TellstickDeviceEvent(Device device, Method method, ChangeType changeType, String data, long timestamp) {
        this.device = device;
        this.method = method;
        this.data = data;
        this.timestamp = timestamp;
        this.changeType = changeType;
    }

    public Device getDevice() {
        return device;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

}
