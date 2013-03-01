import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;

public class DB {
	
	TextParser parser;
	Connection con = null, con2 = null;
    Statement st = null;

    static final String user = "root",password = "fixmixboom4";
    static final String url = "jdbc:mysql://localhost:3306/";
    static final String dbName = "wdm";
    static final String createTablePersons = "CREATE TABLE Persons " +
            "Name VARCHAR(255) not NULL," +
    		"Name_probability DOUBLE," + 
            "BornIn INT," +
        	"BornIn_probability DOUBLE," + 
            "DiedIn INT," +
        	"DienIn_probability DOUBLE," + 
            "Profession VARCHAR(255)," +
        	"Profession_probability DOUBLE," + 
            "PRIMARY KEY (Name))";
    static final String createTableMusicians = "CREATE TABLE Musicians " +
            "Name VARCHAR(255) not NULL," +
        	"Name_probability DOUBLE," + 
            "Type VARCHAR(255)," +
        	"Type_probability DOUBLE," + 
            "Nationality VARCHAR(255)," +
        	"Nationality_probability DOUBLE" + 
            "Genre VARCHAR(255)," +
        	"Genre_probability DOUBLE," + 
            "PRIMARY KEY (Name))";
    static final String createTableCountries = "CREATE TABLE Countries " +
            "Name VARCHAR(255) not NULL," +
            "Name_probability DOUBLE," + 
            "Population INT," +
            "Popoulation_probability DOUBLE," + 
            "PRIMARY KEY (Name))";

    public DB(TextParser parser)
    {
    	this.parser = parser;
    }
    
    public boolean initDB() {

        boolean ans = false;
        try {
            con = DriverManager.getConnection(url+"?user="+user+"&password="+password);
            st = con.createStatement();
            st.executeUpdate("DROP DATABASE IF EXISTS "+dbName);
            int res= st.executeUpdate("CREATE DATABASE "+dbName);

            if (res!=0) {
                ans = true;
            }

        } catch (SQLException ex) {
            projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
            ans=false;
        }
        if (ans){
            try{
                con2 = DriverManager.getConnection(url+dbName,user,password);
                st = con2.createStatement();
                if
                        (st.executeUpdate(createTablePersons)!=0
                        ||st.executeUpdate(createTableMusicians)!=0
                        ||st.executeUpdate(createTableCountries)!=0)
                {
                	projectMain.lgr.log(Level.SEVERE,"Could not create some of the tables");
                    ans=false;
                }
            } catch (SQLException ex) {
            	projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
                ans=false;
            }
        }
        
    return ans;
    }
    
    public void populateDB(){
    	Iterator<Person> itrp = parser.personVec.iterator();
    	Iterator<musicalArtist> itrm = parser.artistVec.iterator();
    	Iterator<Country> itrc = parser.countryVec.iterator();
    	
    	while(itrp.hasNext())
    	{  		
    		Person person = itrp.next();
    		 try {
	            st.executeUpdate("INSERT INTO Persons -> VALUES ('"+person.name+"','"+person.prName+"','"+Integer.parseInt(person.bornIn)+"','"+person.prBornIn+"','"+Integer.parseInt(person.diedIn)+"','"+"','"+person.prDiedIn+"','"+"','"+person.profession+"','"+"','"+person.prProf+"','"+")");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
             
    	}
    	
    	while(itrm.hasNext())
    	{
    		musicalArtist artist = itrm.next();
    		 try {
	            st.executeUpdate("INSERT INTO Musicians -> VALUES ('"+artist.name+"','"+artist.prName+"','"+artist.type+"','"+artist.prType+"','"+artist.nationality+"','"+"','"+artist.prNationality+"','"+"','"+artist.genre+"','"+"','"+artist.prGenre+"','"+")");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
             
    	}
    	
    	while(itrc.hasNext())
    	{
    		Country country = itrc.next();
    		 try {
	            st.executeUpdate("INSERT INTO Countries -> VALUES ('"+country.name+"','"+country.prName+"','"+Integer.parseInt(country.population)+"','"+country.prPopulation+")");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    	}
    }
}