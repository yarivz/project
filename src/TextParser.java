import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {

    String[] args;
    BufferedReader br;
    Vector<Person> personVec;       // A vector that holds all Person objects until they are inserted into the DB
    Vector<musicalArtist> artistVec;  // A vector that holds all Musical Artist objects until they are inserted into the DB
    Vector<Country> countryVec;   // A vector that holds all Country objects until they are inserted into the DB
    //==================================================================================================================
    //Patterns for extracting data from the unstructured text
    String YEAR_PATTERN =  "[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}[^0-9a-zA-Z]{1,2}";
    String INFOBOX_PATTERN = "Infobox[^\\.]+";
    String NAME_PATTERN_A = "'''[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*'''";
    String NAME_PATTERN_B = "\\[\\[[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\]\\]";
    String POPULATION_PATTERN = "(populationtotal[^\\n]+?[0-9',. ]+[^0-9',. ])|(population_estimate[^\\n_]+?[0-9',. ]+[^0-9',. ])";
    String INFO_TYPE_PATTERN = "Infobox(_| )[A-Za-z ]+[^A-Za-z ]";
    final int BIRTH_YEAR=1,DEATH_YEAR=2,PROFESSION=3;
    //------------------------------------------------------------------------------------------------------------------
    // Patterns for the semi-structured Full.txt file
    //------------------------------------------------------------------------------------------------------------------
    //Patterns for people's names
    Pattern nameA = Pattern.compile(NAME_PATTERN_A);
    Pattern nameB = Pattern.compile(NAME_PATTERN_B);
    //Patterns for birth year, based on the name patterns (Patterns with less than 10 results were discarded)
    Pattern bornInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    Pattern bornInYearA2 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern bornInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    Pattern bornInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(born"+YEAR_PATTERN);
    Pattern bornInYearB3 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(b\\. "+YEAR_PATTERN);
    Pattern bornInYearB4 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    //Patterns for death year, based on the name patterns (Patterns with less than 10 results were discarded)
    Pattern diedInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    Pattern diedInYearA2 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern diedInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    Pattern diedInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(d\\. "+YEAR_PATTERN);
    Pattern diedInYearB3 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    //Patterns for death & birth year, based on the Infobox patterns
    Pattern bornInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+(B|b)irth(_| )date[^\\n]+"+YEAR_PATTERN);
    Pattern diedInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+death(_| )date[^\\n]+"+YEAR_PATTERN);
    Pattern infoTypePattern = Pattern.compile(INFO_TYPE_PATTERN);
    Pattern yearPattern = Pattern.compile(YEAR_PATTERN);
    //Patterns for Country population, based on Infobox patterns
    Pattern populPattern = Pattern.compile(POPULATION_PATTERN);
    //------------------------------------------------------------------------------------------------------------------
    //Patterns for the tagged POS.txt
    //------------------------------------------------------------------------------------------------------------------
    Pattern personPOS = Pattern.compile("([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+?(is/VBZ|was/VBD)\\s(a/DT|an/DT)([^\\.]+?(or|er|ian|ist)/NN)+?(\\sand/CC([^\\.\\n]+?(or|er|ian|ist)/NN)+)*");
    Pattern bornInPOS = Pattern.compile("^([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}/CD");
    Pattern diedInPOS = Pattern.compile("^([A-Z][a-z]+/NNP\\s){2,}?[^\\.\\n]+[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}/CD[^\\.\\n]+(-/:){1}[^\\.\\n]+[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}/CD");
    Pattern profPOS = Pattern.compile("([A-Z]{0,1}[a-z]+(or|er|ian|ist)/NN)+?(\\sand/CC\\s([A-Z]{0,1}[a-z ]+(or|er|ian|ist)/NN)+)*");
    Pattern yearPOS = Pattern.compile("[1-9][0-9]{2,3}/CD");
    //==================================================================================================================
    //Matchers, instantiated here and used throughout the class in all Person/Country heuristics
    Matcher mat = yearPattern.matcher("");
    Matcher data = yearPattern.matcher("");
    Matcher name = nameA.matcher("");
    //Variables for Person / Country heuristics, each one is explained in the heuristics
    int i;
    String pname="",pdata="",nameLocation="",dataLocation="",nameCleanup="",dataCleanup="",str;
    Pattern nameVar,matcherVar,dataVar;
    double prHeuristic;
    Person p,x; Country c,t;

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
    	
    	parseFull();  //parse the full.txt file and run heuristics on it
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
    	
    	parsePos();  //parse the pos.txt file and run heuristics on it
    	in.close();
    }
    
    public void parseFull() throws IOException{
    	String line = "";
		//Read File Line By Line
    	while ((line = br.readLine()) != null) 
		{
			String value = "";
			while(!(line.equals(""))){      //concatenate entire "values" (separated by empty lines) to a single string
                value = value.concat(line+'\n');
                line = br.readLine();
            }
			//Run all heuristics designed for the full.txt file
			extractFullArtist(value);
			extractFullPerson(value);
			extractFullCountry(value);
		}
    }
    
    public void parsePos() throws IOException{
    	String value;
    	  //Read File Line By Line
    	 while ((value = br.readLine()) != null)     //the "value" is saved in a separate string to be used directly
     	  {
     		  if(value.equals(""))
     			  break;
     		  String data = "";
     		  String temp;
     		  boolean gap=false;
     		  while(!gap)
     		  {
     			  temp = br.readLine();      //concatenate all the lines until the next empty line (assuming they are
     			  if(!temp.equals(""))       //related to the same value
     				  data = data+temp;
     			  else
     				  gap = true;
     		  }
     		  if(value.contains("+"))
     			  continue;
             //Run all the heuristics for the pos.txt file
             extractPosArtist(value,data);
     		 extractPosPerson(value,data);
     	  }
    }

    public void extractFullArtist(String value) throws IOException
    {
        if(value.contains("{{Infobox musical artist"))
        {
            musicalArtist artist = new musicalArtist();
            // string manipulation to get artist's name
            int j = value.indexOf('(');
            int k = value.indexOf('\n');
            if(j!=-1 && j<k)
            {
                artist.name = "'"+value.substring(0,j-1).replaceAll("'", "")+"'";
            }
            else
                artist.name = "'"+value.substring(0,k).replaceAll("'", "")+"'";

            // all the patterns i'm going to look for in the piece of data
            Pattern background = Pattern.compile("Background.+?\\\n");
            Matcher mBackground = background.matcher(value);
            Pattern genre = Pattern.compile("Genre.+?\\]");
            Matcher mGenre = genre.matcher(value);
            Pattern nation = Pattern.compile(".*?( is| was| were).*?\\[\\[[A-Z].*?\\|[A-Z][^\\s]*?\\]\\]");
            Matcher mNation = nation.matcher(value);

            if(mBackground.find())	//searching for artist's type
            {
                if(mBackground.group(0).contains("singer") || mBackground.group(0).contains("Singer"))
                {
                    artist.type = "'singer'";
                    artist.prType = 0.94;			// in this heuristic, probability for type is 0.94 (according to manual sampling)
                    if(mNation.find())
                    {
                        artist.prNationality = 0.5;	// in this heuristic, probability for nationality is 0.5 (according to manual sampling)
                        artist.nationality = "'"+mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1)+"'";
                    }
                    else
                        artist.prNationality = 0.5;		// the opposite probability
                }
                else if(mBackground.group(0).contains("band") || mBackground.group(0).contains("Band"))
                {
                    artist.type = "'band'";
                    artist.prType = 0.94;		// in this heuristic, probability for type is 0.94 (according to manual sampling)
                    if(mNation.find())
                    {
                        artist.prNationality = 0.5;	// in this heuristic, probability for nationality is 0.5 (according to manual sampling)
                        artist.nationality = "'"+mNation.group(0).substring(mNation.group(0).lastIndexOf('|')+1,mNation.group(0).lastIndexOf(']')-1)+"'";
                    }
                    else
                        artist.prNationality = 0.5;		// the opposite probability
                }
            }
            else
                artist.prType = 0.06;					// the opposite probability

            if(mGenre.find())	//searching for artist's genre
            {
                int i = mGenre.group(0).indexOf('|');
                if(i!=-1)
                {
                    artist.prGenre = 0.96;		// in this heuristic, probability for genre is 0.96 (according to manual sampling)
                    artist.genre = "'"+mGenre.group(0).substring(i+1, mGenre.group(0).length()-1).toLowerCase()+"'";
                }
                else
                {
                    artist.prGenre = 0.96;		// in this heuristic, probability for genre is 0.96 (according to manual sampling)
                    artist.genre = "'"+mGenre.group(0).substring(mGenre.group(0).indexOf('[')+2, mGenre.group(0).length()-1).toLowerCase()+"'";
                }
            }
            else
                artist.prGenre = 0.04;			// the opposite probability

            artistVec.add(artist);			// adding the new artist to our artist vector
        }
    }

    public void extractPosArtist(String value, String data) throws IOException
    {
        String altValue = value;
        // string manipulation to get artist's name
        while(altValue.indexOf(" ")!=-1)
        {
            int index = altValue.indexOf(" ");
            altValue = altValue.substring(0, index)+"[^\\.]+?"+altValue.substring(index+1);
        }

        musicalArtist art = new musicalArtist();
        String strSinger = altValue+"[^\\.]+?"+"(is/VBZ|was/VBD)[^\\.]+?(singer/NN|musician/NN)";
        Pattern singer = Pattern.compile(strSinger);		// pattern to find a singer
        Matcher mSinger = singer.matcher(data);
        if (mSinger.find()) 								// searching for a singer
        {
            art.name = "'"+value.replaceAll("'", "")+"'";
            art.type = "'singer'";
            art.prType = 0.96;				// in this heuristic, probability for type is 0.96 (according to manual sampling)
            Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");		// pattern to find a singer's nationality
            Matcher mNation = nation.matcher(mSinger.group(0));
            if(mNation.find())												// searching for singer's nationality
            {
                art.prNationality = 0.62;	// in this heuristic, probability for nationality is 0.62 (according to manual sampling)
                art.nationality = "'"+mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'))+"'";
            }
            else
                art.prNationality = 0.38;		// the opposite probability
            // pattern for singer's genre
            Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (singer/NN|musician/NN)");
            Matcher mGenre = genre.matcher(mSinger.group(0));
            if(mGenre.find())			// searching for a band's genre
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
                art.genre = "'"+gen+"'";
            }
            else
                art.prGenre = 0.56;			// the opposite probability
        }

        String strBand = altValue+"[^\\.]+?"+"(is/VBZ|were/VBD|was/VBD)[^\\.]+?( band/NN)";
        Pattern band = Pattern.compile(strBand);		// pattern to find a band
        Matcher mBand = band.matcher(data);
        if (mBand.find()) 							// searching for a band
        {
            art.name = "'"+value.replaceAll("'", "")+"'";					// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
            art.type = "'band'";
            art.prType = 0.96;					// in this heuristic, probability for type is 0.96 (according to manual sampling)
            Pattern nation = Pattern.compile("([A-Z][a-zA-Z]+?/JJ)");		// pattern to find a band's nationality
            Matcher mNation = nation.matcher(mBand.group(0));
            if(mNation.find())		// searching for band's nationality
            {
                art.prNationality = 0.62;		// in this heuristic, probability for nationality is 0.62 (according to manual sampling)
                art.nationality = "'"+mNation.group(0).substring(0, mNation.group(0).lastIndexOf('/'))+"'";
            }
            else
                art.prNationality = 0.38;		// the opposite probability
            // pattern for singer's genre
            Pattern genre = Pattern.compile("( ([A-Za-z]|\\p{Punct})+?/NN)+? (band/NN)");
            Matcher mGenre = genre.matcher(mBand.group(0));
            if(mGenre.find())		// searching for a band's genre
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
                art.genre = "'"+gen+"'";
            }
            else
                art.prGenre = 0.56;					// the opposite probability
        }

        if(!art.name.equals("'NULL'"))		//search if artist is already in the artists vector
        {
            int index = artistVec.indexOf(art);
            if(index!=-1)
            {//  if artist already exist we'll check if we can update the new or old valeus of him
                musicalArtist old = artistVec.elementAt(index);
                boolean flag = false;

                // checking if we can update genre
                if(!art.genre.equals(old.genre))
                {
                    if(old.genre.equals("'NULL'") && !art.genre.equals("'NULL'"))
                    {
                        old.genre = art.genre;
                        old.prGenre = art.prGenre;
                    }
                    else if (art.genre.equals("'NULL'") && !old.genre.equals("'NULL'"))
                    {
                        art.genre = old.genre;
                        art.prGenre = old.prGenre;
                    }
                    else
                        flag = true;
                }
                else
                {
                    art.prGenre = old.prGenre = Math.max(old.prGenre, art.prGenre);
                }

                // checking if we can update nationality
                if(!art.nationality.equals(old.nationality))
                {
                    if(old.nationality.equals("'NULL'") && !art.nationality.equals("'NULL'"))
                    {
                        old.nationality = art.nationality;
                        old.prNationality = art.prNationality;
                    }
                    else if (art.nationality.equals("'NULL'") && !old.nationality.equals("'NULL'"))
                    {
                        art.nationality = old.nationality;
                        art.prNationality = old.prNationality;
                    }
                    else
                        flag = true;
                }
                else
                {
                    art.prNationality = old.prNationality = Math.max(old.prNationality, art.prNationality);
                }

                // checking if we can update type
                if(!art.type.equals(old.type))
                {
                    if(old.type.equals("'NULL'") && !art.type.equals("'NULL'"))
                    {
                        old.type = art.type;
                        old.prType = art.prType;
                    }
                    else if (art.type.equals("'NULL'") && !old.type.equals("'NULL'"))
                    {
                        art.type = old.type;
                        art.prType = old.prType;
                    }
                    else
                        flag = true;
                }
                else
                {
                    art.prType = old.prType = Math.max(old.prType, art.prType);
                }


                if(flag)
                    artistVec.add(art);
            }
            else
                artistVec.add(art);
        }
    }

    public void extractFullPerson(String value) throws IOException
    {
        /*
        Each group of variables being initialized is the parameters for the specific heuristic run directly after
        @param matcherVar - match specific pattern of name+data
        @param nameVar - match specific type of name pattern
        @param dataVar - match specific type of data (year/profession) to be extracted
        @param nameLocation, nameCleanup, dataLocation, dataCleanup - auxiliary parameters for the clean extraction of name/data
        @param prHeuristic - the probability (confidence score) of this specific heuristic, calculated manually on a test-set
        */

       Person temp = new Person();

        //Check if the value pattern is an Infobox
        if (value.contains("{{Infobox")||value.contains("{{infobox")){
            matcherVar = bornInInfobox ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "irth";
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.99;
            runPersonInfoboxFullHeuristics(temp,value,1,prHeuristic);

            matcherVar = diedInInfobox ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "eath";
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";   prHeuristic=0.99;
            runPersonInfoboxFullHeuristics(temp,value,2,prHeuristic);

            matcherVar = bornInInfobox ; dataVar = infoTypePattern ; nameLocation = "" ; dataLocation = "Infobox";
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^a-z A-Z]"; prHeuristic=0.99;
            runPersonInfoboxFullHeuristics(temp,value,3,prHeuristic);
        }
        else  //look for the data in free text
        {
            matcherVar = bornInYearA1 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born" ;
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";prHeuristic=0.99;
            runPersonFullHeuristics(temp,value,1,prHeuristic);

            matcherVar = bornInYearA2 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = ""    ;
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.97;
            runPersonFullHeuristics(temp,value,1,prHeuristic);

            matcherVar = diedInYearA1 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "died";
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.99;
            runPersonFullHeuristics(temp,value,2,prHeuristic);

            matcherVar = diedInYearA2 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "-" ;
            nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.97;
            runPersonFullHeuristics(temp,value,2,prHeuristic);

            if(!temp.name.equals("'NULL'"))
            {
                matcherVar = bornInYearB1 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born"  ;
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.58;
                runPersonFullHeuristics(temp,value,1,prHeuristic);

                matcherVar = bornInYearB2 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born"  ;
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.62;
                runPersonFullHeuristics(temp,value,1,prHeuristic);

                matcherVar = bornInYearB3 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "b."    ;
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.65;
                runPersonFullHeuristics(temp,value,1,prHeuristic);

                matcherVar = bornInYearB4 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = ""      ;
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.45;
                runPersonFullHeuristics(temp,value,1,prHeuristic);

                matcherVar = diedInYearB1 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "died";
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.64;
                runPersonFullHeuristics(temp,value,2,prHeuristic);

                matcherVar = diedInYearB2 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "d." ;
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.65;
                runPersonFullHeuristics(temp,value,2,prHeuristic);

                matcherVar = diedInYearB3 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "-";
                nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";  prHeuristic=0.44;
                runPersonFullHeuristics(temp,value,2,prHeuristic);
            }
        }
        personVec.add(temp);
    }

    public void runPersonFullHeuristics(Person temp, String value,int FLAG,double prHeuristic)
    {
        mat.usePattern(matcherVar).reset(value);     //set the matchers to the appropriate patterns
        name.usePattern(nameVar);
        data.usePattern(dataVar);

        if (mat.find())
        {
            str= mat.group();
            if (name.reset(value).find()){
                pname = name.group().substring(name.group().indexOf(nameLocation)).replaceAll(nameCleanup,"").trim(); //extract Person name
                if(!pname.isEmpty()&&!pname.contains("iography")){
                    if(data.reset(str).find(str.indexOf(dataLocation))){
                        switch(FLAG){  //FLAG=1 - Birth Year, FLAG=2 - Death Year
                            case(BIRTH_YEAR):{
                                createPerson1(temp, pname,prHeuristic);
                                break;
                            }
                            case(DEATH_YEAR):{
                                createPerson2(temp,pname,prHeuristic);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void runPersonInfoboxFullHeuristics(Person temp, String value, int FLAG,double prHeuristic)
    {    //This is the same principal as the runPersonFullHeuristics method, but specific for the Infobox pattern
        //with the necessary data parsing, here we do not need a name pattern since we take the first line of each value
        mat.usePattern(matcherVar).reset(value);
        data.usePattern(dataVar);
        if (mat.find())
        {
            str= mat.group();
            int k = value.indexOf('\n');
            int j = value.indexOf(dataLocation);
            pname = value.substring(0,k).replaceAll(nameCleanup,"").trim();
            if(!pname.isEmpty()&&!pname.contains("iography")){   //filtering out biography values
                if(data.reset(value).find(j)){
                    switch(FLAG){
                        case(BIRTH_YEAR):{
                            createPerson1(temp,pname,prHeuristic);
                            break;
                        }
                        case(DEATH_YEAR):{
                            createPerson2(temp,pname,prHeuristic);
                            break;
                        }
                        case(PROFESSION):{
                            pdata = data.group().substring(data.group().indexOf("Infobox")+7).replaceAll(dataCleanup,"").toLowerCase().trim();
                            if(!pdata.equalsIgnoreCase("person")&&!pdata.contains("iography")){
                                createPerson3(temp,pname,pdata,prHeuristic);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void extractPosPerson(String value, String data) throws IOException
    {
        /*
        Each group of variables being initialized is the parameters for the specific heuristic run directly after
        @param matcherVar - match specific pattern of name+data
        @param nameVar - match specific type of name pattern
        @param dataVar - match specific type of data (year/profession) to be extracted
        @param nameLocation, nameCleanup, dataLocation, dataCleanup - auxiliary parameters for the clean extraction of name/data
        @param prHeuristic - the probability (confidence score) of this specific heuristic, calculated manually on a test-set
        */

        Person temp = new Person();

        matcherVar = personPOS ; dataVar = profPOS ; dataLocation = "/DT" ; dataCleanup = "/NN"; prHeuristic=0.79;
        runPosHeuristics(temp,value,data,3,prHeuristic);

        matcherVar = bornInPOS ; dataVar = yearPOS ; dataLocation = "/NNP" ; dataCleanup = "[^0-9]"; prHeuristic=0.96;
        runPosHeuristics(temp,value,data,1,prHeuristic);

        matcherVar = diedInPOS ; dataVar = yearPOS ; dataLocation = "-/:" ; dataCleanup = "[^0-9]"; prHeuristic=0.99;
        runPosHeuristics(temp,value,data,2,prHeuristic);

        boolean flag = true;
        if ((i = personVec.indexOf(temp))>=0){
            x = personVec.get(i);
            if (!x.bornIn.equals(temp.bornIn)){                                     //compare to see if bornIn years are different
                if(!x.bornIn.equals("NULL") && temp.bornIn.equals("NULL")){         //if one is NULL copy value from the other
                    temp.bornIn = x.bornIn;
                    temp.prBornIn = x.prBornIn;
                }
                else if (x.bornIn.equals("NULL") && !temp.bornIn.equals("NULL"))    //if one is NULL copy value from the other
                {
                    x.bornIn = temp.bornIn;
                    x.prBornIn = temp.prBornIn;
                }
                else                     //years are different, set flag to indicate a new Person object should be added
                {
                    flag = false;
                }
            }
            else
            {
                if(!x.bornIn.equals("NULL")){       //if both are not NULL and identical - take the maximal probability
                    x.prBornIn = temp.prBornIn = Math.max(x.prBornIn,temp.prBornIn);
                }
            }
            if (!x.diedIn.equals(temp.diedIn)){                                     //compare to see if bornIn years are different
                if(!x.diedIn.equals("NULL") && temp.diedIn.equals("NULL")){         //if one is NULL copy value from the other
                    temp.diedIn = x.diedIn;
                    temp.prDiedIn = x.prDiedIn;
                }
                else if (x.diedIn.equals("NULL") && !temp.diedIn.equals("NULL"))    //if one is NULL copy value from the other
                {
                    x.diedIn = temp.diedIn;
                    x.prDiedIn = temp.prDiedIn;
                }
                else                     //years are different, set flag to indicate a new Person object should be added
                {
                    flag = false;
                }
            }
            else
            {
                if(!x.diedIn.equals("NULL")){           //if both are not NULL and identical - take the maximal probability
                    x.prDiedIn = temp.prDiedIn = Math.max(x.prDiedIn,temp.prDiedIn);
                }
            }
            if (!x.profession.equals(temp.profession)){                                      //compare to see if bornIn years are different
                if(!x.profession.equals("'NULL'") && temp.profession.equals("'NULL'")){         //if one is NULL copy value from the other
                    temp.profession = x.profession;
                    temp.prProf = x.prProf;
                }
                else if (x.profession.equals("'NULL'") && !temp.profession.equals("'NULL'"))    //if one is NULL copy value from the other
                {
                    x.profession = temp.profession;
                    x.prProf = temp.prProf;
                }
                else
                {
                    flag = false;
                }
            }
            else
            {
                if(!x.profession.equals("'NULL'")){   //if both are not NULL and identical - take the maximal probability
                    x.prProf = temp.prProf = Math.max(x.prProf,temp.prProf);
                }
            }
            if(flag){           //at least one of the fields has a different value
                personVec.add(temp);
            }
        }
        else   //the person does not exist yet
        {
            personVec.add(temp);
        }
    }

    public void runPosHeuristics(Person temp, String name, String lineData,int FLAG, double prHeuristic)
    {
        mat.usePattern(matcherVar).reset(lineData);
        data.usePattern(dataVar);
        if (mat.find())         //if a match to the general pattern of name+data is found, extract them
        {
            str= mat.group();
            pname = name.trim();
            if(!pname.isEmpty()){ //if a name was extracted
                if(data.reset(str).find(str.indexOf(dataLocation))){
                    switch(FLAG){
                        case(BIRTH_YEAR):{
                            createPerson1(temp,pname,prHeuristic);
                            break;
                        }
                        case(DEATH_YEAR):{
                            createPerson2(temp,pname,prHeuristic);
                            break;
                        }
                        case(PROFESSION):{
                            String[] tempdata = data.group().split("and/CC");   //account for multiple professions for the same person
                            for (String s:tempdata){
                                pdata=s.replaceAll(dataCleanup,"").trim().toLowerCase();
                                createPerson3(temp,pname,pdata,prHeuristic);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void extractFullCountry(String value) throws IOException
    {
        /*
        Each group of variables being initialized is the parameters for the specific heuristic run directly after
        @param matcherVar - match specific pattern of name+data
        @param nameVar - match specific type of name pattern
        @param dataVar - match specific type of data (year/profession) to be extracted
        @param nameLocation, nameCleanup, dataLocation, dataCleanup - auxiliary parameters for the clean extraction of name/data
        @param prHeuristic - the probability (confidence score) of this specific heuristic, calculated manually on a test-set
        */
        dataVar = populPattern ; nameLocation = "" ; dataLocation = "population";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0.99;
        runCountryFullHeuristics(value,prHeuristic);
    }

    public void runCountryFullHeuristics(String value, double prHeuristic)
    {   //Similar to the Person heuristics, but here we look for "Infobox Country" directly
        data.usePattern(dataVar);
        if(value.contains("{{Infobox Country"))
        {
            int k = value.indexOf('\n');
            int j = value.indexOf(dataLocation);
            pname =value.substring(0,k).trim().replaceAll("'","");
            if(!pname.isEmpty()){
                if(data.reset(value).find(j)){
                    pdata = data.group().replaceAll(dataCleanup,"").trim();
                    if(!pdata.isEmpty()){
                        c = new Country("'"+pname+"'","'"+pdata+"'",prHeuristic);
                        if ((i = countryVec.indexOf(c))>=0){
                            if ((t = countryVec.get(i)).population.equalsIgnoreCase(pdata)){
                                t.prPopulation = Math.max(t.prPopulation,prHeuristic);
                            }
                            else{
                                countryVec.add(c);
                            }
                        }
                        else{
                            countryVec.add(c);
                        }
                    }
                }
            }
        }
    }

    //helper functions to be used in the heuristics
    public void createPerson1(Person temp, String pname, double prHeuristic){
        pdata = data.group().replaceAll(dataCleanup,"");
        if (temp.bornIn.equals("NULL")){
            temp.bornIn = pdata;
            temp.prBornIn = prHeuristic;
            return;
        }
        else if (temp.bornIn.equals(pdata)){
            temp.prBornIn = Math.max(temp.prBornIn,prHeuristic);
            return;
        }
        else
        {
            p = new Person("'"+pname+"'","'"+pdata+"'",prHeuristic,temp.diedIn,temp.prDiedIn,temp.profession,temp.prProf);
            personVec.add(p);
        }
    }

    public void createPerson2(Person temp, String pname, double prHeuristic){
        pdata = data.group().replaceAll(dataCleanup,"");
        if (temp.diedIn.equals("NULL")){
            temp.diedIn = pdata;
            temp.prDiedIn = prHeuristic;
            return;
        }
        else if (temp.diedIn.equals(pdata)){
            temp.prDiedIn = Math.max(temp.prDiedIn,prHeuristic);
            return;
        }
        else
        {
            p = new Person("'"+pname+"'",temp.bornIn,temp.prBornIn,"'"+pdata+"'",prHeuristic,temp.profession,temp.prProf);
            personVec.add(p);
        }
    }

    public void createPerson3(Person temp, String pname, String pdata, double prHeuristic){
        if (temp.profession.equals("'NULL'")){
            temp.profession = pdata;
            temp.prProf = prHeuristic;
            return;
        }
        else if (temp.profession.equals(pdata)){
            temp.prProf = Math.max(temp.prProf,prHeuristic);
            return;
        }
        else
        {
            p = new Person("'"+pname+"'",temp.bornIn,temp.prBornIn,temp.diedIn,temp.prDiedIn,"'"+pdata+"'",prHeuristic);
            personVec.add(p);
        }
    }
}
