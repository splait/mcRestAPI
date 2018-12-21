
package br.com.rsinet.mobile_center.api.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileCenterApps {

    @SerializedName("messageCode")
    @Expose
    private Long messageCode;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("error")
    @Expose
    private Boolean error;

    public Long getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(Long messageCode) {
        this.messageCode = messageCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

}
