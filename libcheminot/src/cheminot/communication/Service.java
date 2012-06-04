package cheminot.communication;

import cheminot.exception.NotConnectedException;
import cheminot.exception.ProtocolException;
import cheminot.exception.SQLException;
import ets.zip.Compression;
import java.awt.Label;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cheminot service
 *
 * @author hw
 *
 * erreur pas de point: "ERR. SERVICE INCONNU, SANSPT"
 */
public class Service {
    private Connection _connection = null;
    private Random random = null;
    private boolean _javaVersion11 = false;
    protected boolean _isConnected = true;
    private String error = null;
    protected boolean e = true;
    protected Security security = null;
    
    protected String _host;
    protected int _port;
    protected boolean _boolean;

    private static boolean l;
    private static Object k;
    private String usernameLoggedIn = ""; // code permanent

    private boolean delimiterLoaded = false;
    private String _delimiter = "\003";

    private Label errorLabel = null;

    // NOT USED?
    private boolean f = false;
    private int m = 50;
    private int a = 0; // devient 1

    public Service(String host, Security security) {
        this(host, 8815, true, security);
    }

    protected Service(String host, int port, boolean paramBoolean, Security security) {
        this._host = host;
        this._port = port;
        this.e = paramBoolean;
        this.security = security;
        
        this._javaVersion11 = System.getProperty("java.version").startsWith("1.1");
    }

    public void start(Crypto crypto) throws IOException, NotConnectedException, ProtocolException  {
        Long newSeed = Crypto.generateSeed();

        if (this._host.equalsIgnoreCase("LOCAL")) {
            this._connection = new Connection(InetAddress.getLocalHost(), this._port);
        }
        else {
            this._connection = new Connection(this._host, this._port);
        }
        
        this._connection.setCompression(new Compression());

        this._connection.connect();
        
        this._connection.setCrypto(crypto);

        if (this.sendSeed(newSeed)){
            // Agreed on a new seed
            crypto.setSeed(newSeed);
            this.random = new Random(newSeed);
        }
        // else
        //     server didn't accept our new seed?
    }

    public void shutdown() {
        this._isConnected = false;

        if (this._connection != null) {
            //this.logout();
            this._connection.disconnect();
        }

        this._connection = null;
    }

    /**
     * Send and wait for a response
     * 
     * @param paramString
     * @return the response as a string or null
     * @throws NotConnectedException
     * @throws IOException
     */
    protected final String sendAndRecv(String paramString) throws NotConnectedException, IOException {
        String str = "";
        
        if (this.random != null) {
            long l1 = Math.abs(this.random.nextLong() / 2L) + (Integer.parseInt(paramString.substring(0, 3)) - 200);
            paramString = Long.toString(l1, 9) + paramString.substring(3);
        }

        System.out.println("sending: " + this.security.printParam() + "#" + paramString);
        this._connection.send(this.security.printParam() + "#" + paramString);

        str = this._connection.recvString();

        return str;
    }

    /**
     * 200
     * 
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public final boolean pingJavelot() throws NotConnectedException, IOException {
        return sendAndRecv("200.Bonjour Javelot!").equals("Bonjour Javelot!");
    }

    public final boolean send202(String paramString) throws NotConnectedException, IOException {
        return sendAndRecv("202." + paramString).startsWith("OK");
    }

    public final boolean send203() throws NotConnectedException, IOException {
        return sendAndRecv("203.").startsWith("OK");
    }

    public final void send204(String paramString) throws NotConnectedException, IOException {
        sendAndRecv("204." + paramString);
    }
    
    public final boolean send205(String paramString) throws NotConnectedException, IOException {
        return sendAndRecv("205." + paramString).startsWith("OK");
    }

    public final String getRemoteAddr() throws NotConnectedException, IOException {
        return sendAndRecv("206.");
    }

    /**
     * 208
     * 
     * @return
     */
    public final Date getServerTime() throws NotConnectedException, IOException {
        String cmdServerTimeRes = sendAndRecv("208.");

        if (cmdServerTimeRes == null) {
            return new Date();
        }

        try {
            if (cmdServerTimeRes.indexOf(":") < 0) {
                return new Date(Long.valueOf(cmdServerTimeRes).longValue());
            }

            StringTokenizer dateTokenizer = new StringTokenizer(cmdServerTimeRes, ":");
            int i1 = Integer.parseInt(dateTokenizer.nextToken());
            int i2 = Integer.parseInt(dateTokenizer.nextToken());
            int i3 = Integer.parseInt(dateTokenizer.nextToken());
            int i4 = Integer.parseInt(dateTokenizer.nextToken());
            int i5 = Integer.parseInt(dateTokenizer.nextToken());
            int i6 = Integer.parseInt(dateTokenizer.nextToken());

            Calendar localCalendar = Calendar.getInstance();
            localCalendar.set(i1, i2, i3, i4, i5, i6);

            return localCalendar.getTime();
        }
        catch (Exception localException) {
            return new Date();
        }
    }

