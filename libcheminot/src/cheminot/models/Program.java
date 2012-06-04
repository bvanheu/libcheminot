package newchem.models;

/**
 *
 * @author hw
 */
public class Program {
    static public final int ELECTRICAL_ENGINEERING = 7883;

    private Integer _id;
    private Float _gradeMax;
    private String _shortDescription;
    private String _longDescription;
    private Integer _credit;

    /**
     * Return a string representation of a program
     * 
     * @param type
     * @return Program name
     */
    static public String getName(int id) {
        switch (id) {
            case ELECTRICAL_ENGINEERING:
                return "electrical engineering";
        }

        return "unknown";
    }

    public Program() {

    }

    public Float getGradeMax() {
        return _gradeMax;
    }

    public void setGradeMax(Float _gradeMax) {
        this._gradeMax = _gradeMax;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }

    public String getLongDescription() {
        return _longDescription;
    }

    public void setLongDescription(String _longDescription) {
        this._longDescription = _longDescription;
    }

    public String getShortDescription() {
        return _shortDescription;
    }

    public void setShortDescription(String _shortDescription) {
        this._shortDescription = _shortDescription;
    }

    public Integer getCredit() {
        return _credit;
    }

    public void setCredit(Integer _credit) {
        this._credit = _credit;
    }
}
