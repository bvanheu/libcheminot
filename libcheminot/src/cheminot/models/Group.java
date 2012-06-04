package newchem.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hw
 */
public class Group {

    private Integer _id;
    private Term _term;
    private Course _course;
    private List<Period> _periods;

    public Group() {
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }

    public List<Period> getPeriods() {
        return this._periods;
    }

    public void setPeriods(List<Period> periods) {
        this._periods = periods;
    }

    public void addPeriod(Period period) {
        if (this._periods == null) {
            this._periods = new ArrayList<Period>();
        }

        this._periods.add(period);
    }

    public Term getTerm() {
        return _term;
    }

    public void setTerm(Term _term) {
        this._term = _term;
    }

    public Course getCourse() {
        return _course;
    }

    public void setCourse(Course _course) {
        this._course = _course;
    }
}