    /**
     * 210
     * 
     * @param seed
     * @param unknown
     * @param javaVersion11
     * @return
     */
    public boolean sendSeed(long seed) throws ProtocolException, NotConnectedException, IOException {
        String cmdInitCryptoMsg = "";
        String cmdInitCryptoRes = "";
        String seed9 = Long.toString(seed, 9);

        cmdInitCryptoMsg = "210.";
        cmdInitCryptoMsg += seed9;
        cmdInitCryptoMsg += ".";
        cmdInitCryptoMsg += (this.e ? "1" : "0");
        cmdInitCryptoMsg += (this._javaVersion11 ? "1" : "0");

        cmdInitCryptoRes = this.sendAndRecv(cmdInitCryptoMsg);

        this.e = false;

        if (cmdInitCryptoRes == null) {
            this.shutdown();
            throw new ProtocolException("received null while sending 210");
        }
        
        if (cmdInitCryptoRes.startsWith("ERR")) {
            this.shutdown();
            throw new ProtocolException("received ERR while sending 210 (error: <" + cmdInitCryptoRes + ">)");
        }
        
        if (!cmdInitCryptoRes.equals(seed9)) {
            this.shutdown();
            throw new ProtocolException("received different seed (sent: " + seed9 + ", received: " + cmdInitCryptoRes + ")");
        }
        
        return true;
    }

    /**
     * 212
     *
     * Still not sure, but it looks like it is something related with SQL call "USE database"
     * 
     * @param database
     * @throws NotConnectedException
     * @throws IOException
     */
    public final void send212(String database) throws NotConnectedException, IOException {
        sendAndRecv("212." + database);
    }

    /**
     * 213.
     *
     * Get delimiter
     *
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public final String getDelimiter() throws NotConnectedException, IOException {
        String delimiter = this._delimiter;

        if (!this._isConnected) {
            return delimiter;
        }

        if (!this.delimiterLoaded) {
            this.delimiterLoaded = true;

            delimiter = sendAndRecv("213.");

            if (delimiter != null) {
                if (delimiter.length() == 1) {
                    this._delimiter = delimiter;
                }
            }
        }

        delimiter = this._delimiter;

        return delimiter;
    }

    /**
     * 220
     * 
     * @param paramString
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public final String send220(String paramString) throws NotConnectedException, IOException {
        return sendAndRecv("220." + paramString);
    }

    /**
     * 221
     *
     * Log in with a user
     * 
     * @param username
     * @param password
     * @return An integer with proper bit set for the user type
     */
    public Integer login(String username, String password) throws NotConnectedException, IOException, ProtocolException {
        String cmdLogin = "";
        String cmdLoginResult = "";
        Integer userType = 0;

        cmdLogin = "221." + username + "@" + hash(password);
        
        cmdLoginResult = this.sendAndRecv(cmdLogin);

        if (cmdLoginResult.startsWith("ERR")) {
            throw new ProtocolException("unable to log in (username: " + username + ", received: " + cmdLoginResult + ")");
        }

        try {
            userType = Integer.parseInt(cmdLoginResult);
        }
        catch (NumberFormatException ex) {
            // ignore exception, fallback to 0 which is a bad credentials
            userType = new Integer(0);
        }

        return userType;
    }

    /**
     * 224.
     *
     * Not sure but it looks like something related with the SQL function "USE cheminot"
     *
     * Sending 224 should return "OK."
     *
     * @param dbCheminot
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public final boolean send224(String dbCheminot) throws NotConnectedException, IOException, ProtocolException {
        String result = "";

        result = this.sendAndRecv("224." + dbCheminot);

        if (!result.startsWith("OK")) {
            throw new ProtocolException("unable to 224 (received: " + result + ")");
        }
        
        return true;
    }

    /**
     * 229
     *
     * Fetch the logged in username.
     *
     * For a student, this will most likely return the code permanent.
     * 
     * @return Logged in username
     */
    public String fetchUsername() throws NotConnectedException, IOException {
        return this.sendAndRecv("229.");
    }
    
    public final String send231(String paramString) throws NotConnectedException, IOException {
        return sendAndRecv("231." + paramString);
    }

    /**
     * 234
     *
     * Get javelot message of the day
     *
     * @return Message of the day
     * @throws NotConnectedException
     * @throws IOException
     */
    public final String getJavelotMotd() throws NotConnectedException, IOException {
        return sendAndRecv("234.");
    }

