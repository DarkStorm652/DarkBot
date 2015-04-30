package org.darkstorm.minecraft.darkbot.connection;

import java.io.IOException;

import javax.crypto.SecretKey;

public interface ConnectionHandler {
	public void sendPacket(WriteablePacket packet);

	public void process();

	public boolean supportsEncryption();

	public SecretKey getSharedKey() throws UnsupportedOperationException;

	public void setSharedKey(SecretKey sharedKey) throws UnsupportedOperationException;

	public void enableEncryption() throws UnsupportedOperationException;

	public void enableDecryption() throws UnsupportedOperationException;

	public boolean isEncrypting();

	public boolean isDecrypting();

	public boolean supportsPausing();

	public void pauseReading() throws UnsupportedOperationException;

	public void pauseWriting() throws UnsupportedOperationException;

	public void resumeReading() throws UnsupportedOperationException;

	public void resumeWriting() throws UnsupportedOperationException;

	public boolean isReadingPaused();

	public boolean isWritingPaused();

	public void connect() throws IOException;

	public void disconnect(String reason);

	public boolean isConnected();

	public String getServer();

	public int getPort();

	public Protocol<?> getProtocol();
}
