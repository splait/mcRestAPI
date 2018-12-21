
package br.com.rsinet.mobile_center.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("counter")
    @Expose
    private Long counter;
    @SerializedName("dateTime")
    @Expose
    private Long dateTime;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("iconUrl")
    @Expose
    private String iconUrl;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("comment")
    @Expose
    private Object comment;
    @SerializedName("deviceFamily")
    @Expose
    private Object deviceFamily;
    @SerializedName("minimumOsVersion")
    @Expose
    private String minimumOsVersion;
    @SerializedName("instrumented")
    @Expose
    private Boolean instrumented;
    @SerializedName("instrumentationStatus")
    @Expose
    private String instrumentationStatus;
    @SerializedName("instrumentationFailureReason")
    @Expose
    private Object instrumentationFailureReason;
    @SerializedName("urlScheme")
    @Expose
    private Object urlScheme;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("launchActivity")
    @Expose
    private String launchActivity;
    @SerializedName("appPackage")
    @Expose
    private String appPackage;
    @SerializedName("appActivity")
    @Expose
    private String appActivity;
    @SerializedName("bundleId")
    @Expose
    private Object bundleId;
    @SerializedName("appUdid")
    @Expose
    private String appUdid;
    @SerializedName("provisionedDevices")
    @Expose
    private Object provisionedDevices;
    @SerializedName("appBuildVersion")
    @Expose
    private String appBuildVersion;
    @SerializedName("appVersion")
    @Expose
    private String appVersion;
    @SerializedName("workspaces")
    @Expose
    private Object workspaces;
    @SerializedName("instrumentedApplicationExist")
    @Expose
    private Boolean instrumentedApplicationExist;
    @SerializedName("applicationExist")
    @Expose
    private Boolean applicationExist;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getComment() {
        return comment;
    }

    public void setComment(Object comment) {
        this.comment = comment;
    }

    public Object getDeviceFamily() {
        return deviceFamily;
    }

    public void setDeviceFamily(Object deviceFamily) {
        this.deviceFamily = deviceFamily;
    }

    public String getMinimumOsVersion() {
        return minimumOsVersion;
    }

    public void setMinimumOsVersion(String minimumOsVersion) {
        this.minimumOsVersion = minimumOsVersion;
    }

    public Boolean getInstrumented() {
        return instrumented;
    }

    public void setInstrumented(Boolean instrumented) {
        this.instrumented = instrumented;
    }

    public String getInstrumentationStatus() {
        return instrumentationStatus;
    }

    public void setInstrumentationStatus(String instrumentationStatus) {
        this.instrumentationStatus = instrumentationStatus;
    }

    public Object getInstrumentationFailureReason() {
        return instrumentationFailureReason;
    }

    public void setInstrumentationFailureReason(Object instrumentationFailureReason) {
        this.instrumentationFailureReason = instrumentationFailureReason;
    }

    public Object getUrlScheme() {
        return urlScheme;
    }

    public void setUrlScheme(Object urlScheme) {
        this.urlScheme = urlScheme;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLaunchActivity() {
        return launchActivity;
    }

    public void setLaunchActivity(String launchActivity) {
        this.launchActivity = launchActivity;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }

    public void setAppActivity(String appActivity) {
        this.appActivity = appActivity;
    }

    public Object getBundleId() {
        return bundleId;
    }

    public void setBundleId(Object bundleId) {
        this.bundleId = bundleId;
    }

    public String getAppUdid() {
        return appUdid;
    }

    public void setAppUdid(String appUdid) {
        this.appUdid = appUdid;
    }

    public Object getProvisionedDevices() {
        return provisionedDevices;
    }

    public void setProvisionedDevices(Object provisionedDevices) {
        this.provisionedDevices = provisionedDevices;
    }

    public String getAppBuildVersion() {
        return appBuildVersion;
    }

    public void setAppBuildVersion(String appBuildVersion) {
        this.appBuildVersion = appBuildVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Object getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(Object workspaces) {
        this.workspaces = workspaces;
    }

    public Boolean getInstrumentedApplicationExist() {
        return instrumentedApplicationExist;
    }

    public void setInstrumentedApplicationExist(Boolean instrumentedApplicationExist) {
        this.instrumentedApplicationExist = instrumentedApplicationExist;
    }

    public Boolean getApplicationExist() {
        return applicationExist;
    }

    public void setApplicationExist(Boolean applicationExist) {
        this.applicationExist = applicationExist;
    }

}