    /**
     * 239.
     *
     * Do a SQL query
     *
     * @param request
     * @param data
     * @param result
     * @return
     * @throws NotConnectedException
     * @throws IOException
     */
    public int sendString(String request, String data, List<String> result) throws NotConnectedException, IOException, SQLException {
        StringBuilder cmdFetch = new StringBuilder();
        Object objectResult;
        String fetchStatus = "";
        Integer fetchCount; // how many rows has been selected
        String fetchType;   // what kind of data we received: "S" for string,
        Vector localResult; // the raw data we receive

        cmdFetch.append("239.");
        cmdFetch.append(request);
        cmdFetch.append(".");
        cmdFetch.append(data);
        fetchStatus = this.sendAndRecv(cmdFetch.toString());

        try {
            int position = fetchStatus.indexOf(".");

            fetchCount = Integer.parseInt(fetchStatus.substring(0, position));
            fetchType = fetchStatus.substring(position);
        }
        catch (NumberFormatException ex) {
            // unable to parse the number of result
            throw ex;
        }

        objectResult = this._connection.recvObject();

        if (objectResult instanceof String) {
            throw new SQLException((String)objectResult);
        }

        localResult = (Vector)objectResult;

        result.clear();
        result.addAll(localResult);

        return fetchCount;
    }

    /**
     *
     * @param codePermanent
     * @param session
     * @param nouvelAdmis 0, 1 or 2 valid
     * @param paramBoolean
     * @return
     */
    public boolean confirmerChoixDeCours(String codePermanent, int session, int nouvelAdmis, boolean paramBoolean) throws NotConnectedException, IOException {

        if (session < 20061) {
            //setError("confirmeChoixDeCours:session invalide:" + session);
            return false;
        }

        if (codePermanent == null || codePermanent.length() <= 0) {
            //setError("confirmeChoixDeCours:ON ESSAIE DE ME REFILER CODEPERMANENT NUL OU VIDE");
            return false;
        }

        if (nouvelAdmis < 0 || nouvelAdmis > 2) {
            //setError("confirmeChoixDeCours:Valeur 0, 1 ou 2 permise pour parametre nouvelAdmis");
            return false;
        }

        synchronized (k) {
            String fieldSeparator = "\003";
            StringBuffer cmdConfirmeChoixCours = new StringBuffer();

            cmdConfirmeChoixCours.append(codePermanent).append(fieldSeparator);
            cmdConfirmeChoixCours.append(nouvelAdmis).append(fieldSeparator);
            cmdConfirmeChoixCours.append(paramBoolean ? "V" : "F").append(fieldSeparator);
            cmdConfirmeChoixCours.append(session);

            String cmdConfirmeChoixCoursResult = sendAndRecv("237." + cmdConfirmeChoixCours.toString());

            if (cmdConfirmeChoixCoursResult.startsWith("ERR")) {
                //setError(cmdConfirmeChoixCoursResult);
                return false;
            }

            return true;
        }
    }

    public boolean annuleChoixCours(String codePermanent, int session) throws NotConnectedException, IOException {
        if (session < 20061) {
            //setError("AnnuleChoixCours:Session invalide " + session);
            return false;
        }

        if (codePermanent == null || codePermanent.length() <= 0) {
            //setError("AnnuleChoixCours:ON ESSAIE DE ME REFILER CODEPERMANENT NUL OU VIDE");
            return false;
        }

        synchronized (k) {
            String str = sendAndRecv("238." + codePermanent + "." + session);
            if (str.startsWith("ERR")) {
                //setError(str);
                return false;
            }
        }
        return true;
    }

    public static String preHash(String password) {
        password = password.toUpperCase();
        long hash = 0L;
        int thirteen = 13;

        for (int position=0; position < password.length(); position++) {
            hash += (hash * thirteen++) + (short) password.charAt(position);
        }
        
        hash = Math.abs(hash);
        hash <<= (int) (hash % 11L);
        
        return Math.abs(hash) + "";
    }

    public static String hash(String password) {
        password = preHash(password);

        Random rand = new Random(System.currentTimeMillis() % 150001L);

        // Random 68-84
        int random68_84 = 68 + (int) (rand.nextDouble() * 16.0D);

        char[] randomNumber0_9 = new char[random68_84];
        int counter = 0;

        while (counter < random68_84) {
            // Random 0-9
            randomNumber0_9[counter] = (char) (Math.abs(rand.nextInt()) % 10 + 48);
            counter++;
        }
        
        // Random 2-19
        int random2_19 = 2 + (int) (rand.nextFloat() * 17.0F);

        String str = prefixZero(random2_19, 2);

        randomNumber0_9[0] = str.charAt(0);
        randomNumber0_9[1] = str.charAt(1);

        str = prefixZero(100 - password.length(), 2);

        randomNumber0_9[random2_19] = str.charAt(0);
        randomNumber0_9[(random2_19 + 2)] = str.charAt(1);

        random2_19 += 4;

        for (int i=0; i<password.length(); i++) {
            randomNumber0_9[random2_19] = password.charAt(i);
            random2_19 += 2;
        }

        return new String(randomNumber0_9);
    }

