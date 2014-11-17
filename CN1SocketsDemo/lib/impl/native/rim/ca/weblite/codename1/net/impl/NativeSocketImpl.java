package ca.weblite.codename1.net.impl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public class NativeSocketImpl {
    private SocketConnection socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean closedInput = false;
    private boolean closedOutput = false;
    
    private InputStream getInputStream() throws IOException{
        if ( inputStream == null ){
            inputStream = socket.openInputStream();
        }
        return inputStream;
    }
    
    private OutputStream getOutputStream() throws IOException {
        if ( outputStream == null ){
            outputStream = socket.openOutputStream();
        }
        return outputStream;
    }

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
            return getInputStream().read();
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public long skip( long n) {
        try {
            return getInputStream().skip(n);
        } catch (Throwable t){
            lastError = t;
            return -2;
        }
    }

    public int available() {
        try {
            return getInputStream().available();
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean markSupported() {
        try {
            return getInputStream().markSupported();
        } catch ( Throwable t ){
            lastError = t;
            return false;
        }
        
    }

   

    public boolean setReceiveBufferSize( int size) {
        try {
            socket.setSocketOption(SocketConnection.RCVBUF, size);
            return true;
        } catch ( Throwable t ){
            lastError = t;
            return false;
        }
    }

    public boolean write( int b) {
        try {
            getOutputStream().write(b);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean setSendBufferSize( int size) {
        try {
            socket.setSocketOption(SocketConnection.SNDBUF, size);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean setKeepAlive( boolean on) {
        try {
            socket.setSocketOption(SocketConnection.KEEPALIVE, on ? 1:0);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean isInputShutdown() {
        return closedInput;
    }

    public boolean isOutputShutdown() {
        return closedOutput;
    }

    public String getErrorMessage() {
        return lastError.getMessage();
    }

    public boolean closeOutputStream() {
        try {
            getOutputStream().close();
            closedOutput = true;
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public int readBuf( int len) {
        byte[] buf = ca.weblite.codename1.net.Socket.getBuffer(bufferId);
        try {
            return getInputStream().read(buf, 0, len);
            
        } catch ( Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean closeInputStream() {
        try {
            getInputStream().close();
            closedInput = true;
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
            socket = (SocketConnection)Connector.open("socket://"+host+":"+port);
            return true;
            
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean closeSocket() {
        try {
            socket.close();
            closedInput = true;
            closedOutput = true;
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean writeBuf( byte[] buf) {
        try {
            getOutputStream().write(buf);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public int readBuffOffsetLength( byte[] buf, int offset, int len) {
        try {
            return getInputStream().read(buf, offset, len);
        } catch (Throwable t){
            lastError = t;
            return -2;
        }
    }

    public boolean resetInputStream() {
        try {
            getInputStream().reset();
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
        return socket != null && !closedInput && !closedOutput;
    }

    public boolean markInputStream( int readLimit) {
        try {
            getInputStream().mark(readLimit);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean isSocketClosed() {
        return !isSocketConnected();
        
    }

    public boolean writeBuffOffsetLength( byte[] buf, int offset, int len) {
        try {
            getOutputStream().write(buf, offset, len);
            return true;
        } catch ( Throwable t){
            lastError = t;
            return false;
        }
    }

    public boolean flushOutputStream() {
        try {
            getOutputStream().flush();
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
