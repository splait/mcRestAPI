package br.com.rsinet.mobile_center.api.exception;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidCredentialsException(String arg0) {
		super(arg0);
	}

	public InvalidCredentialsException(Throwable arg0) {
		super(arg0);
	}
}
