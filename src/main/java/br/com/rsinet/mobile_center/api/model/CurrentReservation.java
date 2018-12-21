
package br.com.rsinet.mobile_center.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentReservation {

    @SerializedName("endTime")
    @Expose
    private Long endTime;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("startTime")
    @Expose
    private Long startTime;
    @SerializedName("status")
    @Expose
    private String status;

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
