/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.codename1.net;

import ca.weblite.codename1.net.impl.NativeSocket;
import com.codename1.system.NativeLookup;
import com.codename1.ui.Display;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author shannah
 */
public class Socket {
    private NativeSocket peer;
    private String host;
    private int port;
    private int timeout;
    private SocketInputStream is;
    private SocketOutputStream os;
    private int receiveBufferSize = 8192;
    private int sendBufferSize = 8192;
    
    private static int nextBufferId = 0;
    private static Map<Integer,byte[]> buffers = new HashMap<Integer,byte[]>();
    public static byte[] getBuffer(int bufferId){
        return buffers.get(bufferId);
    }
    
    public static int createBuffer(int size){
        buffers.put(nextBufferId, new byte[size]);
        return nextBufferId++;
    }
    
    public static void deleteBuffer(int bufferId){
        buffers.remove(bufferId);
    }
    
    
    public Socket(String host, int port) throws IOException {
        this(host, port, 30000);
    }
    
    public static boolean isSocketSupported(){
        try {
            NativeSocket s = (NativeSocket)NativeLookup.create(NativeSocket.class);

            return s != null && s.isSupported();
        } catch ( Exception ex){
            return false;
        }
    }
    
    public Socket(String host, int port, int timeout) throws IOException {
        this.host = host;
        this.port = port;
        this.peer = (NativeSocket)NativeLookup.create(NativeSocket.class);
        if ( !this.peer.isSupported() ){
            throw new IOException("Sockets aren't supported on this system.");
        }
        this.peer.createSocket(this.host, this.port);
        this.peer.setBufferId(createBuffer(receiveBufferSize));
        this.timeout = timeout;
        
        this.connect();
    }
    
    public void close() throws IOException {
        if (!peer.closeSocket() ){
            throw new IOException(peer.getErrorMessage());
        } else {
            is = null;
            os = null;
        }
    }
    
    private boolean connectionTimedOut;
    public final void connect() throws IOException {
        if (timeout > 0 && "ios".equals(Display.getInstance().getPlatformName()) && !Display.getInstance().isSimulator()) {
            connectionTimedOut = false;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        
                        if (!peer.isSocketConnected()) {
                            connectionTimedOut = true;
                            peer.closeSocket();
                        }
                    } catch (Throwable t) {
                        try {
                            peer.closeSocket();
                        } catch (Throwable t2) {
                            
                        }
                    }
                }
            }, timeout);
        }
        if ( !peer.connectSocket( timeout)){
            throw new IOException(connectionTimedOut ? "Failed to connect to server socket because connection timed out: "+peer.getErrorMessage() : peer.getErrorMessage());
        }
        
    }
    
    public InputStream getInputStream() throws IOException {
        if ( is == null ){
            is = new SocketInputStream();
        }
        return is;
        
    }
    
    public OutputStream getOutputStream() throws IOException {
        if ( os == null ){
            os = new SocketOutputStream();
        }
        return os;
    }
    
    public int getPort(){
        return port;
    }
    
    
    public String getHost(){
        return host;
    }
    
    public boolean isClosed(){
        return peer.isSocketClosed();
    }
    
    public boolean isConnected(){
        return peer.isSocketConnected();
    }
    
    public boolean isInputShutdown(){
        return peer.isInputShutdown();
    }
    
    public boolean isOutputShutdown(){
        return peer.isOutputShutdown();
    }
    
    public int getReceiveBufferSize(){
        return receiveBufferSize;
    }
    
    public int getSendBufferSize(){
        return sendBufferSize;
    }
    
    public void setReceiveBufferSize(int size){
        receiveBufferSize = size;
    }
    
    public void setSendBufferSize(int size){
        sendBufferSize = size;
    }
    
    public void shutdownInput() throws IOException {
        getInputStream().close();
    }
  
    public void shutdownOutput() throws IOException {
        getOutputStream().close();
    }
    
    
    class SocketInputStream extends InputStream {
        SocketInputStream(){
            
        }

        @Override
        public int available() throws IOException {
            return peer.available();
        }

        @Override
        public void close() throws IOException {
            if ( !peer.closeInputStream()){
                throw new IOException(peer.getErrorMessage());
            }
        }

        @Override
        public void mark(int readLimit) {
            peer.markInputStream(readLimit);
        }

        @Override
        public boolean markSupported() {
            return peer.markSupported();
        }
        
        @Override
        public int read() throws IOException {
            int res = peer.read();
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            return res;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int res = peer.readBuf(b.length);
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            if ( res > 0 ){
                byte[] buf = getBuffer(peer.getBufferId());
                System.arraycopy(buf, 0, b, 0, res);
            }
            return res;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int res = peer.readBuf(len);
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            if ( res > 0 ){
                byte[] buf = getBuffer(peer.getBufferId());
                System.arraycopy(buf, 0, b, off, len);

            }
            return res;
        }

        @Override
        public void reset() throws IOException {
            if ( !peer.resetInputStream()){
                throw new IOException(peer.getErrorMessage());
            }
        }
        
        
        
        
        
        
        
    }
    
    class SocketOutputStream extends OutputStream {
        
        
        SocketOutputStream(){
           
        }

        @Override
        public void close() throws IOException {
            if ( !peer.closeOutputStream() ){
                throw new IOException(peer.getErrorMessage());
            }
        }

        @Override
        public void flush() throws IOException {
            if ( !peer.flushOutputStream()){
                throw new IOException(peer.getErrorMessage());
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            if ( !peer.writeBuf( b)){
                throw new IOException(peer.getErrorMessage());
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if ( !peer.writeBuffOffsetLength( b, off, len)){
                throw new IOException(peer.getErrorMessage());
            }
        }
        
        
        
        
        
        @Override
        public void write(int b) throws IOException {
            if ( !peer.write( b)){
                throw new IOException(peer.getErrorMessage());
            }
        }
        
    }
}