    public String getUsernameLoggedIn() {
        return this.usernameLoggedIn;
    }

    public static final String strError(int errCode) {
        if (errCode > 0) {
            return "";
        }

        switch (errCode) {
            case 0:
                return "Code d'accès invalide, recommencez";
            case -1:
                return "Mot de passe invalide, recommencez";
            case -2:
                return "Accès désactivé, voir le registraire";
            case -3:
                return "Déni de service";
            case -4:
                return "Code ou mot de passe à blanc, recommencez";
            case -5:
                return "Perte de la connexion";
            case -6:
                return "Problème Javelot. Appel StorProc?";
            case -7:
                return "Doublons dans la base de données!";
            case -8:
                return "VEUILLEZ CONTACTER L'INFORMATIQUE POUR MODIFIER VOTRE MOT DE PASSE TROP LONG!";
        }

        return "Accès invalide(" + errCode + ") , recommencez";
    }

    

    private String recv() {
        String err = null;
        try {
            return this._connection.recvString();
        }
        catch (Exception localException) {
            err = "ERR. INCAPABLE DE RECEVOIR: " + localException.toString();
            //setError(err);
            if (err == null) {
                //setError("ERR. INCAPABLE DE RECEVOIR CAR NULL");
            }
        }

        return null;
    }

    public void setErrorLabel(Label errorLabel, int paramInt) {
        this.errorLabel = errorLabel;

        if (errorLabel == null) {
            return;
        }
        
        this.m = paramInt;
    }

