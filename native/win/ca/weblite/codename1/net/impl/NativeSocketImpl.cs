using System;
//using java.lang;
// using java.lang;
using org.xmlvm;
using Windows.Networking.Sockets;
using Windows.Storage.Streams;
using Windows.Networking;
using com.codename1.impl;
using System.Threading;
using System.Threading.Tasks;

namespace ca.weblite.codename1.net.impl
{

    public class NativeSocketImpl : NativeSocket
    {

        private StreamSocket iSocket;
        private DataReader iReader;
        private DataWriter iWriter;
        private byte[] receiveBuffer, sendBuffer;
        private Exception lastError;
        private int bufferId;
        private String host;
        private int port;
        private bool iNewMess = true;

        private uint iBufLen = 8192;
        private static int gTimeOut = 10; //10s

        public void @this()
        {
        }
        public void setBufferId(int bufferId)
        {
            this.bufferId = bufferId;
        }

        public int getBufferId()
        {
            return bufferId;
        }




        public int read()
        {
            try
            {
                return iReader.ReadByte(); // socket.InputStream.ReadAsyncgetInputStream().read();
            }
            catch (Exception t)
            {
                lastError = t;
                return -2;
            }
        }

        public long skip(long n)
        {
            try
            {
                //TO DO not tested
                uint i = (uint)n;
                byte[] b = new byte[n];
                iReader.ReadBytes(b);

                //     uint x = iReader.LoadAsync(i).AsTask().GetAwaiter().GetResult();
                return n;  //(long)x; // socket.getInputStream().skip(n);
            }
            catch (Exception t)
            {
                lastError = t;
                return -2;
            }
        }

        public int available()
        {
            uint x;
            if (iReader == null)
                return -1; //should not happen
            try
            {
                if ((iReader.UnconsumedBufferLength == 0) && iNewMess)
                {
                    
                    var cts = new CancellationTokenSource();
                    try
                    {
                        cts.CancelAfter(gTimeOut * 1000);
                        //LoadAsync hangup if no bytes available in stream
                        x = iReader.LoadAsync(iBufLen).AsTask(cts.Token).GetAwaiter().GetResult();
                        if (x < iBufLen) //we assumed that the message/stream is read finished and we need to send sth to get nect message
                            iNewMess = false;
                    }
                    catch (TaskCanceledException)
                    {
                        iNewMess = false;
                        closeSocket(); //after LoadSync TaskCancelled we need to prepare new connection
                        connectSocket(10);
                        return -1;
                    } 
                }
                if (iReader.UnconsumedBufferLength == 0)
                    return -1;
                else
                    return (int)iReader.UnconsumedBufferLength;
            }
            catch (Exception t)
            {
                lastError = t;
                return -2;
            }
        }

        public bool markSupported()
        {
                //TO DO
                return false;
        }



        public bool setReceiveBufferSize(int size)
        {
                iBufLen = (uint)size;
                // receiveBuffer = new byte[size];
                return true;
        }

