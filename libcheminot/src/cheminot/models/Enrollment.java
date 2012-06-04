package newchem.models;

/**
 *
 * @author hw
 */
public class Enrollment {
    private Integer _id;
    
    private Integer _termId;
    private String _studentId;
    private Integer _groupId;

    private Integer _programId;
    private String _courseId;

    private Grade _grade;

    public Enrollment() {
    }

    public Grade getGrade() {
        return _grade;
    }

    public void setGrade(Grade _grade) {
        this._grade = _grade;
    }

    public Integer getGroupId() {
        return _groupId;
    }

    public void setGroupId(Integer _groupId) {
        this._groupId = _groupId;
    }

    public String getStudentId() {
        return _studentId;
    }

    public void setStudentId(String _studentId) {
        this._studentId = _studentId;
    }

    public Integer getTermId() {
        return _termId;
    }

    public void setTermId(Integer _termId) {
        this._termId = _termId;
    }

    public Integer getProgramId() {
        return _programId;
    }

    public void setProgramId(Integer _programId) {
        this._programId = _programId;
    }

    public String getCourseId() {
        return _courseId;
    }

    public void setCourseId(String _courseId) {
        this._courseId = _courseId;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }
}