    // ERROR //
    public final int a(String paramString, Vector paramVector) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /*
    public final int a(String paramString, Vector paramVector) {
        // Byte code:
     *
        //   0: getstatic 586	ets/a/c:q	Z
        //   3: istore 12
     *
        //   5: aload_0
     *
        //   6: getfield 4	ets/a/c:c	Z
        //   9: iload 12
     *
        //   11: ifne +37 -> 48
     * if () goto 48
        //   14: ifne +15 -> 29
     * if (this.isConnected) goto 29
        //   17: aload_0
     *
        //   18: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   21: bipush 9
     * String str = "PAS DE CONNEXION";
        //   23: aaload
        //   24: invokespecial 41	ets/a/c:setError	(Ljava/lang/String;)V
     * setError("PAS DE CONNEXION");
        //   27: iconst_m1
        //   28: ireturn
     * return -1;
     *
     *
        //   29: aload_0
        //   30: dup
        //   31: iload 12
        //   33: ifne +26 -> 59
     *
        //   36: getfield 17	ets/a/c:a	I
        //   39: dup_x1
        //   40: iconst_1
        //   41: iadd
     *
        //   42: putfield 17	ets/a/c:a	I
        //   45: bipush 10
        //   47: irem
     *
        //   48: ifne +7 -> 55
        //   51: aload_0
        //   52: invokespecial 152	ets/a/c:i	()V
        //   55: getstatic 22	ets/a/c:k	Ljava/lang/Object;
        //   58: dup
     *
        //   59: astore_3
        //   60: monitorenter
        //   61: aload_0
        //   62: new 35	java/lang/StringBuffer
        //   65: dup
        //   66: invokespecial 36	java/lang/StringBuffer:<init>	()V
     *
        //   69: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   72: bipush 38
     * String str = "211.";
        //   74: aaload
        //   75: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     * buffer.append("211.");
        //   78: aload_1
        //   79: invokevirtual 154	java/lang/String:trim	()Ljava/lang/String;
     * buffer.trim();
        //   82: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     * buffer.append(???);
        //   85: invokevirtual 40	java/lang/StringBuffer:toString	()Ljava/lang/String;
        //   88: invokevirtual 77	ets/a/c:g	(Ljava/lang/String;)Ljava/lang/String;
     * this.send234(buffer.toString())
        //   91: astore 4
        //   93: aload 4
        //   95: iconst_m1
        //   96: invokestatic 115	ets/a/c:a	(Ljava/lang/String;I)I
        //   99: istore 5
        //   101: ldc 5
        //   103: astore 6
        //   105: iload 5
        //   107: iload 12
        //   109: ifne +29 -> 138
        //   112: iconst_m1
        //   113: if_icmpne +20 -> 133
        //   116: aload_0
        //   117: invokespecial 155	ets/a/c:l	()Ljava/lang/String;
        //   120: astore 4
        //   122: aload_0
        //   123: aload 4
        //   125: invokespecial 41	ets/a/c:d	(Ljava/lang/String;)V
        //   128: iload 12
        //   130: ifeq +334 -> 464
        //   133: aload_2
        //   134: invokevirtual 156	java/util/Vector:removeAllElements	()V
        //   137: iconst_0
        //   138: istore 7
        //   140: iconst_0
        //   141: istore 8
        //   143: aload_0
        //   144: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   147: iload 12
        //   149: ifne +19 -> 168
        //   152: ifnull +46 -> 198
        //   155: aload_0
        //   156: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   159: invokevirtual 157	java/awt/Component:isVisible	()Z
        //   162: istore 7
        //   164: aload_0
        //   165: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   168: iload 12
        //   170: ifne +24 -> 194
        //   173: invokevirtual 158	java/awt/Label:getText	()Ljava/lang/String;
        //   176: astore 6
        //   178: iload 5
        //   180: aload_0
        //   181: getfield 16	ets/a/c:m	I
        //   184: if_icmple +6 -> 190
        //   187: iconst_1
        //   188: istore 8
        //   190: aload_0
        //   191: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   194: iconst_1
        //   195: invokevirtual 159	java/awt/Component:setVisible	(Z)V
        //   198: iload 5
        //   200: iload 12
        //   202: ifne +231 -> 433
        //   205: ifle +226 -> 431
        //   208: aload_1
        //   209: invokevirtual 117	java/lang/String:toUpperCase	()Ljava/lang/String;
        //   212: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   215: bipush 40
        //   217: aaload
        //   218: invokevirtual 21	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   221: iload 12
        //   223: ifne +210 -> 433
        //   226: ifeq +205 -> 431
        //   229: aload_2
        //   230: iload 5
        //   232: invokevirtual 161	java/util/Vector:ensureCapacity	(I)V
        //   235: aload_0
        //   236: iload 12
        //   238: ifne +117 -> 355
        //   241: getfield 9	ets/a/c:g	Z
        //   244: ifeq +104 -> 348
     *
        //   247: iconst_0
        //   248: istore 9 // 0
        //   250: iload 9
        //   252: iload 5
        //   254: if_icmpge +89 -> 343
     * if (x >= 0); goto 343
        //   257: iload 8
        //   259: iload 12
        //   261: ifne +172 -> 433
        //   264: iload 12
        //   266: ifne +13 -> 279
        //   269: ifeq +54 -> 323
        //   272: iload 9
        //   274: aload_0
        //   275: getfield 16	ets/a/c:m	I
        //   278: irem
        //   279: ifne +44 -> 323
        //   282: aload_0
        //   283: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   286: new 35	java/lang/StringBuffer
        //   289: dup
        //   290: invokespecial 36	java/lang/StringBuffer:<init>	()V
        //   293: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   296: bipush 39
        //   298: aaload
     *
        //   299: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     * buffer.append("");
        //   302: iload 9
        //   304: invokevirtual 145	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
     * buffer.append("");
        //   307: ldc 163
        //   309: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     * buffer.append("");
        //   312: iload 5
        //   314: invokevirtual 145	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
     * buffer.append("");
        //   317: invokevirtual 40	java/lang/StringBuffer:toString	()Ljava/lang/String;
        //   320: invokevirtual 164	java/awt/Label:setText	(Ljava/lang/String;)V
     * label.setText(buffer.toString());
        //   323: aload_0
        //   324: invokespecial 155	ets/a/c:l	()Ljava/lang/String;
        //   327: astore 4 // string
     *
        //   329: aload_2 // vector
        //   330: aload 4 // string
        //   332: invokevirtual 165	java/util/Vector:addElement	(Ljava/lang/Object;)V
     * vector.addElement(string);
        //   335: iinc 9 1
        //   338: iload 12
        //   340: ifeq -90 -> 250
     *
        //   343: iload 12
        //   345: ifeq +86 -> 431
        //   348: aload_0
        //   349: getfield 2	ets/a/c:i	Lets/a/b;
        //   352: invokevirtual 166	ets/a/b:c	()Ljava/lang/Object;
        //   355: checkcast 167	java/util/Vector
        //   358: astore 9
        //   360: aload_2
        //   361: aload 9
        //   363: invokevirtual 168	java/util/Vector:size	()I
        //   366: invokevirtual 161	java/util/Vector:ensureCapacity	(I)V
        //   369: iconst_0
        //   370: istore 10
        //   372: iload 10
        //   374: aload 9
        //   376: invokevirtual 168	java/util/Vector:size	()I
        //   379: if_icmpge +27 -> 406
        //   382: aload_2
        //   383: aload 9
        //   385: iload 10
        //   387: invokevirtual 169	java/util/Vector:elementAt	(I)Ljava/lang/Object;
        //   390: invokevirtual 165	java/util/Vector:addElement	(Ljava/lang/Object;)V
        //   393: iinc 10 1
        //   396: iload 12
        //   398: ifne +15 -> 413
        //   401: iload 12
        //   403: ifeq -31 -> 372
        //   406: aload 9
        //   408: invokevirtual 168	java/util/Vector:size	()I
        //   411: istore 5
        //   413: goto +18 -> 431
        //   416: astore 9
        //   418: aload_0
        //   419: aload 9
        //   421: invokevirtual 46	java/lang/Throwable:toString	()Ljava/lang/String;
        //   424: invokespecial 41	ets/a/c:d	(Ljava/lang/String;)V
        //   427: iconst_m1
        //   428: aload_3
        //   429: monitorexit
        //   430: ireturn
        //   431: iload 8
        //   433: iload 12
        //   435: ifne +33 -> 468
        //   438: ifeq +26 -> 464
        //   441: aload_0
        //   442: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   445: iload 7
        //   447: invokevirtual 159	java/awt/Component:setVisible	(Z)V
        //   450: aload_0
        //   451: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   454: aload 6
        //   456: invokevirtual 164	java/awt/Label:setText	(Ljava/lang/String;)V
        //   459: aload_0
        //   460: aconst_null
        //   461: putfield 15	ets/a/c:d	Ljava/awt/Label;
        //   464: iload 5
        //   466: aload_3
        //   467: monitorexit
        //   468: ireturn
        //   469: astore 11
        //   471: aload_3
        //   472: monitorexit
        //   473: aload 11
        //   475: athrow
        //
        // Exception table:
        //   from	to	target	type
        //   348	413	416	java/lang/Exception
        //   61	430	469	finally
        //   431	468	469	finally
        //   469	473	469	finally
    }
    */

