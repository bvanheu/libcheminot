package newchem.models;

/**
 *
 * @author hw
 */
public class Term {
    private Integer _id;
    
    private Integer _year;
    private TermType _type;

    public Term() {
        
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        this._id = id;
        this._year = this._id / 10;
        switch (this._id % 10) {
            case 1:
                this._type = TermType.WINTER;
                break;
            case 2:
                this._type = TermType.SUMMER;
                break;
            case 3:
                this._type = TermType.FALL;
                break;
            default:
                this._type = TermType._UNKNOWN;
                break;
        }
    }

    public TermType getType() {
        return _type;
    }

    public void setType(TermType _type) {
        this._type = _type;
    }

    public Integer getYear() {
        return _year;
    }

    public void setYear(Integer _year) {
        this._year = _year;
    }
}
