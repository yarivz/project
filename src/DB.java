import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class DB {

    static final String user = "root",password = "fixmixboom4";
    static final String url = "jdbc:mysql://localhost:3306/";
    static final String dbName = "wdm";
    static final String createTablePersons = "CREATE TABLE PERSONS " +
            "(id INTEGER not NULL AUTO_INCREMENT, " +
            " name VARCHAR(255) not NULL," +
            " bornIn VARCHAR(255)," +
            " bornAt VARCHAR(255)," +
            " diedIn VARCHAR(255)," +
            " profession VARCHAR(255)," +
            " PRIMARY KEY (id))";
    static final String createTableMusicians = "CREATE TABLE Musicians " +
            "(id INTEGER not NULL AUTO_INCREMENT," +
            "name VARCHAR(255) not NULL," +
            "Type VARCHAR(255)," +
            "Label VARCHAR(255)," +
            "Genre VARCHAR(255)," +
            "PRIMARY KEY (id))";
    static final String createTableCountries = "CREATE TABLE Countries " +
            "(id INTEGER not NULL AUTO_INCREMENT," +
            "name VARCHAR(255) not NULL," +
            "Population VARCHAR(255)," +
            "Capitol VARCHAR(255)," +
            "Area VARCHAR(255)," +
            "PRIMARY KEY (id))";


    public static boolean initDB() {

        boolean ans = false;
        Connection con = null, con2 = null;
        Statement st = null, st2 = null;
        try {
            con = DriverManager.getConnection(url+"?user="+user+"&password="+password);
            st = con.createStatement();
            st.executeUpdate("DROP DATABASE IF EXISTS "+dbName);
            int res= st.executeUpdate("CREATE DATABASE "+dbName);

            if (res!=0) {
                ans = true;
            }

        } catch (SQLException ex) {
            test.lgr.log(Level.SEVERE, ex.getMessage(), ex);
            ans=false;
        }
        if (ans){
            try{
                con2 = DriverManager.getConnection(url+dbName,user,password);
                st2 = con2.createStatement();
                if
                        (st2.executeUpdate(createTablePersons)!=0
                        ||st2.executeUpdate(createTableMusicians)!=0
                        ||st2.executeUpdate(createTableCountries)!=0)
                {
                    test.lgr.log(Level.SEVERE,"Could not create some of the tables");
                    ans=false;
                }
            } catch (SQLException ex) {
                test.lgr.log(Level.SEVERE, ex.getMessage(), ex);
                ans=false;
            }
        }
        try {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
            if (st2 != null) {
                st.close();
            }
            if (con2 != null) {
                con.close();
            }
        } catch (SQLException ex) {
                test.lgr.log(Level.WARNING, ex.getMessage(), ex);
        }
    return ans;
    }


    public void populateDB(){

    }

}



//"CREATE TABLE PERSONS (id INTEGER AUTO_INCREMENT,name VARCHAR(255),bornIn VARCHAR(255),bornAt VARCHAR(255),diedIn VARCHAR(255),profession VARCHAR(255),PRIMARY KEY ( id ))