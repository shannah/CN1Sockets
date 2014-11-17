package ca.weblite.codename1.net.impl;


/**
 * 
 *  @author shannah
 */
public interface NativeSocket extends com.codename1.system.NativeInterface {

	public void setBufferId(int id);

	public int getBufferId();

	public boolean createSocket(String host, int port);

	public boolean connectSocket(int timeout);

	public boolean closeSocket();

	public boolean isSocketClosed();

	public boolean isSocketConnected();

	public boolean isInputShutdown();

	public boolean isOutputShutdown();

	public boolean setKeepAlive(boolean on);

	public boolean setReceiveBufferSize(int size);

	public boolean setSendBufferSize(int size);

	public String getErrorMessage();

	public int getErrorCode();

	public int read();

	public int readBuf(int length);

	public int available();

	public boolean closeInputStream();

	public boolean markInputStream(int readLimit);

	public boolean markSupported();

	public boolean resetInputStream();

	public long skip(long n);

	public boolean closeOutputStream();

	public boolean flushOutputStream();

	public boolean writeBuf(byte[] b);

	public boolean writeBuffOffsetLength(byte[] b, int offset, int len);

	public boolean write(int b);
}
