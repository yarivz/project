import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    String[] args;
    BufferedReader br;
    Vector<Person> personVec;
    Vector<musicalArtist> artistVec;
    Vector<Country> countryVec;

    public TextParser(String[] args){
        this.args = args;
        personVec = new Vector<Person>();
        artistVec = new Vector<musicalArtist>();
        countryVec = new Vector<Country>();
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
    	br = new BufferedReader(new InputStreamReader(in));
    	
    	parseFull();
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
    	
    	parsePos();
    	in.close();
    }
    
    public void parseFull() throws IOException{
    	String line = "";
		//Read File Line By Line
    	while ((line = br.readLine()) != null) 
		{
			String value = "";
			while(!(line.equals(""))){
                value = value.concat(line+'\n');
                line = br.readLine();
            }
			
			
			extractFullArtist(value);
			//extractFullPerson(value);
			extractFullCountry(value);
		}
    }
    
    public void parsePos() throws IOException{
    	String value;
    	  //Read File Line By Line
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
     		  
     		  
     		 extractPosArtist(value,data);
     		 //extractPosPerson(value,data);
     		 extractPosCountry(value,data);
     	  }
    }
    
    public void extractFullArtist(String value) throws IOException
    {
    	//int counter = 1;

		if(value.contains("{{Infobox musical artist"))
		{
			musicalArtist artist = new musicalArtist();
			artist.prName = 0.99;						// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
			int j = value.indexOf('(');
			int k = value.indexOf('\n');
			if(j!=-1 && j<k)
				artist.name = value.substring(0,j-1);
			else
				artist.name = value.substring(0,k);
			//System.out.print(counter+". "+artist.name+" ");
			//counter++;
			
			Pattern background = Pattern.compile("Background.+?\\\n");
			Matcher mBackground = background.matcher(value);
			Pattern genre = Pattern.compile("Genre.+?\\]");
			Matcher mGenre = genre.matcher(value);
			Pattern nation = Pattern.compile(".*?( is| was| were).*?\\[\\[[A-Z].*?\\|[A-Z][^\\s]*?\\]\\]");
			Matcher mNation = nation.matcher(value);
			
			if(mBackground.find())
			{
				if(mBackground.group(0).contains("singer") || mBackground.group(0).contains("Singer"))
				{
					artist.type = "singer";
					artist.prType = 0.94;			// in this heuristic, probability for type is 0.94 (according to manual sampling)
					//System.out.print(artist.type+" ");
					if(mNation.find())
					{
						artist.prNationality = 0.5;	// in this heuristic, probability for nationality is 0.5 (according to manual sampling)
						artist.nationality = mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1);
						//System.out.print(artist.nationality+" ");
					}
				}
				else if(mBackground.group(0).contains("band") || mBackground.group(0).contains("Band"))
				{
					artist.type = "band";
					artist.prType = 0.94;		// in this heuristic, probability for type is 0.94 (according to manual sampling)
					//System.out.print(artist.type+" ");
					if(mNation.find())
					{
						artist.prNationality = 0.5;	// in this heuristic, probability for nationality is 0.5 (according to manual sampling)
						artist.nationality = mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1);
						//System.out.print(artist.nationality+" ");
					}
				}
			}
			
			if(mGenre.find())
			{
				int i = mGenre.group(0).indexOf('|');
				if(i!=-1)
				{
					artist.prGenre = 0.96;		// in this heuristic, probability for genre is 0.96 (according to manual sampling)
					artist.genre = mGenre.group(0).substring(i+1, mGenre.group(0).length()-1).toLowerCase();
				}
				else
				{
					artist.prGenre = 0.96;		// in this heuristic, probability for genre is 0.96 (according to manual sampling)
					artist.genre = mGenre.group(0).substring(mGenre.group(0).indexOf('[')+2, mGenre.group(0).length()-1).toLowerCase();
				}
				//System.out.println(artist.genre);
			}
			else
				//System.out.println("");

			artistVec.add(artist);			// adding the new artist to our artist vector
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
    
    public void extractPosArtist(String value, String data) throws IOException
    {
  	  //int i=1;
  	  //int a=1;
  	 
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
		art.prName = 0.99;				// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
		art.type = "singer";
		art.prType = 0.96;				// in this heuristic, probability for type is 0.96 (according to manual sampling)
		Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");
		Matcher mNation = nation.matcher(mSinger.group(0));
		if(mNation.find())
		{
			art.prNationality = 0.62;	// in this heuristic, probability for nationality is 0.62 (according to manual sampling)
			art.nationality = mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'));
			//System.out.println(a+". "+mNation.group(0));
			//a++;
		}
		
		Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (singer/NN|musician/NN)");
		Matcher mGenre = genre.matcher(mSinger.group(0));
		if(mGenre.find())
		{
			int index = mGenre.group(0).lastIndexOf(' ');
			String gen = mGenre.group(0).substring(1,index).toLowerCase();
			
			index = gen.indexOf('\\');
			if(index!=-1)
				gen = gen.substring(index+1);
			
			for(int j=0;j<gen.length();j++)
			{
				if(gen.charAt(j)=='/')
					gen = gen.substring(0,j) + gen.substring(j+3);
			}
			art.prGenre = 0.44;				// in this heuristic, probability for genre is 0.44 (according to manual sampling)
			art.genre = gen;
			//System.out.println(a+". "+art.genre);
			//a++;
		}
		
	  	//System.out.println(i+". "+value);
	    //System.out.println(mSinger.group(0));
	  	//i++;
	  }
	  
	  String strBand = altValue+"[^\\.]+?"+"(is/VBZ|were/VBD|was/VBD)[^\\.]+?( band/NN)";
	  Pattern band = Pattern.compile(strBand);
	  Matcher mBand = band.matcher(data);
	  if (mBand.find()) 
	  {
		art.name = value;
		art.prName = 0.99;					// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
		art.type = "band";
		art.prType = 0.96;					// in this heuristic, probability for type is 0.96 (according to manual sampling)
		Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");
		Matcher mNation = nation.matcher(mBand.group(0));
		if(mNation.find())
		{
			art.prNationality = 0.62;		// in this heuristic, probability for nationality is 0.62 (according to manual sampling)
			art.nationality = mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'));
			//System.out.println(a+". "+mNation.group(0));
			//a++;
		}
		
		Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (band/NN)");
		Matcher mGenre = genre.matcher(mBand.group(0));
		if(mGenre.find())
		{
			int index = mGenre.group(0).lastIndexOf(' ');
			String gen = mGenre.group(0).substring(1,index).toLowerCase();
			
			index = gen.indexOf('\\');
			if(index!=-1)
				gen = gen.substring(index+2);
			
			for(int j=0;j<gen.length();j++)
			{
				if(gen.charAt(j)=='/')
					gen = gen.substring(0,j) + gen.substring(j+3);
			}
			art.prGenre = 0.44;					// in this heuristic, probability for genre is 0.44 (according to manual sampling)
			art.genre = gen;
			//System.out.println(a+". "+art.genre);
			//a++;
		}
		
	  	//System.out.println(i+". "+value);
	    //System.out.println(mBand.group(0));
	  	//i++;
	  }
	  
	  if(!art.name.equals(""))		//search if artist is already in the artists vector
	  {
		 int index = artistVec.indexOf(art);
		 if(index!=-1)
		 {
			 musicalArtist old = artistVec.elementAt(index); // if artist already exist we'll check if he has at least one different property
			 if(!art.genre.equals(old.genre) || !art.nationality.equals(old.nationality) || !art.type.equals(old.type))
				 artistVec.add(art);
		 }
		 else
			 artistVec.add(art);
	  }
    }

    public void extractFullCountry(String value) throws IOException
    {
    	
    }
    
    public void extractPosCountry(String value, String data) throws IOException
    {
    	
    }
}
