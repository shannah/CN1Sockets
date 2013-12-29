package ca.weblite.codename1.net.impl;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public class NativeSocketImpl {
    private SocketConnection socket;

    private Throwable lastError;
    private int bufferId;
    
    public void setBufferId(int bufferId){
        this.bufferId = bufferId;
    }
    
    public int getBufferId(){
        return bufferId;
    }
    
    
    public int read() {
        try {
            return socket.getInputStream().read();
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public long skip( long n) {
        try {
            return socket.getInputStream().skip(n);
        } catch (Throwable t){
            lastError = t;
            return -2;
        }
    }

    public int available() {
        try {
            return socket.getInputStream().available();
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean markSupported() {
        try {
            return socket.getInputStream().markSupported();
        } catch ( Throwable t ){
            lastError = t;
            return false;
        }
        
    }

   

    public boolean setReceiveBufferSize( int size) {
        try {
            socket.setReceiveBufferSize(size);
            return true;
        } catch ( Throwable t ){
            lastError = t;
            return false;
        }
    }

    public boolean write( int b) {
        try {
            socket.getOutputStream().write(b);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean setSendBufferSize( int size) {
        try {
            socket.setSendBufferSize(size);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean setKeepAlive( boolean on) {
        try {
            socket.setKeepAlive(on);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean isInputShutdown() {
        return socket.isInputShutdown();
    }

    public boolean isOutputShutdown() {
        return socket.isOutputShutdown();
    }

    public String getErrorMessage() {
        return lastError.getMessage();
    }

    public boolean closeOutputStream() {
        try {
            socket.getOutputStream().close();
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public int readBuf( int len) {
        byte[] buf = ca.weblite.codename1.net.Socket.getBuffer(bufferId);
        try {
            return socket.getInputStream().read(buf, 0, len);
            
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean closeInputStream() {
        try {
            socket.getInputStream().close();
            return true;
        } catch ( Throwable t ){
            lastError = t;
            return false;
        }
    }

    public int getErrorCode() {
        return 0;
    }

    public boolean createSocket(String host, int port) {
        try {
            socket = Connector.open("socket://"+host+":"+port);
            return true;
            
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean closeSocket() {
        try {
            socket.close();
            
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean writeBuf( byte[] buf) {
        try {
            socket.getOutputStream().write(buf);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public int readBuffOffsetLength( byte[] buf, int offset, int len) {
        try {
            return socket.getInputStream().read(buf, offset, len);
        } catch (Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean resetInputStream() {
        try {
            socket.getInputStream().reset();
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean connectSocket( int timeout) {
        // do nothing... connects automatically on creation
        return true;
    }

    public boolean isSocketConnected() {
        return socket.isConnected();
    }

    public boolean markInputStream( int readLimit) {
        try {
            socket.getInputStream().mark(readLimit);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean isSocketClosed() {
        return socket.isClosed();
        
    }

    public boolean writeBuffOffsetLength( byte[] buf, int offset, int len) {
        try {
            socket.getOutputStream().write(buf, offset, len);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean flushOutputStream() {
        try {
            socket.getOutputStream().flush();
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean isSupported() {
        return true;
    }

}