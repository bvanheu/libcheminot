package newchem.models;

/**
 *
 * @author hw
 */
public class Person {
    
    private String _firstname;
    private String _lastname;
    private String _address;
    private String _city;
    private String _postalCode;
    private String _phone;
    private String _email;
    private String _cheminotCode;

    public Person() {
        
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String _address) {
        this._address = _address;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String _email) {
        this._email = _email;
    }

    public String getFirstname() {
        return _firstname;
    }

    public void setFirstname(String _firstname) {
        this._firstname = _firstname;
    }

    public String getLastname() {
        return _lastname;
    }

    public void setLastname(String _lastname) {
        this._lastname = _lastname;
    }

    public String getPhone() {
        return _phone;
    }

    public void setPhone(String _phone) {
        this._phone = _phone;
    }

    public String getPostalCode() {
        return _postalCode;
    }

    public void setPostalCode(String _postalCode) {
        this._postalCode = _postalCode;
    }

    public String getCheminotCode() {
        return _cheminotCode;
    }

    public void setCheminotCode(String _cheminotCode) {
        this._cheminotCode = _cheminotCode;
    }

    public String getCity() {
        return _city;
    }

    public void setCity(String _city) {
        this._city = _city;
    }
}
