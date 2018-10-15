/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.tellstick.device;

import org.tellstick.device.iface.TellstickEvent;
import org.tellstick.enums.DataType;

/**
 * A sensor event from tellstick.
 *
 * @author jarlebh
 * @since 1.5.0
 */
public class TellstickSensorEvent implements TellstickEvent {
    private int sensorId;
    private String data;
    private DataType method;
    private String protocol;
    private String model;
    private long timestamp;

    public TellstickSensorEvent(int sensorId, String data, DataType method, String protocol, String model,
            long timeStamp) {
        super();
        this.sensorId = sensorId;
        this.data = data;
        this.method = method;
        this.protocol = protocol;
        this.model = model;
        this.timestamp = timeStamp;
    }

    public int getSensorId() {
        return sensorId;
    }

    @Override
    public String getData() {
        return data;
    }

    public DataType getDataType() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getModel() {
        return model;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "TellstickSensorEvent [sensorId=" + this.sensorId + ", model=" + this.model + ", protocol="
                + this.protocol + ", data=" + this.data + ", timestamp=" + this.timestamp + "]";
    }

}
