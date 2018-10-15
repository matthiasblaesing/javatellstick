package org.tellstick.device;

import java.util.ArrayList;
import java.util.List;

import org.tellstick.JNA;
import org.tellstick.device.iface.Controller;
import org.tellstick.enums.ControllerType;

import com.sun.jna.Memory;

public class TellstickController implements Controller {
    private ControllerType type;
    private int id;
    private String name;
    private boolean online = false;

    public TellstickController(ControllerType type, int id, String name, boolean online) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.online = online;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public ControllerType getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static List<TellstickController> getControllers() {
        ArrayList<TellstickController> controllers = new ArrayList<TellstickController>();
        int[] contrId = new int[] { 1 };
        int[] contrType = new int[] { 1 };
        int[] avail = new int[] { 1 };
        int NAME_LEN = 255;
        Memory name = new Memory(NAME_LEN);
        int status = 0;
        while (status == 0) {
            status = JNA.CLibrary.INSTANCE.tdController(contrId, contrType, name, NAME_LEN, avail);
            if (status != 0) {
                continue;
            }
            String strName = name.getString(0L);
            if (strName.isEmpty()) {
                strName = "UnNamed";
            }
            TellstickController cntrl = new TellstickController(ControllerType.findDeviceType(contrType[0]), contrId[0],
                    strName, avail[0] == 1);
            controllers.add(cntrl);
        }
        return controllers;
    }

    @Override
    public String toString() {
        return "TellstickController ["
                + (this.type != null ? new StringBuilder("type=").append(this.type).append(", ").toString() : "")
                + "id=" + this.id + ", "
                + (this.name != null ? new StringBuilder("name=").append(this.name).append(", ").toString() : "")
                + "online=" + this.online + "]";
    }
}
