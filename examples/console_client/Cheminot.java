package newchem;

import cheminot.communication.Crypto;
import cheminot.communication.DataService;
import cheminot.communication.Service;
import cheminot.exception.NotConnectedException;
import cheminot.exception.ProtocolException;
import newchem.models.User;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import newchem.dao.AdmissionDao;
import newchem.dao.EnrollmentDao;
import newchem.dao.PeriodDao;
import newchem.dao.StudentDao;
import newchem.models.Admission;
import newchem.models.Program;
import newchem.models.Student;
import newchem.models.Enrollment;
import newchem.models.Period;
import newchem.models.Term;

public class Cheminot {

    private final static String USERNAME = ""; // AJXXXXX ou AHXXXXX
    private final static String PASSWORD = ""; // XXXXX

    private final static String DB_CHEMINOT = "ChemiNot";
    private final static String DB_DOSSETUD = "DossEtud";

    private Service _client;
    private DataService _data;

    private User _user;

    private StudentDao _studentDao;
    private AdmissionDao _admissionDao;
    private EnrollmentDao _enrollmentDao;
    private PeriodDao _periodDao;

    private final static Logger LOGGER = Logger.getLogger(Cheminot.class.getName());

    public Cheminot(Service client) {
        LOGGER.setLevel(Level.ALL);
        this._client = client;
        this._data = new DataService(client);
    }

    public void run() throws IOException, NotConnectedException, ProtocolException {
        Integer userType;
        System.out.println("Welcome to Cheminot");
        System.out.println("Connecting...");

        this._client.start(new Crypto());
        this._user = new User(USERNAME, PASSWORD);

        if (!this._client.pingJavelot()) {
            System.out.println("javelot didn't pinged back");
            this._client.shutdown();

            return;
        }

        userType = this._client.login(this._user.getUsername(), this._user.getPassword());

        if (userType == 0) {
            System.out.println("bad credentials for <" + this._user.getUsername() + ">");
            this._client.shutdown();
            return;
        }

        this._user.setType(userType);

        LOGGER.severe("connected!");

        //System.out.println(this._client.getJavelotMotd());
        this._client.send224(DB_CHEMINOT);

        this._studentDao = new StudentDao(this._data);
        this._admissionDao = new AdmissionDao(this._data);
        this._enrollmentDao = new EnrollmentDao(this._data);
        this._periodDao = new PeriodDao(this._data);

        Student student = this._studentDao.get(this._client.fetchUsername());
        Admission admission = this._admissionDao.get(student);
        List<Enrollment> enrollements = this._enrollmentDao.get(student);
        Program program = admission.getProgram();
        List<Period> periods = null;

        System.out.println("Welcome " + student.getFirstname() + " " + student.getLastname());
        System.out.println(program.getLongDescription() + " " + 100 * admission.getCredit() / program.getCredit() + "% completed ("+ admission.getCredit() + "/" + program.getCredit() + " credits).");

        Enrollment oldEnrollment = null, enrollment = null;
        Term term = new Term();

        for (int i=0; i<enrollements.size(); i++){
            enrollment = enrollements.get(i);
            periods = this._periodDao.getByCourseGroupTerm(enrollment.getCourseId(), enrollment.getGroupId(), enrollment.getTermId());
            term.setId(enrollment.getTermId());

            if (oldEnrollment == null || !oldEnrollment.getTermId().equals(enrollment.getTermId())) {
                String session= "Session - ";

                switch (term.getType()) {
                    case FALL:
                        session += "automne";
                        break;
                    case SUMMER:
                        session += "ete";
                        break;
                    case WINTER:
                        session += "hiver";
                        break;
                    default:
                        session += "inconnu";

                }

                session += " " + term.getYear();

                System.out.println(session);
            }

            System.out.println("\t" + enrollment.getCourseId() + " - " + enrollment.getGrade());
            if (periods != null) {
                for (int j=0; j<periods.size(); j++) {
                    Period period = periods.get(j);
                    String periodStr = "\t\t";

                    switch (period.getDay()) {
                        case SUNDAY:
                            periodStr += "dimanche ";
                            break;
                        case MONDAY:
                            periodStr += "lundi ";
                            break;
                        case TUESDAY:
                            periodStr += "mardi ";
                            break;
                        case WEDNESDAY:
                            periodStr += "mercredi ";
                            break;
                        case THURSDAY:
                            periodStr += "jeudi ";
                            break;
                        case FRIDAY:
                            periodStr += "vendredi ";
                            break;
                        case SATURDAY:
                            periodStr += "samedi ";
                            break;
                    }

                    periodStr += period.getBegin()/60 + ":" + period.getBegin()%60 + " - ";
                    periodStr += period.getEnd()/60 + ":" + period.getEnd()%60;

                    System.out.println(periodStr);
                }
            }

            oldEnrollment = enrollment;
        }

        this._client.shutdown();
    }
}
