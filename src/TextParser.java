import java.io.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    String[] args;
    Vector<Person> personVec;
    Vector<musicalArtist> artistVec;

    public TextParser(String[] args){
        this.args = args;
        personVec = new Vector<Person>();
        artistVec = new Vector<musicalArtist>();
    }
    
    public void run() throws IOException{
    	//try to open and parse args[0] which is full.txt
    	FileInputStream fstream;
    	try{
    		fstream = new FileInputStream(args[0]);
        } catch (FileNotFoundException e){
            System.out.println("Could not read file "+args[0]);
            return;
        }
    	DataInputStream in = new DataInputStream(fstream);
    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	
    	parseFull(br);
    	in.close();
    	
    	//try to open and parse args[1] which is pos.txt
    	try{
    		fstream = new FileInputStream(args[1]);
        } catch (FileNotFoundException e){
            System.out.println("Could not read file "+args[1]);
            return;
        }
    	in = new DataInputStream(fstream);
    	br = new BufferedReader(new InputStreamReader(in));
    	
    	parsePos(br);
    	in.close();
    }
    
    public void parseFull(BufferedReader br) throws IOException{
    	String line = "";
		//Read File Line By Line
   
		while ((line = br.readLine()) != null) 
		{
			String value = "";
			while(!(line.equals(""))){
                value = value.concat(line+'\n');
                line = br.readLine();
            }
			
			if(value.contains("{{Infobox musical artist"))
			{
				musicalArtist artist = new musicalArtist();
				int j = value.indexOf('(');
				int k = value.indexOf('\n');
				if(j!=-1 && j<k)
					artist.name = value.substring(0,j-1);
				else
					artist.name = value.substring(0,k);
				
				Pattern background = Pattern.compile("Background.+?\\\n");
				Matcher mBackground = background.matcher(value);
				Pattern genre = Pattern.compile("Genre.+?\\]");
				Matcher mGenre = genre.matcher(value);
				Pattern nation = Pattern.compile(".*?( is| was| were).*?\\[\\[[A-Z].*?\\|[A-Z][^\\s]*?\\]\\]");
				Matcher mNation = nation.matcher(value);
				
				if(mBackground.find())
				{
					if(mBackground.group(0).contains("singer"))
					{
						artist.type = "singer";
						if(mNation.find())
						{
							//System.out.println(mNation.group(0));
							artist.nationality = mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1);
						}
					}
					else
					{
						artist.type = "band";
						if(mNation.find())
						{
							//System.out.println(mNation.group(0));
							artist.nationality = mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1);
						}
					}
				}
				
				if(mGenre.find())
				{
					//System.out.println(mGenre.group(0));
					int i = mGenre.group(0).indexOf('|');
					if(i!=-1)
						artist.genre = mGenre.group(0).substring(i+1, mGenre.group(0).length()-1);
					else
						artist.genre = mGenre.group(0).substring(mGenre.group(0).indexOf('[')+2, mGenre.group(0).length()-1);
				}

				artistVec.add(artist);
			 }
		  }
		  
		 /*while ((strLine = br.readLine()) != null)
		 {
			 Pattern pattern1 = Pattern.compile("'{3}[a-zA-Z ]{1,}'{3}");
			 Pattern pattern2 = Pattern.compile("\\[\\[([a-zA-Z ]+?)\\]\\]");
			 Matcher matcher1 = pattern1.matcher(strLine);
			 Matcher matcher2 = pattern2.matcher(strLine);
			
			if(strLine.contains("musican") || strLine.contains("singer"))
			{
				musicalArtist artist = new musicalArtist();
				while (matcher1.find()) 
				{
					//System.out.println(matcher1.group(0));
					artist.name = matcher1.group(0).substring(3, matcher1.group(0).length()-3);
					//System.out.println(artist.name);
					artist.type = "singer";
					vec2.add(artist);
				}
			} 
		 }*/
		 
    }
    
    public void parsePos(BufferedReader br) throws IOException{
   
	  String value;
	  //Read File Line By Line
	  int i=1;
	  int a=1;
	  while ((value = br.readLine()) != null)
	  {
		  if(value.equals(""))
			  break;
		  String data = "";
		  String temp;
		  boolean gap=false;
		  while(!gap)
		  {
			  temp = br.readLine();
			  if(!temp.equals(""))
				  data = data+temp;
			  else
				  gap = true;
		  }
		  if(value.contains("+"))
			  continue;
		  
		  String altValue = value;
		  while(altValue.indexOf(" ")!=-1)
		  {
			  int index = altValue.indexOf(" ");
			  altValue = altValue.substring(0, index)+"[^\\.]+?"+altValue.substring(index+1);
		  }
		 
		  musicalArtist art = new musicalArtist();
		  String strSinger = altValue+"[^\\.]+?"+"(is/VBZ|was/VBD)[^\\.]+?(singer/NN|musician/NN)";
		  Pattern singer = Pattern.compile(strSinger);
		  Matcher mSinger = singer.matcher(data);
		  if (mSinger.find()) 
		  {
			art.name = value;
			art.type = "singer";
			Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");
			Matcher mNation = nation.matcher(mSinger.group(0));
			if(mNation.find())
			{
				art.nationality = mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'));
				//System.out.println(a+". "+mNation.group(0));
				//a++;
			}
			
			Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (singer/NN|musician/NN)");
			Matcher mGenre = genre.matcher(mSinger.group(0));
			if(mGenre.find())
			{
				art.genre = mGenre.group(0);
				//System.out.println(a+". "+mGenre.group(0));
				//a++;
			}
			
		  	//System.out.println(i+". "+value);
		    //System.out.println(mSinger.group(0));
		  	//i++;
		  }
		  
		  String strBand = altValue+"[^\\.]+?"+"(is/VBZ|were/VBD)[^\\.]+?( band/NN)";
		  Pattern band = Pattern.compile(strBand);
		  Matcher mBand = band.matcher(data);
		  if (mBand.find()) 
		  {
			art.name = value;
			art.type = "band";
			Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");
			Matcher mNation = nation.matcher(mBand.group(0));
			if(mNation.find())
			{
				art.nationality = mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'));
				//System.out.println(a+". "+mNation.group(0));
				//a++;
			}
			
			Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (band/NN)");
			Matcher mGenre = genre.matcher(mBand.group(0));
			if(mGenre.find())
			{
				art.genre = mGenre.group(0);
				//System.out.println(a+". "+mGenre.group(0));
				//a++;
			}
			
		  	//System.out.println(i+". "+value);
		    //System.out.println(mBand.group(0));
		  	//i++;
		  }
		  
		  if(!art.name.equals(""))		//search if artist is already in the artists vector
		  {
			  System.out.println(art.genre);
		  }
    	
	  }
    }
}
