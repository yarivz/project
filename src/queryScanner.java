import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class queryScanner{
	Scanner scan;
	Properties prop;
	
	public queryScanner(Properties prop)
	{
		scan = new Scanner(System.in);
		this.prop = prop;
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
        Pattern p6 = Pattern.compile("decay");
        Pattern p7 = Pattern.compile("country");
        patVec.add(p1);
        patVec.add(p2);
        patVec.add(p3);
        patVec.add(p4);
        patVec.add(p5);
        patVec.add(p6);
        patVec.add(p7);
        
        
        while (!query.equals("exit")) 
        {
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
            	
            	switch(scase){
            	case "when were":
            		break;
            	case "which people":
            		break;
            	case "when did":
            		break;
            	case "who is":
            		break;
            	case "countries":
            		break;
            	case "decay":
            		break;
            	case "country":
            		break;
            	default:
            		 throw new IllegalArgumentException();
            	}
            }
        }
	}
}