package org.darkstorm.darkbot.minecraftbot.util;

import org.apache.http.HttpHost;

public final class ProxyData {
	private final HttpHost host;
	private final ProxyType type;

	public ProxyData(String hostName, int port, ProxyType type) {
		host = new HttpHost(hostName, port);
		this.type = type;
	}

	public String getHostName() {
		return host.getHostName();
	}

	public int getPort() {
		return host.getPort();
	}

	public HttpHost getHost() {
		return host;
	}

	public ProxyType getType() {
		return type;
	}

	public enum ProxyType {
		HTTP,
		SOCKS,
		HTTP_CONNECT
	}
}
