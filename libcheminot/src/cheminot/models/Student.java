package newchem.models;

import java.util.List;

/**
 *
 * @author hw
 */
public class Student extends Person {

    private String _permanentCode;
    private String _solde;
    private Integer _firstTermId;
    private Integer _currentTermCredit;
    private String _libraryCode;

    private Admission _admission;
    private List<Enrollment> _enrollments;

    public Student() {

    }

    public String getPermanentCode() {
        return _permanentCode;
    }

    public void setPermanentCode(String permanentCode) {
        this._permanentCode = permanentCode;
    }

    public String getId() {
        return this.getPermanentCode();
    }

    public void setId(String id) {
        this.setPermanentCode(id);
    }

    public String getSolde() {
        return _solde;
    }

    public void setSolde(String _solde) {
        this._solde = _solde;
    }

    public Integer getFirstTermId() {
        return _firstTermId;
    }

    public void setFirstTermId(Integer _firstTermId) {
        this._firstTermId = _firstTermId;
    }

    public List<Enrollment> getEnrollments() {
        return _enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this._enrollments = enrollments;
    }

    public Integer getCurrentTermCredit() {
        return _currentTermCredit;
    }

    public void setCurrentTermCredit(Integer _credit) {
        this._currentTermCredit = _credit;
    }

    public String getLibraryCode() {
        return _libraryCode;
    }

    public void setLibraryCode(String _libraryCode) {
        this._libraryCode = _libraryCode;
    }

    public Admission getAdmission() {
        return _admission;
    }

    public void setAdmission(Admission _program) {
        this._admission = _program;
    }
}
