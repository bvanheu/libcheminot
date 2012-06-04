package cheminot.communication;

import cheminot.exception.CompressionException;
import cheminot.exception.NotConnectedException;
import ets.zip.Compression;
import ets.zip.ZipObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection {

    public static int DEFAULT_BUFFER_SIZE = 2048;

    private Socket _socket = null;
    private ObjectInputStream _objectInput;
    private ObjectOutputStream _objectOutput;
    private InetAddress _host;
    private int _port;
    private Compression _compression;
    private Crypto _crypto;

    public Connection(String host, int port) throws IOException {
        this._host = InetAddress.getByName(host);
        this._port = port;
    }

    public Connection(InetAddress host, int port) throws IOException {
        this._host = host;
        this._port = port;
    }

    public Connection setCompression(Compression compression) {
        this._compression = compression;
        return this;
    }

    public Connection setCrypto(Crypto crypto) {
        this._crypto = crypto;
        return this;
    }

    public boolean connect() throws UnknownHostException, IOException {
        this._socket = new Socket(this._host, this._port);
        this._socket.setTcpNoDelay(true);
        this._objectInput = new ObjectInputStream(new BufferedInputStream(this._socket.getInputStream(), DEFAULT_BUFFER_SIZE));
        this._objectOutput = new ObjectOutputStream(new BufferedOutputStream(this._socket.getOutputStream(), DEFAULT_BUFFER_SIZE));
       
        return true;
    }

    public boolean isConnected() {
        return (this._socket != null && this._socket.isConnected());
    }
    
    public void send(String data) throws NotConnectedException, IOException {
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        if (this._crypto != null) {
            data = this._crypto.encrypt(data);
        }

        if (this._compression != null && data.length() >= 200) {
            ZipObject zipData = null;
            try {
                zipData = this._compression.deflate(data);
            } catch (CompressionException ex) {
                // try to send without compressing data or throw exception?
                this._objectOutput.writeObject(data);
            }

            this._objectOutput.writeObject(zipData);
        }
        else {
            this._objectOutput.writeObject(data);
        }
        
        this._objectOutput.flush();
    }

    /**
     * Receive a String
     */
    public String recvString() throws NotConnectedException, IOException {
        return (String)this.recvObject();
    }

    /**
     * Receive an object
     *
     * Mainly used to receive a Vector
     * 
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public Object recvObject() throws NotConnectedException, IOException {
        Object data = null;

        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        try {
            data = this._objectInput.readObject();
        }
        catch (ClassNotFoundException classNotFound) {
            // Either String or ZipObject is not found... which is bad
            throw new RuntimeException(classNotFound);
        }

        if (data instanceof ZipObject) {
            if (this._compression == null) {
                throw new IOException("received a ZipObject but i wasn't expecting it");
            }

            ZipObject zipData = (ZipObject)data;

            try {
                // We need to decrypt the "compressed data" before trying to inflate the compressed data
                ZipObject zipData2 = new ZipObject();
                zipData2.build(this._crypto.decrypt(zipData.getCompressData()), zipData.getOriginalSize());
                data = this._compression.inflate(zipData2);
            }
            catch (CompressionException ex) {
                throw new IOException(ex);
            }
        }
         else {
            if (this._crypto != null) {
                data = this._crypto.decrypt((String)data);
            }
            else {
                System.out.println("Received data but crypto is not enabled");
            }
         }

        return data;
    }

    /**
     * 
     */
    public void disconnect() {
        try {
            this._socket.close();
        } catch (IOException ex) {
            // Ignore exception
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this._socket = null;
    }
}
