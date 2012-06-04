package newchem.dao;

import cheminot.communication.DataService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import newchem.models.Course;
import newchem.models.Day;
import newchem.models.Group;
import newchem.models.Period;
import newchem.models.Term;

/**
 *
 * @author hw
 */
public class PeriodDao extends Dao {

    private Map<Integer, Map<String, List<Period>>> _cache;

    public PeriodDao(DataService data) {
        super(data);
        this._cache = new HashMap<Integer, Map<String, List<Period>>>();
    }

    public List<Period> get(Integer termId) {
        List<List<String>> rawPeriods;
        List<String> rawPeriod;

        List<Period> periods = new ArrayList<Period>();

        rawPeriods = this._data.fetchPeriods(termId);
        Period period;

        for (int i=0; i<rawPeriods.size(); i++) {
            rawPeriod = rawPeriods.get(i);
            // [course_id, course_group, day, course_begin, course_end, course_type, ???, ???, 0-1?, db_id?]
            period = new Period();
            period.setCourseId(rawPeriod.get(0));
            period.setGroupId(Integer.parseInt(rawPeriod.get(1)));

            switch (Integer.parseInt(rawPeriod.get(2))) {
                case 0:
                    period.setDay(Day.SUNDAY);
                    break;
                case 1:
                    period.setDay(Day.MONDAY);
                    break;
                case 2:
                    period.setDay(Day.TUESDAY);
                    break;
                case 3:
                    period.setDay(Day.WEDNESDAY);
                    break;
                case 4:
                    period.setDay(Day.THURSDAY);
                    break;
                case 5:
                    period.setDay(Day.FRIDAY);
                    break;
                case 6:
                    period.setDay(Day.SATURDAY);
                    break;
            }
            
            period.setBegin(Integer.parseInt(rawPeriod.get(3).substring(0, 1)) * 60 + Integer.parseInt(rawPeriod.get(3).substring(3,4)));
            period.setEnd(Integer.parseInt(rawPeriod.get(4).substring(0, 1)) * 60 + Integer.parseInt(rawPeriod.get(4).substring(3,4)));
            //period.setType();

            period.setId(Integer.parseInt(rawPeriod.get(9)));

            periods.add(period);
        }

        return periods;
    }

    private void _loadPeriod(Integer termId) {
        List<List<String>> rawPeriods;
        List<String> rawPeriod;
        Map<String, List<Period>> periods = new HashMap<String,List<Period>>();

        if (!this._cache.containsKey(termId)) {
            rawPeriods = this._data.fetchPeriods(termId);
            Period period;

            for (int i=0; i<rawPeriods.size(); i++) {
                rawPeriod = rawPeriods.get(i);
                // [course_id, course_group, day, course_begin, course_end, course_type, ???, ???, 0-1?, db_id?]
                period = new Period();
                period.setCourseId(rawPeriod.get(0));
                period.setGroupId(Integer.parseInt(rawPeriod.get(1)));

                switch (Integer.parseInt(rawPeriod.get(2))) {
                    case 0:
                        period.setDay(Day.SUNDAY);
                        break;
                    case 1:
                        period.setDay(Day.MONDAY);
                        break;
                    case 2:
                        period.setDay(Day.TUESDAY);
                        break;
                    case 3:
                        period.setDay(Day.WEDNESDAY);
                        break;
                    case 4:
                        period.setDay(Day.THURSDAY);
                        break;
                    case 5:
                        period.setDay(Day.FRIDAY);
                        break;
                    case 6:
                        period.setDay(Day.SATURDAY);
                        break;
                }


            
                period.setBegin(Integer.parseInt(rawPeriod.get(3).substring(0, 2)) * 60 + Integer.parseInt(rawPeriod.get(3).substring(3,5)));
                period.setEnd(Integer.parseInt(rawPeriod.get(4).substring(0, 2)) * 60 + Integer.parseInt(rawPeriod.get(4).substring(3,5)));
                //period.setType();

                period.setId(Integer.parseInt(rawPeriod.get(9)));

                String key = period.getCourseId() + period.getGroupId();
                List<Period> local_periods;
                if (!periods.containsKey(key)) {
                    periods.put(key, new ArrayList<Period>());
                }
                local_periods = periods.get(key);
                local_periods.add(period);
            }

            this._cache.put(termId, periods);
        }
    }

    public List<Period> getByCourseGroupTerm(Course course, Group group, Term term) {
        return this.getByCourseGroupTerm(course.getId(), group.getId(), term.getId());
    }

    public List<Period> getByCourseGroupTerm(String courseId, Integer groupId, Integer termId) {
        this._loadPeriod(termId);
        return this._cache.get(termId).get(courseId + groupId);
    }
}
