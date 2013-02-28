import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class projectMain {

    public static Logger lgr = Logger.getLogger("wdm.Logger");
    static FileHandler fh;
    public static void main(String[] args) throws FileNotFoundException{
        try{
            fh =  new FileHandler("wdm.log");
            lgr.addHandler(fh);
        } catch (IOException ex){
            System.out.println("could not create log file wdm.log\n");
        }
        
        boolean ret = DB.initDB();
        if(ret) System.out.println("The DB and tables were created\n");
        else  System.out.println("something went wrong, please check the log\n");

        TextParser parser = new TextParser(args);
        try {
			parser.run();
		} catch (IOException e) {
			System.out.println("Parser error!\n");
		}
        
        
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
        
        
        Iterator<musicalArtist> itr = parser.artistVec.iterator();
        int r=1;
        while(itr.hasNext())
        {
        	musicalArtist art = itr.next();
        	System.out.println(r+". "+art.name+" "+art.type+" " +art.nationality+" "+art.genre);
        	r++;
        	
        }
    }
}

