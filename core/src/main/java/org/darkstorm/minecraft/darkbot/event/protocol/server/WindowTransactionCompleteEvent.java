package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class WindowTransactionCompleteEvent extends WindowEvent {
	private final int transactionId;
	private final boolean accepted;

	public WindowTransactionCompleteEvent(int windowId, int transactionId, boolean accepted) {
		super(windowId);

		this.transactionId = transactionId;
		this.accepted = accepted;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public boolean isAccepted() {
		return accepted;
	}
}
