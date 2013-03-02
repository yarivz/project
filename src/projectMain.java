import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class projectMain {

    public static Logger lgr = Logger.getLogger("wdm.Logger");
    static FileHandler fh;
    public static void main(String[] args) throws FileNotFoundException{
    	//loading the country&nationality file
        Properties prop = new Properties();
 
        FileInputStream fstream;
        DataInputStream in;
        try{
    		fstream = new FileInputStream("country&nationality.txt");
    		in = new DataInputStream(fstream);
        	prop.load(in);
        	
    
  
        	
	    	} catch (IOException e){
	        System.out.println("Could not read file "+"country&nationality.txt");
	        return;
	    	}

        
        try {
			in.close();
		} catch (IOException e) {
			System.out.println("Could not close file "+"country&nationality.txt");
			return;
		}
    	
        try{
            fh =  new FileHandler("wdm.log");
            lgr.addHandler(fh);
        } catch (IOException ex){
            System.out.println("could not create log file wdm.log\n");
        }
        DB db = new DB(new TextParser(args),prop);
        boolean ret = db.initDB();
        if(ret) System.out.println("The DB and tables were created\n");
        else  System.out.println("something went wrong, please check the log\n");

        
       try {
        	System.out.println("Processing input files. This might take a few minutes. Feel free to grab some coffee.");
			db.parser.run();
		} catch (IOException e) {
			System.out.println("Parser error!\n");
		}
        
        
        //filling db with our parser vectors
        db.populateDB();
        
        
        System.out.println("Ready for queries");
        // creating a scanner object for reciving and processing queries from user
        queryScanner qScanner = new queryScanner(db);
        qScanner.run();
        qScanner.scan.close();		//closing scanner
        try {						//closing connection to db
            if (qScanner.db.st != null) {
            	qScanner.db.st.close();
            }
            if (qScanner.db.con != null) {
            	qScanner.db.con.close();
            }
            
            if (qScanner.db.con2 != null) {
            	qScanner.db.con2.close();
            }
        } catch (SQLException ex) {
        	projectMain.lgr.log(Level.WARNING, ex.getMessage(), ex);
        }


        /*Iterator<Person> itrp = db.parser.personVec.iterator();
        int k=1;
        while(itrp.hasNext())
        {
            Person p = itrp.next();
            System.out.println(k+". "+p.name+" "+p.bornIn+" " +p.diedIn+" "+p.profession);
            k++;

        }
        
        Iterator<musicalArtist> itr = db.parser.artistVec.iterator();
        int r=1;
        while(itr.hasNext())
        {
        	musicalArtist art = itr.next();
        	System.out.println(r+". "+art.name+" "+art.type+" " +art.nationality+" "+art.genre);
        	r++;
        	
        }*/
    }
}

