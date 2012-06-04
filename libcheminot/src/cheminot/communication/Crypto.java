package cheminot.communication;

import java.util.Random;

/**
 *
 * @author hw
 */
public class Crypto {

    static public long DEFAULT_SEED = 7378697629483820644L;

    private Random _random;
    private long _seed;

    static public long generateSeed() {
        long lrandom = 9223372036854775807L - System.currentTimeMillis();
        lrandom = Math.abs(lrandom >> (int) (1L + lrandom % 13L));
        return lrandom;
    }
    
    public Crypto() {
        this(DEFAULT_SEED);
    }

    public Crypto(long seed) {
        this._random = new Random(seed);
        this._seed = seed;
    }

    public long getSeed() {
        return this._seed;
    }

    public Crypto setSeed(long seed) {
        this._seed = seed;
        this._random.setSeed(seed);
        return this;
    }

    /**
     * 
     * @param data
     * @return
     */
    public String encrypt(String data) {
        return this.xor(data);
    }

    public byte[] encrypt(byte[] data) {
        return this.xor(data);
    }

    /**
     * 
     * @param data
     * @return
     */
    public String decrypt(String data) {
        return this.xor(data);
    }

    public byte[] decrypt(byte[] data) {
        return this.xor(data);
    }

    /**
     * 
     * @param str
     * @return
     */
    private String xor(String str) {
        StringBuffer localStringBuffer = new StringBuffer(str);

        for (int j=0; j < str.length(); j++) {
            localStringBuffer.setCharAt(j, xor(localStringBuffer.charAt(j)));
        }

        return localStringBuffer.toString();
    }

    private char xor(char character) {
        return (char) ((short) character ^ (short) (this._random.nextInt() % 255));
    }

    private byte[] xor(byte[] data) {
        byte[] newData = new byte[data.length];
        
        for (int j=0; j<data.length; j++) {
            newData[j] = (byte) xor((char) data[j]);
        }

        return newData;
    }
}
