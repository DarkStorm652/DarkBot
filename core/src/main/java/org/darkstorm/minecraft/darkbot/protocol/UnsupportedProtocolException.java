package org.darkstorm.minecraft.darkbot.protocol;

@SuppressWarnings("serial")
public class UnsupportedProtocolException extends Exception {

	public UnsupportedProtocolException() {
	}

	public UnsupportedProtocolException(String message) {
		super(message);
	}

	public UnsupportedProtocolException(Throwable cause) {
		super(cause);
	}

	public UnsupportedProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
