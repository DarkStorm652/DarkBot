package org.darkstorm.darkbot.ircbot.irc.parsing;


@SuppressWarnings("serial")
public class LineParseException extends RuntimeException {
	private LineParser lineParser;

	public LineParseException(LineParser lineParser) {
		this.lineParser = lineParser;
	}

	public LineParseException(LineParser lineParser, String message) {
		super(message);
		this.lineParser = lineParser;
	}

	public LineParseException(LineParser lineParser, Throwable cause) {
		super(cause);
		this.lineParser = lineParser;
	}

	public LineParseException(LineParser lineParser, String message,
			Throwable cause) {
		super(message, cause);
		this.lineParser = lineParser;
	}

	public LineParser getLineParser() {
		return lineParser;
	}

}
