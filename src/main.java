import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class main {

    public static Logger lgr = Logger.getLogger("wdm.Logger");
    static FileHandler fh;
    public static void main( String[] args){
        try{
            fh =  new FileHandler("wdm.log");
            lgr.addHandler(fh);
        } catch (IOException ex){
            System.out.println("could not create log file wdm.log");
        }

        boolean ret = DB.initDB();
        if(ret) System.out.println("The DB and tables were created");
        else  System.out.println("something went wrong, please check the log");

        createRelation


    }


}
