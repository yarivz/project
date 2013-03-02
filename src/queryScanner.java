import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class queryScanner{
	Scanner scan;
	DB db;
	
	public queryScanner(DB db)
	{
		scan = new Scanner(System.in);
		this.db = db;
	}
	
	public void run()
	{
		String query = "";
		String scase = "";
        
        Vector<Pattern> patVec = new Vector<Pattern>();
        Pattern p1 = Pattern.compile("when were");
        Pattern p2 = Pattern.compile("which people");
        Pattern p3 = Pattern.compile("when did");
        Pattern p4 = Pattern.compile("who is");
        Pattern p5 = Pattern.compile("countries");
        Pattern p6 = Pattern.compile("decade");
        Pattern p7 = Pattern.compile("country");
        Pattern p8 = Pattern.compile("exit");
        patVec.add(p1);
        patVec.add(p2);
        patVec.add(p3);
        patVec.add(p4);
        patVec.add(p5);
        patVec.add(p6);
        patVec.add(p7);
        patVec.add(p8);
        
      
        while (!query.equals("exit")) 
        {
        	System.out.println("");
        	System.out.println("Please insert your query");
        	Matcher matcher;
            if(scan.hasNextLine())
            {
            	query = scan.nextLine();
            	Iterator<Pattern> itr = patVec.iterator();
            	while(itr.hasNext())
            	{
            		Pattern pattern = itr.next();
            		matcher = pattern.matcher(query); 
            		if(matcher.find())
            		{
            			scase = matcher.group(0);
            			break;
            		}
            	}
            	
            }
           
            	switch(scase)
            	{
		        	case "when were":
		        		db.query1(query);
		        		break;
		        	case "which people":
		        		db.query2(query);
		        		break;
		        	case "when did":
		        		db.query3(query);
		        		break;
		        	case "who is":
		        		db.query4(query);
		        		break;
		        	case "countries":
		        		db.query5(query);
		        		break;
		        	case "decade":
		        		db.query6(query);
		        		break;
		        	case "country":
		        		db.query7(query);
		        		break;
		        	case "exit":
		        		break;
		        	default:
		        		 System.out.println("Problem with the insreted query. Please check your syntax");
            	}
        }
        scan.close();
    }
		
}