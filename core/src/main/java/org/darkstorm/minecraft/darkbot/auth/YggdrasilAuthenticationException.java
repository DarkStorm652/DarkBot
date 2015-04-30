package org.darkstorm.minecraft.darkbot.auth;

@SuppressWarnings("serial")
public class YggdrasilAuthenticationException extends AuthenticationException {
	private final String error, errorMessage, errorCause;

	public YggdrasilAuthenticationException(String error, String errorMessage) {
		this(error, errorMessage, null);
	}

	public YggdrasilAuthenticationException(String error, String errorMessage, String errorCause) {
		super(error + ": " + errorMessage + (errorCause != null ? " (Caused by " + errorCause + ")" : ""));

		this.error = error;
		this.errorMessage = errorMessage;
		this.errorCause = errorCause;
	}

	public String getError() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorCause() {
		return errorCause;
	}
}
