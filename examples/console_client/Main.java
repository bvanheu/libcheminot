package newchem;

import cheminot.communication.Service;
import cheminot.communication.Security;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        try {
            Security security = new Security("SSL(AUTH SSL)", "1.5", 6753568841L, 6753568842L, 6753568843L, 6753568844L);
            //String host = "vertlime.etsmtl.ca";
            String host = "vert.etsmtl.ca";

            Cheminot cheminot = new Cheminot(new Service(host, security));

            cheminot.run();
        }
        catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            throw ex;
        }
    }

}