    // ERROR //



    /*
    public final int a(String paramString1, String paramString2, Vector paramVector) {
        // Byte code:
        //   0: getstatic 586	ets/a/c:q	Z
        //   3: istore 14
     *
        //   5: aload_0
        //   6: iload 14
        //   8: ifne +24 -> 32
     *
        //   11: getfield 4	ets/a/c:c	Z
        //   14: ifne +15 -> 29
     * if (this.isConnected) goto 29;
        //   17: aload_0
        //   18: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   21: bipush 9
     * String str = "PAS DE CONNEXION"
        //   23: aaload
        //   24: invokespecial 41	ets/a/c:d	(Ljava/lang/String;)V
     * setError("PAS DE CONNEXION");
        //   27: iconst_m1
        //   28: ireturn
     * return -1;
        //   29: getstatic 22	ets/a/c:k	Ljava/lang/Object;
        //   32: dup
        //   33: astore 4
        //   35: monitorenter
     * synchronized(k) {
        //   36: aload_0
        //   37: new 35	java/lang/StringBuffer
        //   40: dup
        //   41: invokespecial 36	java/lang/StringBuffer:<init>	()V
     *
     * StringBuffer buffer = new StringBuffer();
     * 
        //   44: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   47: bipush 68
        //   49: aaload
        //   50: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     *
     * buffer.append("239.");
     * 
        //   53: aload_1
        //   54: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     *
     * buffer.append(paramString1);
     *
        //   57: ldc 74 // '.'
        //   59: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     *
     * buffer.append(???);
     *
        //   62: aload_2
        //   63: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
     *
     * buffer.append(paramString2);
     *
        //   66: invokevirtual 40	java/lang/StringBuffer:toString	()Ljava/lang/String;
        //   69: invokevirtual 77	ets/a/c:sendAndRecv	(Ljava/lang/String;)Ljava/lang/String;
     *
     * String cmdResult = sendAndRecv(buffer.toString());
     * 
        //   72: astore 5
        //   74: aload 5
     *
        //   76: ldc 74
        //   78: invokevirtual 48	java/lang/String:indexOf	(Ljava/lang/String;)I
     * indexOf(???);
        //   81: istore 6
        //   83: aload 5
        //   85: iconst_0
        //   86: iload 6
        //   88: invokevirtual 60	java/lang/String:substring	(II)Ljava/lang/String;
     * substring(0, indexOf());
        //   91: iconst_m1
        //   92: invokestatic 115	ets/a/c:a	(Ljava/lang/String;I)I
     *
        //   95: istore 7
        //   97: ldc 5
        //   99: astore 8
        //   101: iload 7
        //   103: iload 14
        //   105: ifne +26 -> 131
        //   108: iconst_m1
        //   109: if_icmpne +17 -> 126
        //   112: aload_0
        //   113: aload_0
        //   114: invokespecial 155	ets/a/c:l	()Ljava/lang/String;
        //   117: invokespecial 41	ets/a/c:d	(Ljava/lang/String;)V
        //   120: iload 7
        //   122: aload 4
        //   124: monitorexit
     * } // end synchronized
        //   125: ireturn
        //   126: aload_3
        //   127: invokevirtual 156	java/util/Vector:removeAllElements	()V
        //   130: iconst_0
        //   131: istore 9
        //   133: iconst_0
        //   134: istore 10
        //   136: aload_0
        //   137: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   140: iload 14
        //   142: ifne +19 -> 161
        //   145: ifnull +46 -> 191
        //   148: aload_0
        //   149: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   152: invokevirtual 157	java/awt/Component:isVisible	()Z
        //   155: istore 9
        //   157: aload_0
        //   158: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   161: iload 14
        //   163: ifne +24 -> 187
        //   166: invokevirtual 158	java/awt/Label:getText	()Ljava/lang/String;
        //   169: astore 8
        //   171: iload 7
        //   173: aload_0
        //   174: getfield 16	ets/a/c:m	I
        //   177: if_icmple +6 -> 183
        //   180: iconst_1
        //   181: istore 10
        //   183: aload_0
        //   184: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   187: iconst_1
        //   188: invokevirtual 159	java/awt/Component:setVisible	(Z)V
        //   191: iload 7
        //   193: iload 14
        //   195: ifne +226 -> 421
        //   198: ifle +221 -> 419
        //   201: aload 5
        //   203: ldc 171
        //   205: invokevirtual 48	java/lang/String:indexOf	(Ljava/lang/String;)I
        //   208: iload 14
        //   210: ifne +211 -> 421
        //   213: iflt +206 -> 419
        //   216: aload_3
        //   217: iload 7
        //   219: invokevirtual 161	java/util/Vector:ensureCapacity	(I)V
        //   222: aload_0
        //   223: iload 14
        //   225: ifne +117 -> 342
        //   228: getfield 9	ets/a/c:g	Z
        //   231: ifeq +104 -> 335
        //   234: iconst_0
        //   235: istore 11
        //   237: iload 11
        //   239: iload 7
        //   241: if_icmpge +89 -> 330
        //   244: iload 10
        //   246: iload 14
        //   248: ifne +173 -> 421
        //   251: iload 14
        //   253: ifne +13 -> 266
        //   256: ifeq +54 -> 310
        //   259: iload 11
        //   261: aload_0
        //   262: getfield 16	ets/a/c:m	I
        //   265: irem
        //   266: ifne +44 -> 310
        //   269: aload_0
        //   270: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   273: new 35	java/lang/StringBuffer
        //   276: dup
        //   277: invokespecial 36	java/lang/StringBuffer:<init>	()V
        //   280: getstatic 607	ets/a/c:z	[Ljava/lang/String;
        //   283: bipush 39
        //   285: aaload
        //   286: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   289: iload 11
        //   291: invokevirtual 145	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
        //   294: ldc 163
        //   296: invokevirtual 38	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   299: iload 7
        //   301: invokevirtual 145	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
        //   304: invokevirtual 40	java/lang/StringBuffer:toString	()Ljava/lang/String;
        //   307: invokevirtual 164	java/awt/Label:setText	(Ljava/lang/String;)V
        //   310: aload_0
        //   311: invokespecial 155	ets/a/c:l	()Ljava/lang/String;
        //   314: astore 5
        //   316: aload_3
        //   317: aload 5
        //   319: invokevirtual 165	java/util/Vector:addElement	(Ljava/lang/Object;)V
        //   322: iinc 11 1
        //   325: iload 14
        //   327: ifeq -90 -> 237
        //   330: iload 14
        //   332: ifeq +87 -> 419
        //   335: aload_0
        //   336: getfield 2	ets/a/c:i	Lets/a/b;
        //   339: invokevirtual 166	ets/a/b:c	()Ljava/lang/Object;
        //   342: checkcast 167	java/util/Vector
        //   345: astore 11
        //   347: aload_3
        //   348: aload 11
        //   350: invokevirtual 168	java/util/Vector:size	()I
        //   353: invokevirtual 161	java/util/Vector:ensureCapacity	(I)V
        //   356: iconst_0
        //   357: istore 12
        //   359: iload 12
        //   361: aload 11
        //   363: invokevirtual 168	java/util/Vector:size	()I
        //   366: if_icmpge +27 -> 393
        //   369: aload_3
        //   370: aload 11
        //   372: iload 12
        //   374: invokevirtual 169	java/util/Vector:elementAt	(I)Ljava/lang/Object;
        //   377: invokevirtual 165	java/util/Vector:addElement	(Ljava/lang/Object;)V
        //   380: iinc 12 1
        //   383: iload 14
        //   385: ifne +15 -> 400
        //   388: iload 14
        //   390: ifeq -31 -> 359
        //   393: aload 11
        //   395: invokevirtual 168	java/util/Vector:size	()I
        //   398: istore 7
        //   400: goto +19 -> 419
        //   403: astore 11
        //   405: aload_0
        //   406: aload 11
        //   408: invokevirtual 46	java/lang/Throwable:toString	()Ljava/lang/String;
        //   411: invokespecial 41	ets/a/c:d	(Ljava/lang/String;)V
        //   414: iconst_m1
        //   415: aload 4
        //   417: monitorexit
     * } synchronized ()
        //   418: ireturn
     * return ?;
        //   419: iload 10
        //   421: iload 14
        //   423: ifne +34 -> 457
        //   426: ifeq +26 -> 452
        //   429: aload_0
        //   430: getfield 15	ets/a/c:d	Ljava/awt/Label;
        //   433: iload 9
        //   435: invokevirtual 159	java/awt/Component:setVisible	(Z)V
     * setVisible();
        //   438: aload_0
        //   439: getfield 15	ets/a/c:d	Ljava/awt/Label;
     *
        //   442: aload 8
        //   444: invokevirtual 164	java/awt/Label:setText	(Ljava/lang/String;)V
     * errorLabel.setText();
        //   447: aload_0
        //   448: aconst_null
        //   449: putfield 15	ets/a/c:d	Ljava/awt/Label;
        //   452: iload 7
        //   454: aload 4
        //   456: monitorexit
     * } // synchronized()
        //   457: ireturn
     * return ?;
        //   458: astore 13
        //   460: aload 4
        //   462: monitorexit
     * } // synchronized()
        //   463: aload 13
        //   465: athrow
        //
        // Exception table:
        //   from	to	target	type
        //   335	400	403	java/lang/Exception
        //   36	125	458	finally
        //   126	418	458	finally
        //   419	457	458	finally
        //   458	463	458	finally
    }
     */

