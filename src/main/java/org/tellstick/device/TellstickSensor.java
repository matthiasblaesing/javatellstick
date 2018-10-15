package org.tellstick.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.slf4j.LoggerFactory;
import org.tellstick.JNA;
import org.tellstick.device.iface.Device;
import org.tellstick.enums.DataType;
import org.tellstick.enums.DeviceType;

import com.sun.jna.Memory;

public class TellstickSensor implements Device {

    // private static final Logger logger = LoggerFactory.getLogger(TellstickSensor.class);
    private int sensorId;
    private String uuid;
    // private String data;
    private Map<DataType, String> data;
    private String protocol;
    private Date timeStamp;
    private String model;

    public TellstickSensor(int sensorId, String protocol, String model) {
        super();
        this.sensorId = sensorId;
        this.data = new HashMap<DataType, String>();
        this.protocol = protocol;
        this.model = model;
        this.uuid = TellstickSensor.createUUId(sensorId, model, protocol);
    }

    public static List<TellstickSensor> getAllSensors() {
        List<TellstickSensor> resultSensors = new ArrayList<TellstickSensor>();
        int result = JNA.CLibrary.TELLSTICK_SUCCESS;
        while (result == JNA.CLibrary.TELLSTICK_SUCCESS) {
            int varSize = 255;
            Memory protocol = new Memory(varSize);
            Memory model = new Memory(varSize);
            Memory value = new Memory(varSize);
            int id[] = new int[1];
            int dataTypes[] = new int[1];
            int timeStamp[] = new int[1];
            result = JNA.CLibrary.INSTANCE.tdSensor(protocol, varSize, model, varSize, id, dataTypes);
            DataType[] dTypes = DataType.getDataTypesById(dataTypes[0]);
            for (DataType type : dTypes) {
                TellstickSensor sensor = new TellstickSensor(id[0], protocol.getString(0), model.getString(0));
                JNA.CLibrary.INSTANCE.tdSensorValue(sensor.getProtocol(), sensor.getModel(), sensor.getId(),
                        type.getTellstickId(), value, varSize, timeStamp);
                sensor.setData(type, value.getString(0));
                long timeInMilli = timeStamp[0] * 1000L;
                sensor.setTimeStamp(new Date(timeInMilli));
                resultSensors.add(sensor);
            }
        }
        return resultSensors;
    }

    @Override
    public int getId() {
        return sensorId;
    }

    public String getData(DataType type) {
        return this.data.get(type);
    }

    public Map<DataType, String> getData() {
        return this.data;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getModel() {
        return model;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setData(DataType type, String data) {
        this.data.put(type, data);
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.SENSOR;
    }

    @Override
    public String toString() {
        return "TellstickSensor [sensorId=" + this.sensorId + ", "
                + (this.protocol != null ? new StringBuilder("protocol=").append(this.protocol).append(", ").toString()
                        : "")
                + (this.model != null ? new StringBuilder("model=").append(this.model).append(", ").toString() : "")
                + (this.timeStamp != null
                        ? new StringBuilder("timeStamp=").append(this.timeStamp).append(", ").toString()
                        : "")
                + (this.data != null ? new StringBuilder("data=").append(this.data).toString() : "") + "]";
    }

    @Override
    public String getName() {
        return String.valueOf(this.getModel()) + ":" + this.getId();
    }

    @Override
    public String getUUId() {
        return this.uuid;
    }

    public static String createUUId(int id, String model, String protocol) {
        return String.valueOf(id) + "_" + model + "_" + protocol;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;
        if (obj instanceof TellstickSensor) {
            res = this.getUUId().equals(((TellstickSensor) obj).getUUId());
        }
        return res;
    }

    @Override
    public int hashCode() {
        return this.getUUId().hashCode();
    }

}
