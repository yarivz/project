import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;


public class DB {
	
	TextParser parser;
	Properties prop;
	Connection con = null, con2 = null;
    Statement st = null;
    boolean flag;

    static final String url = "jdbc:mysql://localhost:3306/";
    static final String dbName = "wdm";
    static final String createTablePersons = "CREATE TABLE Persons " +
    		"(Ind INT not NULL AUTO_INCREMENT," +
            "Name VARCHAR(255) not NULL," +
            "BornIn INT," +
        	"BornIn_probability DOUBLE," + 
            "DiedIn INT," +
        	"DiedIn_probability DOUBLE," +
            "Profession VARCHAR(255)," +
        	"Profession_probability DOUBLE," + 
            "PRIMARY KEY (Ind))";
    static final String createTableMusicians = "CREATE TABLE Musicians " +
    		"(Ind INT not NULL AUTO_INCREMENT," +
            "Name VARCHAR(255) not NULL," +
            "Type VARCHAR(255)," +
        	"Type_probability DOUBLE," + 
            "Nationality VARCHAR(255)," +
        	"Nationality_probability DOUBLE," +
        	"Origin VARCHAR(255)," +
            "Genre VARCHAR(255)," +
        	"Genre_probability DOUBLE," + 
            "PRIMARY KEY (Ind))";
    static final String createTableCountries = "CREATE TABLE Countries " +
    		"(Ind INT not NULL AUTO_INCREMENT," +
    		"Name VARCHAR(255) not NULL," +
            "Population INT," +
            "Population_probability DOUBLE," +
            "PRIMARY KEY (Ind))";

    public DB(TextParser parser, Properties prop)
    {
    	this.parser = parser;
    	this.prop = prop;
    	flag = false;
    }
    
