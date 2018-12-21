
package br.com.rsinet.mobile_center.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceCapabilities {

    @SerializedName("deviceName")
    @Expose
    private String deviceName;
    @SerializedName("udid")
    @Expose
    private String udid;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

}
