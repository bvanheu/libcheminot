package newchem.models;

/**
 *
 * @author hw
 */
public class Period {

    private Integer _groupId;
    private Integer _termId;
    private String _courseId;

    private Integer _id;
    private Day _day;
    private Integer _begin;
    private Integer _end;
    private Integer _type;
    
    public Period() {
    }

    public Integer getBegin() {
        return _begin;
    }

    public void setBegin(Integer begin) {
        this._begin = begin;
    }

    public String getCourseId() {
        return _courseId;
    }

    public void setCourseId(String _courseId) {
        this._courseId = _courseId;
    }

    public Integer getEnd() {
        return _end;
    }

    public void setEnd(Integer _end) {
        this._end = _end;
    }

    public Integer getGroupId() {
        return _groupId;
    }

    public void setGroupId(Integer _groupId) {
        this._groupId = _groupId;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }

    public Integer getTermId() {
        return _termId;
    }

    public void setTermId(Integer _termId) {
        this._termId = _termId;
    }

    public Integer getType() {
        return _type;
    }

    public void setType(Integer _type) {
        this._type = _type;
    }

    public Day getDay() {
        return _day;
    }

    public void setDay(Day _day) {
        this._day = _day;
    }
}
