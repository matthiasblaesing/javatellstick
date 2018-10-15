package org.tellstick.device;

import org.tellstick.JNA;

public class BellDevice extends TellstickDevice implements org.tellstick.device.iface.BellDevice {
    public BellDevice(int deviceId) throws SupportedMethodsException {
        super(deviceId);
    }

    @Override
    public void bell() throws TellstickException {
        int status = JNA.CLibrary.INSTANCE.tdBell(this.getId());
        if (status != 0) {
            throw new TellstickException(this, status);
        }
    }

    @Override
    public String getType() {
        return "Bell SwitchableDeviceImpl";
    }
}
