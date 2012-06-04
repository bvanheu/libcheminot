package newchem.models;

/**
 *
 * @author hw
 */
public class Admission {

    private Integer _programId;

    private Integer _firstTermId;
    private Integer _lastTermId;
    private Float _grade;
    private Integer _credit;

    private Program _program;

    public Admission() {
        
    }

    public Integer getCredit() {
        return _credit;
    }

    public void setCredit(Integer _credit) {
        this._credit = _credit;
    }

    public Integer getFirstTermId() {
        return _firstTermId;
    }

    public void setFirstTermId(Integer _firstTermId) {
        this._firstTermId = _firstTermId;
    }

    public Float getGrade() {
        return _grade;
    }

    public void setGrade(Float _grade) {
        this._grade = _grade;
    }

    public Integer getLastTermId() {
        return _lastTermId;
    }

    public void setLastTermId(Integer _lastTermId) {
        this._lastTermId = _lastTermId;
    }

    public Integer getProgramId() {
        return _programId;
    }

    public void setProgramId(Integer _programId) {
        this._programId = _programId;
    }

    public Program getProgram() {
        return _program;
    }

    public void setProgram(Program _program) {
        this._program = _program;
    }

}
