package org.tellstick.device;

import org.tellstick.JNA;
import org.tellstick.device.iface.DimmableDevice;

public class DimmableDeviceImpl extends SwitchableDeviceImpl implements DimmableDevice {
    public DimmableDeviceImpl(int deviceId) throws SupportedMethodsException {
        super(deviceId);
    }

    /**
     * Dims the lights.
     *
     * @throws TellstickException
     * @throws IllegalArguementException
     */
    @Override
    public void dim(int level) throws TellstickException {
        if (level < 0 || level > 255) {
            throw new IllegalArgumentException("Dim levels must be between 0 and 255.");
        }
        int status = JNA.CLibrary.INSTANCE.tdDim(this.getId(), level);
        if (status != 0) {
            throw new TellstickException(this, status);
        }
    }

    /**
     * Since Dimmers can be dimmed, we override the SwitchableDeviceImpl::isOn.
     * This checks if the device is turned on.
     *
     * @return true if device is on. false otherwise.
     */
    @Override
    public boolean isOn() {
        if (super.isOn() || (16 & this.getStatus()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return "Dimmer Device";
    }
}
