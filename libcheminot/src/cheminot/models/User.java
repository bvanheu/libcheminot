package newchem.models;

/**
 *
 * @author hw
 */
public class User {
    private String _username;
    private String _password;
    private UserType _type;

    public User(String username, String password) {
        this._username = username;
        this._password = password;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String _username) {
        this._username = _username;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public boolean isStudent() {
        return this._type == UserType.STUDENT;
    }

    public boolean isManager() {
        return this._type == UserType.MANAGER;
    }

    public boolean isPrepose() {
        return this._type == UserType.PREPOSE;
    }

    public boolean isProgrammer() {
        return this._type == UserType.PROGRAMMER;
    }

    public boolean isExternal() {
        return this._type == UserType.EXTERNAL;
    }

    public User setType(Integer type) {
        if ((type & 0x1) != 0) {
            if ((type & 0x20) != 0) {
                this._type = UserType.PREPOSE;
                return this;
            }

            this._type = UserType.MANAGER;

            return this;
        }

        if ((type & 0x4) != 0) {
            this._type = UserType.STUDENT;
            return this;
        }

        if ((type & 0x2) != 0) {
            this._type = UserType.PROGRAMMER;
            return this;
        }

        if ((type & 0x8) != 0) {
            this._type = UserType.EXTERNAL;
            return this;
        }

        return this;
    }
}
