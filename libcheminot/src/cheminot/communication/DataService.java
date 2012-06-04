package cheminot.communication;

import cheminot.exception.NotConnectedException;
import cheminot.exception.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author hw
 */
public class DataService {

    Service _service;
    String _delimiter = "\003";

    public DataService(Service service) {
        this._service = service;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String _delimiter) {
        this._delimiter = _delimiter;
    }

    /**
     * 005
     * 
     * [annee d'inscription, credit acquis?, programme courant]
     *
     * @param permanentCode
     * @return
     */
    public List<List<String>> fetchProfileUniversity(String permanentCode) {
        List<String> result = new ArrayList<String>();

        this._fetch("005", this._objectToString(permanentCode), result);

        return this._parseDelimiter(result);
    }

    /**
     * 008
     *
     * [code_perm, family_name, first_name, street, city, postal code, indicatif reg, ???, phone, ???, ???, 001?, 1?, S?, 8? session_id, email, solde, code cheminot, code bibliotheque, 1?]
     * 
     * @return
     */
    public List<List<String>> fetchStudent(String permanentCode) {
        List<String> result = new ArrayList<String>();
        
        this._fetch("008", this._objectToString(permanentCode), result);

        return this._parseDelimiter(result);
    }

    /**
     * 010
     * 
     * [program_id, 1?, 02?, 01?, ???, 1?, 0?, 2010-06-16 00:00:00?, I?, ?, ?, ?, 2?, 062010?, 902000?, dec_id, ?, 5220, ?, 620?, REG?, program_desc, program_desc2, session_id_start, session_id_inscrit, 0?, 8?, 0?, credit reussi 29?, 59?, credit inscrit? 30?, moyenne, 5?, ?, moy_max, 1?, 10?, 114?, 0?]
     *
     * @param permanentCode
     * @return
     */
    public List<List<String>> fetchAdmission(String permanentCode) {
        List<String> result = new ArrayList<String>();

        this._fetch("010", this._objectToString(permanentCode), result);

        return this._parseDelimiter(result);
    }

    /**
     * 013
     *
     * [program_id, course_id, group, session_id, note, ???, ???, integer?, 0-1-2?]
     *
     * @param permanentCode
     * @return
     */
    public List<List<String>> fetchEnrollments(String permanentCode) {
        List<String> result = new ArrayList<String>();

        this._fetch("013", this._objectToString(permanentCode), result);

        return this._parseDelimiter(result);
    }

    /**
     * 022
     *
     * [exclusion_msg]
     *
     * @param permanentCode
     * @return
     */
    public List<String> fetchExclusion(String permanentCode){
        List<String> result = new ArrayList<String>();

        this._fetch("022", this._objectToString(permanentCode), result);

        return result;
    }

    /**
     * 027
     * 
     * [course_id, course_group, day, course_begin, course_end, course_type, ???, ???, 0-1?, db_id?]
     *
     * @param sessionId 
     * @return
     */
    public List<List<String>> fetchPeriods(Integer sessionId){
        List<String> result = new ArrayList<String>();

        this._fetch("027", this._objectToString(sessionId), result);

        return this._parseDelimiter(result);
    }

    /**
     * Remove the fucking delimiters
     * 
     * @param data
     * @return
     */
    protected List<List<String>> _parseDelimiter(List<String> data) {
        List<List<String>> newData = new ArrayList<List<String>>();

        for (int i=0; i<data.size(); i++) {
            StringTokenizer st = new StringTokenizer(data.get(i), this._delimiter);
            List<String> parsedData = new ArrayList<String>();
            
            while (st.hasMoreTokens()) {
                parsedData.add(st.nextToken());
            }

            newData.add(parsedData);
        }

        return newData;
    }

    /*
     * Do a SQL query
     *
     * request - data - result
     * 001 - "" - []
     * 002 - "" - [next session?]
     * 003 - "Ssession_id" - [current session, next session]
     * 004 - ?
     *
     * Load test diagnostique
     * 009 - "Scode_perm" - [MS?, SS?]
     *
     * FAIL 011 - "S" - []
     * FAIL 012 - "Scode_perm" - []
     *
     * Load sanction
     * 014 - "Scode_perm" - [???]
     *
     * 
     * 
     * FAIL 017 - "Scode_perm" - ?
     * FAIL 018 - "Scode_perm" - ?
     *
     * 032 - "" - [course_id, course_name, course_credit, ???, ???, ???]
     *
     * SQL is up?
     * 037 - "" - [status]
     *
     * Load remarque
     * 041 - "Scode_perm" - [???]
     * 
     * 042 - "Scode_perm" - [entier?]
     */

    /**
     * Cast object into proper string cheminot representation
     * 
     * @param o
     * @return
     */
    protected String _objectToString(Object o) {
        StringBuilder sb = new StringBuilder("");
        
        if (o == null) {
            return "";
        }
        else if (o instanceof List) {
            List<Object> objects= (List<Object>) o;

            for (int i=0; i < objects.size(); i++){
                sb.append(this._objectToString(objects.get(i)));
            }
        }
        else if (o instanceof String) {
            sb.append('S').append(o);
        }
        else if (o instanceof Integer) {
            sb.append('E').append(((Integer) o).intValue());
        }
        else if (o instanceof Long) {
            sb.append('L').append(((Long) o).longValue());
        }
        else if (o instanceof Double) {
            sb.append('R').append(((Double) o).doubleValue());
        }
        else if (o instanceof Date) {
            sb.append('D').append(((Date) o).getTime());
        }
        else {
            // TODO - error
        }

        sb.append(this._delimiter);

        return sb.toString();
    }

    /**
     * 
     */
    protected void _fetch(String request, String parameters, List<String> result) {
        try {
            this._service.sendString(request, parameters, result);
        }
        catch (NotConnectedException ex) {
            // TODO
        }
        catch (IOException ex) {
            // TODO
        }
        catch (SQLException ex) {
            // TODO
        }
    }
    
}
