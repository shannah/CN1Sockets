package ca.weblite.codename1.net;


/**
 * 
 *  @author shannah
 */
public class Socket {

	public Socket(String host, int port) {
	}

	public Socket(String host, int port, int timeout) {
	}

	public static byte[] getBuffer(int bufferId) {
	}

	public static int createBuffer(int size) {
	}

	public static void deleteBuffer(int bufferId) {
	}

	public static boolean isSocketSupported() {
	}

	public void close() {
	}

	public final void connect() {
	}

	public java.io.InputStream getInputStream() {
	}

	public java.io.OutputStream getOutputStream() {
	}

	public int getPort() {
	}

	public String getHost() {
	}

	public boolean isClosed() {
	}

	public boolean isConnected() {
	}

	public boolean isInputShutdown() {
	}

	public boolean isOutputShutdown() {
	}

	public int getReceiveBufferSize() {
	}

	public int getSendBufferSize() {
	}

	public void setReceiveBufferSize(int size) {
	}

	public void setSendBufferSize(int size) {
	}

	public void shutdownInput() {
	}

	public void shutdownOutput() {
	}
}
