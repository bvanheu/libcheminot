package newchem.dao;

import cheminot.communication.DataService;
import java.util.ArrayList;
import java.util.List;
import newchem.models.Enrollment;
import newchem.models.Grade;
import newchem.models.Student;

/**
 *
 * @author hw
 */
public class EnrollmentDao extends Dao {

    public EnrollmentDao(DataService data) {
        super(data);
    }

    public List<Enrollment> get(String studentId) {
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        
        List<List<String>> rawEnrollements = this._data.fetchEnrollments(studentId);
        List<String> rawEnrollement;
        
        for (int i=0; i<rawEnrollements.size(); i++) {
            rawEnrollement = rawEnrollements.get(i);

            Enrollment enrollment = new Enrollment();

            enrollment.setProgramId(Integer.parseInt(rawEnrollement.get(0)));
            enrollment.setCourseId(rawEnrollement.get(1));
            enrollment.setGroupId(Integer.parseInt(rawEnrollement.get(2)));
            enrollment.setTermId(Integer.parseInt(rawEnrollement.get(3)));
            enrollment.setGrade(new Grade(rawEnrollement.get(4)));
            //enrollment.set(rawEnrollement.get(5)); // 5
            //enrollment.set(rawEnrollement.get(5)); // 6
            //enrollment.set(rawEnrollement.get(5)); // 7
            enrollment.setId(Integer.parseInt(rawEnrollement.get(8)));
            //studentCourse.set(rawEnrollement.get(9)); // 9

            enrollments.add(enrollment);
        }

        return enrollments;
    }

    public List<Enrollment> get(Student student) {
        student.setEnrollments(this.get(student.getId()));
        return student.getEnrollments();
    }
}