    public boolean initDB(String user, String password) {

        boolean ans = false;
        try {
            con = DriverManager.getConnection(url+"?user="+user+"&password="+password);
            st = con.createStatement();
            st.executeUpdate("DROP DATABASE IF EXISTS "+dbName);
            int res= st.executeUpdate("CREATE DATABASE "+dbName);

            if (res!=0) {
                ans = true;
            }

        } catch (Exception ex) {
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
                    ans=false;
                }
            } catch (SQLException ex) {
                ans=false;
            }
        }
        
    return ans;
    }
    
    public void populateDB(){
    	/*Person haim = new Person("'haim'","'1989'",0.9,"'2050'",1,"'singer'",0.5);
    	parser.personVec.add(haim);
    	Person haim2 = new Person("'haim'","'1989'",1,"'2050'",1,"'singer'",0.7);
    	parser.personVec.add(haim2);
    	musicalArtist art = new musicalArtist("'haim'","'pop'","'singer'","'English'");
    	parser.artistVec.add(art);
    	Country israel = new Country("'England'","'1000'",0.5);
    	parser.countryVec.add(israel);
    	Country spain = new Country("'Spain'","'2000'",0.9);
    	parser.countryVec.add(spain);
    	Country germany = new Country("'Germany'","'3000'",0.4);
    	parser.countryVec.add(germany);
    	Country france = new Country("'France'","'500'",0.25);
    	parser.countryVec.add(france);
    	Country egypt = new Country("'Egypt'","'7000'",0.15);
    	parser.countryVec.add(egypt);*/
    	
    	
    	Iterator<Person> itrp = parser.personVec.iterator();
    	Iterator<musicalArtist> itrm = parser.artistVec.iterator();
    	Iterator<Country> itrc = parser.countryVec.iterator();
    	
    	while(itrp.hasNext())
    	{  		
    		Person person = itrp.next();
			try {
	            st.executeUpdate("INSERT INTO Persons VALUES ('0',"+person.name+","+person.bornIn+",'"+person.prBornIn+"',"+person.diedIn+",'"+person.prDiedIn+"',"+person.profession+",'"+person.prProf+"')");
			} catch (SQLException ex) {
			}
             
    	}
    	
    	while(itrm.hasNext())
    	{
    		musicalArtist artist = itrm.next();
    		String origin = prop.getProperty(artist.nationality);
    		 try {
	            st.executeUpdate("INSERT INTO Musicians VALUES ('0',"+artist.name+","+artist.type+",'"+artist.prType+"',"+artist.nationality+",'"+artist.prNationality+"',"+origin+","+artist.genre+",'"+artist.prGenre+"')");
			} catch (SQLException ex) {
			}
             
    	}
    	
    	while(itrc.hasNext())
    	{
    		Country country = itrc.next();
    		 try {
	            st.executeUpdate("INSERT INTO Countries VALUES ('0',"+country.name+","+country.population+",'"+country.prPopulation+"')");
			} catch (SQLException ex) {
			}
    	}
    }
    
    public void query1(String query) throws RuntimeException
    {
    	 try {
    		  ResultSet rs = st.executeQuery("SELECT DISTINCT BornIn, BornIn_probability FROM Persons WHERE Name = '"+query.subSequence(10, query.lastIndexOf(' '))+"' AND BornIn IS NOT NULL ORDER BY BornIn_probability DESC;");
    		 
	          while(rs.next())
	          {
	        	  System.out.println(rs.getInt("BornIn")+" Probability: "+rs.getDouble("BornIn_probability"));
	        	  flag = true;
	          }
	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
			} catch (SQLException ex) {
			}
    }
    
    public void query2(String query) throws RuntimeException
    {
    	try {
  		  ResultSet rs = st.executeQuery("SELECT DISTINCT Name, BornIn_probability, Profession_probability FROM Persons WHERE Profession = '"+query.subSequence(33, query.lastIndexOf(','))+"' AND BornIn = '"+query.substring(query.lastIndexOf(' ')+1)+"';");
	          while(rs.next())
	          {
	        	System.out.println(rs.getString("Name")+" Probability: "+(rs.getDouble("BornIn_probability")*rs.getDouble("Profession_probability")));
	        	flag = true;
	          }
	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
			} catch (SQLException ex) {
			}
    }
    
    public void query3(String query) throws RuntimeException
    {
    	 try {
   		  ResultSet rs = st.executeQuery("SELECT DISTINCT DiedIn, DiedIn_probability FROM Persons WHERE Name = '"+query.subSequence(9, query.lastIndexOf(' '))+"' AND DiedIn IS NOT NULL ORDER BY DiedIn_probability DESC;");
	          while(rs.next())
	          {
	        	  System.out.println(rs.getInt("DiedIn")+" Probability: "+rs.getDouble("DiedIn_probability"));
	        	  flag = true;
	          }
	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
			} catch (SQLException ex) {
			}
    }
    
    public void query4(String query) throws RuntimeException
    {
    	 try {
      		  ResultSet rs = st.executeQuery("SELECT DISTINCT Type, Type_probability, Nationality, Nationality_probability, Genre, Genre_probability FROM Musicians WHERE Name = '"+query.substring(14)+"' AND Type IS NOT NULL AND Nationality IS NOT NULL AND Genre IS NOT NULL;");
   	          while(rs.next())
   	          {
   	        	  if(rs.getString("Type").equals("NULL"))
   	        		  System.out.println("Type: Unknown");
   	        	  else
   	        		System.out.println("Type: "+rs.getString("Type"));
   	        	  if(rs.getString("Nationality").equals("NULL"))
 	        		  System.out.println(" Nationality: Unknown");
 	        	  else
 	        		System.out.println(" Nationality: "+rs.getString("Nationality"));
   	        	  if(rs.getString("Genre").equals("NULL"))
 	        		  System.out.println(" Genre: Unknown");
 	        	  else
 	        		System.out.println(" Genre: "+rs.getString("Genre"));
   	        	  System.out.println(" Probability: "+(rs.getDouble("Type_probability")*rs.getDouble("Nationality_probability")*rs.getDouble("Genre_probability")));
   	        	  flag = true;
   	          }
   	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
   			} catch (SQLException ex) {
   			}
    }
    
    public void query5(String query) throws RuntimeException
    {
    	try {
     		  ResultSet rs = st.executeQuery("SELECT DISTINCT Name, Population, Population_probability FROM Countries ORDER BY Population DESC LIMIT "+query.substring(query.indexOf('-')+1, query.lastIndexOf('p')-3)+";");
  	          while(rs.next())
  	          {
  	        	  System.out.println(rs.getString("Name")+" Population: "+rs.getInt("Population")+" Probability: "+rs.getDouble("Population_probability"));
  	        	  flag = true;
  	          }
  	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
  			} catch (SQLException ex) {
  			}
    }
    
    public void query6(String query) throws RuntimeException
    {
    	try {
    		int decade = Integer.parseInt(query.substring(42, query.lastIndexOf(' ')));
    		ResultSet rs = st.executeQuery("SELECT Musicians.Name, BornIn, BornIn_probability FROM Persons JOIN Musicians ON Persons.name = Musicians.name WHERE BornIn BETWEEN "+decade+" AND "+(decade+9)+" ORDER BY BornIn_probability DESC;");
	        while(rs.next())
	        {
	        	System.out.println(rs.getString("Name")+" Born In: "+rs.getInt("BornIn")+" Probability: "+rs.getDouble("BornIn_probability"));
	        	flag = true;
	        }
	        if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
			} catch (SQLException ex) {
			}
    }
    
    public void query7(String query) throws RuntimeException
    {
    	try {
   		  	ResultSet rs = st.executeQuery("SELECT Musicians.Name, Nationality_probability FROM Musicians JOIN Countries ON Musicians.Origin = Countries.name WHERE Musicians.Origin = '"+query.substring(46)+"' ORDER BY Nationality_probability DESC;");
	          while(rs.next())
	          {
	        	  System.out.println(rs.getString("Name")+" Probability: "+rs.getDouble("Nationality_probability"));
	        	  flag = true;
	          }
	          if(!flag)
	        	  System.out.println("Unknown");
	          flag = false;
			} catch (SQLException ex) {
			}
    }
}