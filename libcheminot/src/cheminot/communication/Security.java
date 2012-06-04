package cheminot.communication;

public class Security {
    private String _securityModel = "";
    private String _errorModel = "";
    private long _checksumA = 0L;
    private long _checksumB = 0L;
    private long _checksumC = 0L;
    private long _checksumD = 0L;
    
    public Security(String securityModel, String errorModel, long checksumA, long checksumB, long checksumC, long checksumD) {
        this._securityModel = securityModel;
        this._errorModel = errorModel;
        this._checksumA = checksumA;
        this._checksumB = checksumB;
        this._checksumC = checksumC;
        this._checksumD = checksumD;
    }
    
    public String printParam() {
        StringBuilder parameters = new StringBuilder("");

        parameters.append(_parseString(this._securityModel)).append("%").append(_parseString(this._errorModel)).append("%").append(this._checksumA).append("%").append(this._checksumB).append("%").append(this._checksumC).append("%").append(this._checksumD).append("%");

        return parameters.toString();
    }

    @Override
    public String toString() {
        return "sm=" + this._securityModel + ",se=" + this._errorModel + ",A=" + this._checksumA + ",B=" + this._checksumB + ",C=" + this._checksumC + ",D=" + this._checksumD;
    }

    private long _parseLong(String paramString) throws Exception {
        try {
            return Long.parseLong(paramString);
        } catch (Exception localException) {
            throw new Exception("Exception en tentant de décoder " + paramString + ":" + localException.toString());
        }
    }

    private String _parseString(String paramString) {
        String str;

        if (paramString == null) {
            str = "~";
        }
        else {
            str = paramString;
        }

        if (str.length() == 0) {
            return " " + str;
        }

        return str;
    }
}