package newchem.dao;

import cheminot.communication.DataService;
import newchem.models.Course;
import newchem.models.Group;

/**
 *
 * @author hw
 */
public class GroupDao extends Dao {

    public GroupDao(DataService data) {
        super(data);
    }

    public Group get(Course course, Integer id) {
        return null;
    }
}
