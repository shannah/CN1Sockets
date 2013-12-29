/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.codename1.net;

import ca.weblite.codename1.net.impl.NativeSocket;
import com.codename1.io.Log;
import com.codename1.system.NativeLookup;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
    private static ArrayList<byte[]> buffers = new ArrayList<byte[]>();
    public static byte[] getBuffer(int bufferId){
        return buffers.get(bufferId);
    }
    
    public static int createBuffer(int size){
        buffers.add(new byte[size]);
        return nextBufferId++;
    }
    
    public static void deleteBuffer(int bufferId){
        buffers.remove(bufferId);
    }
    
    
    public Socket(String host, int port) throws IOException {
        this(host, port, 30);
    }
    
    public Socket(String host, int port, int timeout) throws IOException {
        this.host = host;
        this.port = port;
        this.peer = (NativeSocket)NativeLookup.create(NativeSocket.class);
        Log.p("We have the peer");
        if ( !this.peer.isSupported() ){
            Log.p("Not supported");
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
    
    public final void connect() throws IOException {
        if ( !peer.connectSocket( timeout)){
            throw new IOException(peer.getErrorMessage());
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
            Log.p("About to read single byte");
            int res = peer.read();
            Log.p("Single byte read");
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            return res;
        }

        @Override
        public int read(byte[] b) throws IOException {
            Log.p("About to read length "+b.length);
            int res = peer.readBuf(b.length);
            Log.p("Read length : "+res);
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            if ( res > 0 ){
                Log.p("About to copy array to buffer");
                byte[] buf = getBuffer(peer.getBufferId());
                System.arraycopy(buf, 0, b, 0, res);
                Log.p("Finished copying array to buffer");
            }
            return res;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            Log.p("Reading at offset "+off+" with len "+len);
            int res = peer.readBuf(len);
            Log.p("Completed read "+res);
            if ( res == -2 ){
                throw new IOException(peer.getErrorMessage());
            }
            if ( res > 0 ){
                byte[] buf = getBuffer(peer.getBufferId());
                Log.p("About to copy array to buffer");
                System.arraycopy(buf, 0, b, off, len);
                Log.p("Finished copying array to buffer");
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
