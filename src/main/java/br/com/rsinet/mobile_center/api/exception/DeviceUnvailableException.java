package br.com.rsinet.mobile_center.api.exception;

public class DeviceUnvailableException extends RuntimeException {

	public DeviceUnvailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DeviceUnvailableException(String arg0) {
		super(arg0);
	}

	public DeviceUnvailableException(Throwable arg0) {
		super(arg0);
	}
}