    public static String prefixZero(int value, int valueLength) {
        String str = "" + value;

        while (str.length() < valueLength) {
            str = "0" + str;
        }

        return str;
    }

    public static String formatDate(Date paramDate) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        int i1 = localCalendar.get(1);
        
        return prefixZero(i1, 2) + "-" + prefixZero(localCalendar.get(2) + 1, 2) + "-" + prefixZero(localCalendar.get(5), 2) + "." + prefixZero(localCalendar.get(11), 2) + ":" + prefixZero(localCalendar.get(12), 2) + ":" + prefixZero(localCalendar.get(13), 2);
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        }
        catch (Exception localException) {
        }

        return defaultValue;
    }

    /*
    private ets.b.a a(ets.b.a parama, String error) {
        parama.o = -1;
        parama.l = -1;
        parama.c = -1;
        parama.error = (error + ". " + parama.toString());
        return parama;
    }
     */

    /**
     *
     * @param paramString1
     * @param parama
     * @param typeReservation
     * @param paramString2
     *      May be "F" or "N", "Z"
     * @return
     */
    /*
    private ets.b.a _reserverPlace(String paramString1, ets.b.a parama, boolean typeReservation, String paramString2) {
        String cmd = typeReservation ? "liberePlace: " : "reservePlace: ";

        if (parama == null) {
            return a(new ets.b.a(), cmd + "on essaie de me refiler un null!");
        }

        if (!this._isConnected) {
            return a(parama, cmd + "PAS CONNECTE A JAVELOT");
        }

        String str2 = parama.a();

        if (str2 != null) {
            return a(parama, cmd + " " + str2);
        }

        parama.error = null;

        String cmdReservation = sendAndRecv((typeReservation ? "236." : "235.") + paramString1 + "\003" + paramString2 + "\003" + parama.b());

        if (cmdReservation.startsWith("ERR")) {
            return a(parama, cmdReservation);
        }
        
        return new ets.b.a(cmdReservation);
    }

    public boolean a(String paramString, ets.b.a parama, boolean paramBoolean) {
        String fORn = paramBoolean ? "F" : "N";

        synchronized (k) {
            parama.a(_reserverPlace(paramString, parama, false, fORn));
        }

        if (parama.error != null) {
            return false;
        }

        if (paramBoolean) {
            return true;
        }

        return !parama.f;
    }

    public boolean reserverPlace(String paramString, ets.b.a parama) {
        synchronized (k) {
            parama.a(_reserverPlace(paramString, parama, true, "Z"));
        }

        if (parama.error != null) {
            return parama.error.length() == 0;
        }

        return true;
    }
     */
}

