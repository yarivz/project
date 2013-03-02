import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

public class DB {
	
	TextParser parser;
	Properties prop;
	Connection con = null, con2 = null;
    Statement st = null;

    static final String user = "root",password = "fixmixboom4";
    static final String url = "jdbc:mysql://localhost:3306/";
    static final String dbName = "wdm";
    static final String createTablePersons = "CREATE TABLE Persons " +
            "(Name VARCHAR(255) not NULL," +
            "BornIn INT," +
        	"BornIn_probability DOUBLE," + 
            "DiedIn INT," +
        	"DiedIn_probability DOUBLE," +
            "Profession VARCHAR(255)," +
        	"Profession_probability DOUBLE," + 
            "PRIMARY KEY (Name))";
    static final String createTableMusicians = "CREATE TABLE Musicians " +
            "(Name VARCHAR(255) not NULL," +
            "Type VARCHAR(255)," +
        	"Type_probability DOUBLE," + 
            "Nationality VARCHAR(255)," +
        	"Nationality_probability DOUBLE," +
        	"Origin VARCHAR(255)," +
            "Genre VARCHAR(255)," +
        	"Genre_probability DOUBLE," + 
            "PRIMARY KEY (Name))";
    static final String createTableCountries = "CREATE TABLE Countries " +
            "(Name VARCHAR(255) not NULL," +
            "Population INT," +
            "Population_probability DOUBLE," +
            "PRIMARY KEY (Name))";

    public DB(TextParser parser, Properties prop)
    {
    	this.parser = parser;
    	this.prop = prop;
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
    	/*Person haim = new Person("haim","1986",1,"2050",1,"singer",0.5);
    	parser.personVec.add(haim);
    	musicalArtist art = new musicalArtist("haim","pop","singer","Israeli");
    	parser.artistVec.add(art);
    	Country israel = new Country("Israel","1000",0.5);
    	parser.countryVec.add(israel);
    	Country spain = new Country("Spain","2000",0.9);
    	parser.countryVec.add(spain);
    	Country germany = new Country("Germany","3000",0.4);
    	parser.countryVec.add(germany);
    	Country france = new Country("France","500",0.25);
    	parser.countryVec.add(france);
    	Country egypt = new Country("Egypt","7000",0.15);
    	parser.countryVec.add(egypt);*/
    	
    	
    	Iterator<Person> itrp = parser.personVec.iterator();
    	Iterator<musicalArtist> itrm = parser.artistVec.iterator();
    	Iterator<Country> itrc = parser.countryVec.iterator();
    	
    	while(itrp.hasNext())
    	{  		
    		Person person = itrp.next();
    		 try {
	            st.executeUpdate("INSERT INTO Persons VALUES ('"+person.name+"','"+Integer.parseInt(person.bornIn)+"','"+person.prBornIn+"','"+Integer.parseInt(person.diedIn)+"','"+person.prDiedIn+"','"+person.profession+"','"+person.prProf+"')");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
             
    	}
    	
    	while(itrm.hasNext())
    	{
    		musicalArtist artist = itrm.next();
    		String origin = prop.getProperty(artist.nationality);
    		 try {
	            st.executeUpdate("INSERT INTO Musicians VALUES ('"+artist.name+"','"+artist.type+"','"+artist.prType+"','"+artist.nationality+"','"+artist.prNationality+"','"+origin+"','"+artist.genre+"','"+artist.prGenre+"')");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
             
    	}
    	
    	while(itrc.hasNext())
    	{
    		Country country = itrc.next();
    		 try {
	            st.executeUpdate("INSERT INTO Countries VALUES ('"+country.name+"','"+Integer.parseInt(country.population)+"','"+country.prPopulation+"')");
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    	}
    }
    
    /////////
    //TO-DO: multiply by heuristic's general probability
    /////////
    
    public void query1(String query)
    {
    	 try {
    		  ResultSet rs = st.executeQuery("SELECT DISTINCT BornIn, BornIn_probability FROM Persons WHERE Name = '"+query.subSequence(10, query.lastIndexOf(' '))+"';");
	          while(rs.next())
	        	  System.out.println(rs.getInt("BornIn")+" Probability: "+rs.getDouble("BornIn_probability")); 
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    }
    
    public void query2(String query)
    {
    	try {
  		  ResultSet rs = st.executeQuery("SELECT DISTINCT Name, BornIn_probability, Profession_probability FROM Persons WHERE Profession = '"+query.subSequence(33, query.lastIndexOf(','))+"' AND BornIn = '"+query.substring(query.lastIndexOf(' ')+1)+"';");
	          while(rs.next())
	        	System.out.println(rs.getString("Name")+" Probability: "+(rs.getDouble("BornIn_probability")*rs.getDouble("Profession_probability")));  
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    }
    
    public void query3(String query)
    {
    	 try {
   		  ResultSet rs = st.executeQuery("SELECT DISTINCT DiedIn, DiedIn_probability FROM Persons WHERE Name = '"+query.subSequence(9, query.lastIndexOf(' '))+"';");
	          while(rs.next())
	        	  System.out.println(rs.getInt("DiedIn")+" Probability: "+rs.getDouble("DiedIn_probability")); 
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    }
    
    public void query4(String query)
    {
    	 try {
      		  ResultSet rs = st.executeQuery("SELECT DISTINCT Type, Type_probability, Nationality, Nationality_probability, Genre, Genre_probability FROM Musicians WHERE Name = '"+query.substring(14)+"';");
   	          while(rs.next())
   	        	  System.out.println("Type: "+rs.getString("Type")+" Nationality: "+rs.getString("Nationality")+" Genre: "+rs.getString("Genre")+" Probability: "+(rs.getDouble("Type_probability")*rs.getDouble("Nationality_probability")*rs.getDouble("Genre_probability"))); 
   			} catch (SQLException ex) {
   				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
   			}
    }
    
    public void query5(String query)
    {
    	try {
     		  ResultSet rs = st.executeQuery("SELECT DISTINCT Name, Population_probability FROM Countries ORDER BY Population DESC LIMIT "+query.substring(query.indexOf('-')+1, query.lastIndexOf('p')-3)+";");
  	          while(rs.next())
  	        	  System.out.println(rs.getString("Name")+" Probability: "+rs.getDouble("Population_probability")); 
  			} catch (SQLException ex) {
  				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
  			}
    }
    
    public void query6(String query)
    {
    	try {
    		int decay = Integer.parseInt(query.substring(42, query.lastIndexOf(' ')));
    		ResultSet rs = st.executeQuery("SELECT Name, BornIn_probability FROM Persons JOIN Musicians ON Persons.name = Musicians.name WHERE BornIn BETWEEN "+decay+"AND "+(decay+9)+";");
	        while(rs.next())
	        	System.out.println(rs.getString("Name")+" Probability: "+rs.getDouble("BornIn_probability")); 
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    }
    
    public void query7(String query)
    {
    	try {
   		  ResultSet rs = st.executeQuery("SELECT Musicians.Name, Nationality_probability FROM Musicians JOIN Countries ON Musicians.Origin = Countries.name WHERE Musicians.Origin = '"+query.substring(46)+"';");
	          while(rs.next())
	        	  System.out.println(rs.getString("Name")+" Probability: "+rs.getDouble("Nationality_probability")); 
			} catch (SQLException ex) {
				projectMain.lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
    }
}