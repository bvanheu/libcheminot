package newchem.models;

/**
 *
 * @author hw
 */
public class Grade {
    
    private String _grade;

    public Grade(String grade) {
        this._grade = grade.trim().toUpperCase();
    }

    public String getGrade() {
        return _grade;
    }

    public boolean isGrade() {
        return this._grade.matches("^[ABCDE]{1}[+-]{0,1}$");
    }

    public Float toFloat() {
        if (_grade.equals("A+")) {
            return new Float(4.3);
        }
        else if (_grade.equals("A")) {
            return new Float(4.0);
        }
        else if (_grade.equals("A-")) {
            return new Float(3.7);
        }
        else if (_grade.equals("B+")) {
            return new Float(3.3);
        }
        else if (_grade.equals("B")) {
            return new Float(3.0);
        }
        else if (_grade.equals("B-")) {
            return new Float(2.7);
        }
        else if (_grade.equals("C+")) {
            return new Float(2.3);
        }
        else if (_grade.equals("C")) {
            return new Float(2.0);
        }
        else if (_grade.equals("C-")) {
            return new Float(1.7);
        }
        else if (_grade.equals("D+")) {
            return new Float(1.3);
        }
        else if (_grade.equals("D")) {
            return new Float(1.0);
        }
        else if (_grade.equals("D-")) {
            return new Float(0.7);
        }

        return new Float(0.0);
    }

    @Override
    public String toString() {
        if (this.isGrade()) {
            return this.toFloat().toString();
        }
        else {
            if (this._grade.equals("AX")) {
                return "abandon avec remboursement";
            }
            else if (this._grade.equals("I")) {
                return "incomplet";
            }
            else if (this._grade.equals("XX")) {
                return "abandon sans remboursement";
            }
            else if (this._grade.equals("L")) {
                return "cours echoue, repris et reussi";
            }
            else if (this._grade.equals("K")) {
                return "exemption par equivalence";
            }
            else if (this._grade.equals("S")) {
                return "exigences satisfaites";
            }
            else if (this._grade.equals("H/?")) {
                return "cours hors-programme";
            }
            else if (this._grade.equals("R/?")) {
                return "equivalence reconnaissance";
            }
            else if (this._grade.equals("UX")) {
                return "annulation par l'ecole";
            }
            else if (this._grade.equals("FX")) {
                return "annulation par l'ecole pour solde du";
            }
            else if (this._grade.equals("RX")) {
                return "annulation faute de prealable";
            }
            else if (this._grade.equals("FX")) {
                return "echec du a un incomplet non transforme";
            }
            else if (this._grade.equals("V")) {
                return "exigences satisfaites (n'influe pas la moyenne)";
            }
            else {
                return "";
            }
        }
    }
}
