package newchem.dao;

import cheminot.communication.DataService;

/**
 *
 * @author hw
 */
public abstract class Dao {
    protected DataService _data;

    public Dao (DataService data) {
        this._data = data;
    }
}
