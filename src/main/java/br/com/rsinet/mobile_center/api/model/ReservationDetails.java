
package br.com.rsinet.mobile_center.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReservationDetails {

    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("releaseOnJobCompletion")
    @Expose
    private Boolean releaseOnJobCompletion;
    @SerializedName("deviceCapabilities")
    @Expose
    private DeviceCapabilities deviceCapabilities;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getReleaseOnJobCompletion() {
        return releaseOnJobCompletion;
    }

    public void setReleaseOnJobCompletion(Boolean releaseOnJobCompletion) {
        this.releaseOnJobCompletion = releaseOnJobCompletion;
    }

    public DeviceCapabilities getDeviceCapabilities() {
        return deviceCapabilities;
    }

    public void setDeviceCapabilities(DeviceCapabilities deviceCapabilities) {
        this.deviceCapabilities = deviceCapabilities;
    }

}
