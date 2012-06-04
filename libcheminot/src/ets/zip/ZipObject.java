package ets.zip;

import java.io.Serializable;

public class ZipObject implements Serializable {
    private static final long serialVersionUID = 51439688007957L;
    private int iOriginalSize = 0;
    private byte[] compressData;
    public static boolean a;

    public synchronized void build(byte[] pCompressData, int pOriginalSize) {
        this.compressData = pCompressData;
        this.iOriginalSize = pOriginalSize;
    }

    public synchronized byte[] getCompressData() {
        return this.compressData;
    }

    public synchronized int getOriginalSize() {
        return this.iOriginalSize;
    }

    public String toString() {
        String output = "ZipObject serId=51439688007957 data=";

        if (this.compressData == null) {
            output += "null";
        }
        else {

            // TODO - could we replace the following line by:
            // output += this.compressData.length;
            // ???
            output += new StringBuffer().append("").append(this.compressData.length).toString();
        }

        output += " ori=";
        output += this.iOriginalSize;

        return output;
        //return z[2] + (this.compressData == null ? z[1] : new StringBuffer().append("").append(this.compressData.length).toString()) + z[0] + this.iOriginalSize;
    }
}
