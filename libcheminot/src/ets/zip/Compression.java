package ets.zip;

import cheminot.exception.CompressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compression {
    static public int DEFAULT_COMPRESSION_LEVEL = Deflater.BEST_COMPRESSION;
    private Deflater _deflater;
    private Inflater _inflater;

    public Compression() {
        this(DEFAULT_COMPRESSION_LEVEL);
    }

    public Compression(int level) {
        this._deflater = new Deflater(level);
        this._inflater = new Inflater();
    }

    public ZipObject deflate(Object object) throws CompressionException {
        ZipObject zipObject = new ZipObject();
        byte[] raw, rawDeflated;
        
        try {
            raw = this._objectToByte(object);
        } catch (IOException ex) {
            throw new CompressionException();
        }

        rawDeflated = new byte[raw.length];

        this._deflater.setInput(raw);
        this._deflater.finish();
        this._deflater.deflate(rawDeflated);

        zipObject.build(rawDeflated, this._deflater.getTotalOut());

        this._deflater.reset();

        return zipObject;
    }

    public Object inflate(ZipObject zipObject) throws CompressionException {
        Object object = null;
        byte[] raw = new byte[zipObject.getOriginalSize()];
        
        this._inflater.setInput(zipObject.getCompressData());
        
        try {
            this._inflater.inflate(raw);
            this._inflater.reset();
        } catch (DataFormatException ex) {
            throw new CompressionException(ex);
        }
        
        try {
            object = this._byteToObject(raw);
        }
        catch (IOException ex) {
            throw new CompressionException(ex);
        }
        catch (ClassNotFoundException ex) {
            throw new CompressionException(ex);
        }
        
        return object;
    }

    private byte[] _objectToByte(Object object) throws IOException {
        byte[] raw;
        
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

        objectOutput.writeObject(object);
        raw = byteOutput.toByteArray();

        objectOutput.close();
        byteOutput.close();

        return raw;
    }

    private Object _byteToObject(byte[] raw) throws IOException, ClassNotFoundException {
        Object object = null;

        ByteArrayInputStream byteInput = new ByteArrayInputStream(raw);
        ObjectInputStream objectInput = new ObjectInputStream(byteInput);

        object = objectInput.readObject();

        objectInput.close();
        byteInput.close();

        return object;
    }

    public static String byteArrayToHexString(byte[] b) {
    StringBuffer sb = new StringBuffer(b.length * 2);
    for (int i = 0; i < b.length; i++) {
      int v = b[i] & 0xff;
      if (v < 16) {
        sb.append('0');
      }
      sb.append(Integer.toHexString(v));
    }
    return sb.toString().toUpperCase();
  }
}
