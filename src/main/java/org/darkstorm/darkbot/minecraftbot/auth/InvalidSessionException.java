package org.darkstorm.darkbot.minecraftbot.auth;

@SuppressWarnings("serial")
public class InvalidSessionException extends RuntimeException {

	public InvalidSessionException() {
	}

	public InvalidSessionException(String message) {
		super(message);
	}

	public InvalidSessionException(Throwable cause) {
		super(cause);
	}

	public InvalidSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
