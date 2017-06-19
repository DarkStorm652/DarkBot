package org.darkstorm.minecraft.darkbot.connection;

import java.io.*;
import java.net.*;
import java.security.Principal;

import org.apache.http.*;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.ProxyClient;
import org.apache.http.params.HttpConnectionParams;
import org.darkstorm.minecraft.darkbot.connection.ProxyData.ProxyType;

public class Connection {
	private static final ProxyClient proxyClient = createClient();
	private static final Credentials proxyCredentials = createCredentials();

	private String host;
	private int port;
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private ProxyData proxy;

	public Connection(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Connection(String host, int port, ProxyData proxy) {
		if(proxy.getType().equals(ProxyType.HTTP))
			throw new IllegalArgumentException("HTTP proxies are not usable for this purpose.");
		this.host = host;
		this.port = port;
		this.proxy = proxy;
	}

	public Connection(Socket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket must be open");
		try {
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
			port = socket.getPort();
			this.socket = socket;
			createStreams();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	private static ProxyClient createClient() {
		ProxyClient client = new ProxyClient();

		HttpConnectionParams.setConnectionTimeout(client.getParams(), 3000);
		HttpConnectionParams.setSoTimeout(client.getParams(), 3000);

		return client;
	}

	private static Credentials createCredentials() {
		return new Credentials() {

			@Override
			public Principal getUserPrincipal() {
				return null;
			}

			@Override
			public String getPassword() {
				return null;
			}
		};
	}

	public void connect() throws IOException {
		if(isConnected())
			return;
		if(proxy != null) {
			switch(proxy.getType()) {
			case SOCKS:
				socket = new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHostName(), proxy.getPort())));
				break;
			case HTTP_CONNECT:
				try {
					socket = proxyClient.tunnel(proxy.getHost(), new HttpHost(host, port), proxyCredentials);
				} catch(HttpException exception) {
					throw new IOException(exception);
				}
				break;
			default:
			}
		} else
			socket = new Socket();
		try {
			socket.setSoTimeout(15000);
		} catch(SocketException exception) {}
		socket.connect(new InetSocketAddress(host, port), 3000);
		createStreams();
	}

	private void createStreams() throws IOException {
		InputStream in = socket.getInputStream();
		inputStream = new DataInputStream(in);
		OutputStream out = socket.getOutputStream();
		outputStream = new DataOutputStream(out);
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				socket.close();
			} catch(IOException e) {}
			socket = null;
			inputStream = null;
			outputStream = null;
		}
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ProxyData getProxy() {
		return proxy;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProxy(ProxyData proxy) {
		if(proxy.getType().equals(ProxyType.HTTP))
			throw new IllegalArgumentException("HTTP proxies are not usable for this purpose.");
		this.proxy = proxy;
	}

	public Socket getSocket() {
		return socket;
	}

	public DataInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(DataInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(DataOutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
