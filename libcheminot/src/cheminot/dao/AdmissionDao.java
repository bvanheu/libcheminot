package newchem.dao;

import cheminot.communication.DataService;
import java.util.List;
import newchem.models.Admission;
import newchem.models.Program;
import newchem.models.Student;

/**
 *
 * @author hw
 */
public class AdmissionDao extends Dao {
    public AdmissionDao(DataService data) {
        super(data);
    }

    /**
     * 7883, // 0
     * 1,
     * 02,
     * 01,
     * ,
     * 1,
     * 0,
     * 2010-06-16 00:00:00.0,
     * I,
     * ,
     * ,  // 10
     * ,
     * 2,
     * 062010,
     * 902000,
     * 420AA, 
     * ,
     * 5220,
     * ,
     * 0620,
     * REG, // 20
     * Baccalauréat en génie électrique, // 21
     * Bacc. en génie électrique, // 22
     * 20093, // 23
     * 20112, // 24
     * 0,
     * 8,
     * 0,
     * 29,  // 28 - current credit
     * 59,
     * 14,  // 30 - registred credit
     * 3.09, // 31 - current grade
     * 5,
     * ,
     * 4.30, // 34 - max grade
     * 1,
     * 10,
     * 114, // 37 - max credit
     * 0
     *
     * @param student
     * @return
     */
    public Admission get(String studentId) {
        List<List<String>> rawDatas;
        List<String> rawData;
        Program program;
        Admission admission;

        rawDatas = this._data.fetchAdmission(studentId);
        rawData = rawDatas.get(0);

        program = new Program();
        program.setId(Integer.parseInt(rawData.get(0)));
        program.setGradeMax(Float.parseFloat(rawData.get(34)));
        program.setLongDescription(rawData.get(21));
        program.setShortDescription(rawData.get(22));
        program.setCredit(Integer.parseInt(rawData.get(37)));

        admission = new Admission();
        admission.setFirstTermId(Integer.parseInt(rawData.get(23)));
        admission.setLastTermId(Integer.parseInt(rawData.get(24)));
        admission.setGrade(Float.parseFloat(rawData.get(31)));
        admission.setCredit(Integer.parseInt(rawData.get(28)));
        admission.setProgram(program);

        return admission;
    }

    public Admission get(Student student) {
        return this.get(student.getId());
    }
}