        public bool write(int b)
        {
            try
            {
                iWriter.WriteByte((byte)b);
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool setSendBufferSize(int size)
        {
            try
            {
                // socket.setSendBufferSize(size);
                //sendBuffer = new byte[size];
                //TO DO
                iSocket.Control.OutboundBufferSizeInBytes = (uint)size;
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool setKeepAlive(bool on)
        {
            try
            {  //Not Tested
                iSocket.Control.KeepAlive = on;
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool isInputShutdown()
        {
            //TO DO
            return false; // socket.InputStream.isInputShutdown();
        }

        public bool isOutputShutdown()
        {
            //TO DO
            return false; // socket.isOutputShutdown();
        }

        public bool closeOutputStream()
        {
            if (iWriter == null)
                return true;
            try
            {
                try
                {
                    iWriter.DetachStream();
                }
                catch (Exception t1)
                {
                    lastError = t1;
                }
                iWriter.Dispose();
                iSocket.OutputStream.Dispose();
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
            finally
            {
                iWriter = null;
            }
        }

        public int readBuf(int len)
        {
            sbyte[] sbuf =
                ((_nArrayAdapter<sbyte>)ca.weblite.codename1.net.Socket.getBuffer(bufferId)).getCSharpArray();
            int readLen = (int)iReader.UnconsumedBufferLength;
            if (readLen > len)
                readLen = len;
            byte[] buf = new byte[readLen];
            try
            {
                //        iReader.LoadAsync((uint)len).AsTask().ConfigureAwait(false).GetAwaiter().GetResult();
                iReader.ReadBytes(buf);
                System.Buffer.BlockCopy(buf, 0, sbuf, 0, readLen);
                return readLen;

            }
            catch (Exception t)
            {
                lastError = t;
                return -2;
            }
        }

        public bool closeInputStream()
        {
            if (iReader == null)
                return true;
            try
            {
                try
                {
                    iReader.DetachStream();
                }
                catch (Exception t1)
                {
                    lastError = t1;
                }
                iReader.Dispose();

                iSocket.InputStream.Dispose();
                ca.weblite.codename1.net.Socket.deleteBuffer(bufferId);
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
            finally
            {
                iReader = null;
            }
        }

        public int getErrorCode()
        {
            return (lastError == null ? 0 : lastError.HResult);
        }

        public bool createSocket(java.lang.String host, int port)
        {
            this.host = SilverlightImplementation.toCSharp(host);
            this.port = port;
            return true;

        }
        //
        // timeout in s
        public bool connectSocket(int timeout)
        {
            try
            {
                iSocket = new StreamSocket();
                HostName h = new HostName(host);
                var cts = new CancellationTokenSource();
                cts.CancelAfter(timeout*1000);
                iSocket.ConnectAsync(h, port.ToString()).AsTask(cts.Token).GetAwaiter().GetResult();
                iReader = new DataReader(iSocket.InputStream);
                iReader.UnicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.Utf8;
                iReader.InputStreamOptions = InputStreamOptions.Partial;
                iReader.ByteOrder = Windows.Storage.Streams.ByteOrder.LittleEndian;
                iWriter = new DataWriter(iSocket.OutputStream);
                iWriter.UnicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.Utf8;
                iWriter.ByteOrder = Windows.Storage.Streams.ByteOrder.LittleEndian;
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                closeSocket();
                return false;
            }
        }

        public bool closeSocket()
        {
            if (iSocket == null)
                return true;
            try
            {
                closeInputStream();
                closeOutputStream();

                iSocket.Dispose();
                ca.weblite.codename1.net.Socket.deleteBuffer(bufferId);
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
            finally
            {
                iSocket = null;
            }
        }

        public bool writeBuf(byte[] buf)
        {
            try
            {
                iWriter.WriteBytes(buf);
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }


        public bool resetInputStream()
        {
            try
            {
                //TO DO
                //socket.InputStream.reset();
                return false;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }



        public bool isSocketConnected()
        {
            return lastError == null; // socket.isConnected();
        }

        public bool markInputStream(int readLimit)
        {
            try
            {
                //TO DO
                // socket.getInputStream().mark(readLimit);
                return false; // true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool isSocketClosed()
        {
            return iSocket == null; //.isClosed();

        }

        public bool writeBuffOffsetLength(byte[] buf, int offset, int len)
        {
            try
            {
                //Not Tested
                byte[] b = new byte[len];
                System.Buffer.BlockCopy(buf, offset, b, 0, len);
                return writeBuf(b);
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool flushOutputStream()
        {
            try
            {
                uint y = iWriter.StoreAsync().AsTask().GetAwaiter().GetResult();
                bool x = iWriter.FlushAsync().AsTask().GetAwaiter().GetResult();
                iNewMess = true;
                return true;
            }
            catch (Exception t)
            {
                lastError = t;
                return false;
            }
        }

        public bool isSupported()
        {
            return true;
        }

        public Object getErrorMessage()
        {
            return SilverlightImplementation.toJava(lastError == null ? @"" : lastError.Message);
        }

        public bool writeBuf(_nArrayAdapter<sbyte> aArr)
        {
            return writeBuf(SilverlightImplementation.toByteArray(aArr.getCSharpArray()));
        }

        public bool writeBuffOffsetLength(_nArrayAdapter<sbyte> aArr, int aOffset, int aLen)
        {
            return writeBuffOffsetLength(SilverlightImplementation.toByteArray(aArr.getCSharpArray()), aOffset, aLen);
        }
    }
}
