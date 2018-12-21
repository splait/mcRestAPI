
package br.com.rsinet.mobile_center.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceContent {

	@SerializedName("apiLevel")
	@Expose
	private String apiLevel;
	@SerializedName("connected")
	@Expose
	private Boolean connected;
	@SerializedName("currentReservation")
	@Expose
	private CurrentReservation currentReservation;
	@SerializedName("deviceHostingType")
	@Expose
	private String deviceHostingType;
	@SerializedName("deviceName")
	@Expose
	private String deviceName;
	@SerializedName("deviceType")
	@Expose
	private String deviceType;
	@SerializedName("formFactor")
	@Expose
	private String formFactor;
	@SerializedName("nickName")
	@Expose
	private String nickName;
	@SerializedName("platformName")
	@Expose
	private String platformName;
	@SerializedName("platformVersion")
	@Expose
	private String platformVersion;
	@SerializedName("udid")
	@Expose
	private String udid;

	public String getApiLevel() {
		return apiLevel;
	}

	public void setApiLevel(String apiLevel) {
		this.apiLevel = apiLevel;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public CurrentReservation getCurrentReservation() {
		return currentReservation;
	}

	public void setCurrentReservation(CurrentReservation currentReservation) {
		this.currentReservation = currentReservation;
	}

	public String getDeviceHostingType() {
		return deviceHostingType;
	}

	public void setDeviceHostingType(String deviceHostingType) {
		this.deviceHostingType = deviceHostingType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getFormFactor() {
		return formFactor;
	}

	public void setFormFactor(String formFactor) {
		this.formFactor = formFactor;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public boolean isFree() {
		return "Free".equals(this.getCurrentReservation().getStatus());
	}
}
