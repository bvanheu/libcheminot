package newchem.dao;

import cheminot.communication.DataService;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import newchem.models.Student;
import newchem.models.Enrollment;
import newchem.models.Grade;

/**
 *
 * @author hw
 */
public class StudentDao extends Dao {
    
    public StudentDao(DataService data) {
        super(data);
    }

    /**
     * 
     */
    public Student get(String permanentCode) {
        List<List<String>> rawStudents;
        List<String> rawStudent;
        
        List<List<String>> rawStudentCourses;
        List<String> rawStudentCourse;

        List<List<String>> rawUniversitys;
        List<String> rawUniversity;
        
        Student student = new Student();
        
        rawStudents = this._data.fetchStudent(permanentCode);
        rawStudent = rawStudents.get(0);

        student.setPermanentCode(rawStudent.get(0));
        student.setLastname(rawStudent.get(1));
        student.setFirstname(rawStudent.get(2));
        student.setAddress(rawStudent.get(3));
        student.setCity(rawStudent.get(4));
        student.setPostalCode(rawStudent.get(5));
        student.setPhone(rawStudent.get(6) + "-" + rawStudent.get(8));
        //student.set(rawStudent.get()); // 9
        //student.set(rawStudent.get()); // 10
        //student.set(rawStudent.get()); // 11
        //student.set(rawStudent.get()); // 12
        //student.set(rawStudent.get()); // 13
        student.setFirstTermId(Integer.parseInt(rawStudent.get(14)));
        //student.set(rawStudent.get()); // 15
        student.setEmail(rawStudent.get(16));
        student.setSolde(rawStudent.get(17));
        student.setCheminotCode(rawStudent.get(18));
        student.setLibraryCode(rawStudent.get(19));
        //student.set(rawStudent.get()); // 20

        rawUniversitys = this._data.fetchProfileUniversity(permanentCode);
        rawUniversity = rawUniversitys.get(0);

        student.setCurrentTermCredit(Integer.parseInt(rawUniversity.get(1)));

        return student;
    }
}
