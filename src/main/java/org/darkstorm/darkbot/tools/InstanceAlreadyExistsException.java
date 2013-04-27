package org.darkstorm.darkbot.tools;

/**
 * Thrown to indicate that an object that only permits one instance was already
 * instantiated and another object attempted to create an instance of it.
 * 
 * @created Jul 15, 2010 at 11:45:17 PM
 * @author DarkStorm
 */
@SuppressWarnings("serial")
public class InstanceAlreadyExistsException extends RuntimeException {

	public InstanceAlreadyExistsException() {
		super();
	}

	public InstanceAlreadyExistsException(String message) {
		super(message);
	}

	public InstanceAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public InstanceAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
