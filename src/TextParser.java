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

    String YEAR_PATTERN =  "[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}[^0-9a-zA-Z]{1,2}";
    String INFOBOX_PATTERN = "Infobox[^\\.]+";
    String NAME_PATTERN_A = "'''[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*'''";
    String NAME_PATTERN_B = "\\[\\[[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\]\\]";
    String POPULATION_PATTERN = "(populationtotal[^\\n]+?[0-9,. ]+[^0-9,. ])|(population_estimate[^\\n_]+?[0-9,. ]+[^0-9,. ])";
    String INFO_TYPE_PATTERN = "Infobox(_| )[A-Za-z ]+[^A-Za-z ]";
    String COUNTRY_PATTERN =  "Infobox(_| )Country";
    final int BIRTH_YEAR=1,DEATH_YEAR=2,PROFESSION=3;
    /*
    Patterns for the semi-structured Full.txt
    */
    //Patterns for people's names
    Pattern nameA = Pattern.compile(NAME_PATTERN_A);
    Pattern nameB = Pattern.compile(NAME_PATTERN_B);
    Pattern nameBeforeInfobox = Pattern.compile("^[a-zA-Z\\s]+?[^A-Z a-z]");
    //Patterns for birth year, based on the name patterns
    Pattern bornInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    Pattern bornInYearA2 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+\\(born"+YEAR_PATTERN);
    Pattern bornInYearA3 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern bornInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    Pattern bornInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(born"+YEAR_PATTERN);
    Pattern bornInYearB3 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(b\\. "+YEAR_PATTERN);
    Pattern bornInYearB4 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern bornInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+(B|b)irth(_| )date[^\\n]+"+YEAR_PATTERN);
    //Patterns for death year, based on the name patterns
    Pattern diedInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    Pattern diedInYearA2 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern diedInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    Pattern diedInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(d\\. "+YEAR_PATTERN);
    Pattern diedInYearB3 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    Pattern diedInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+death(_| )date[^\\n]+"+YEAR_PATTERN);

    Pattern nameInfobox = Pattern.compile("(N|n)ame[^\\n]*?=[^\\n]*?[a-zA-Z\\s]+?[^A-Z a-z]");
    //Patterns for people Infoboxes, year
    Pattern infoTypePattern = Pattern.compile(INFO_TYPE_PATTERN);
    Pattern yearPattern = Pattern.compile(YEAR_PATTERN);
    //Patterns for Country Infoboxes
    Pattern populPattern = Pattern.compile(POPULATION_PATTERN);
    Pattern countryPattern = Pattern.compile(".+"+COUNTRY_PATTERN+".+?"+POPULATION_PATTERN,Pattern.DOTALL);
    //Patterns for the tagged POS.txt
    Pattern personPOS = Pattern.compile("([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+?(is/VBZ|was/VBD)\\s(a/DT|an/DT)([^\\.]+?(or|er|ian|ist)/NN)+?(\\sand/CC([^\\.\\n]+?(or|er|ian|ist)/NN)+)*");
    Pattern bornInPOS = Pattern.compile("^([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}/CD");
    Pattern profPOS = Pattern.compile("([a-z]+(or|er|ian|ist)/NN)+?(\\sand/CC\\s([a-z ]+(or|er|ian|ist)/NN)+)*");
    Pattern yearPOS = Pattern.compile("[1-9][0-9]{2,3}/CD");
    //Matchers
    Matcher mat = yearPattern.matcher("");
    Matcher data = yearPattern.matcher("");
    Matcher name = nameA.matcher("");
    //variables for heuristics
    int i;
    String pname="",pdata="",nameLocation="",dataLocation="",nameCleanup="",dataCleanup="",str;
    Pattern nameVar,matcherVar,dataVar;
    double prHeuristic;
    Person p; Country c;

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
    	
    	//parsePos();                       ----------------------------------------------------------------------------------------
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
			
		//	extractFullArtist(value);       ------------------------------------------------------
		//	extractFullPerson(value);      ----------------------------------------------------------------
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
     		 extractPosPerson(value,data);
     		 extractPosCountry(value,data);
     	  }
    }
    
    public void extractFullArtist(String value) throws IOException
    {
    	//int counter = 1;
		if(value.contains("{{Infobox musical artist"))
		{
			musicalArtist artist = new musicalArtist();		// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
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
		art.name = value;   			// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
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
		art.name = value;					// after finding a an artist in this heuristic, his name get 0.99 probability (according to manual sampling)
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


    
    public void extractPosCountry(String value, String data) throws IOException
    {
    	
    }

    public void extractFullPerson(String value) throws IOException
    {

        matcherVar = bornInYearA1 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born" ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";prHeuristic=0;   //TODO fix probability    //1273 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearA2 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born" ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability    //10 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearA3 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = ""    ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability     //309 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearB1 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born"  ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability      //166 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearB2 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "born"  ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability     //48 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearB3 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "b."    ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability    //286 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInYearB4 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = ""      ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability   //160 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = bornInInfobox ; nameVar = nameInfobox ; dataVar = yearPattern ; nameLocation = "=" ; dataLocation = "date";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability   //86 results
        runPersonFullHeuristics(value,1,prHeuristic);

        matcherVar = diedInYearA1 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "died";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability    //150 results
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = diedInYearA2 ; nameVar = nameA ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "-" ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability   //309 results
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = diedInYearB1 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "died";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability   //195 results, some mistakes
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = diedInYearB2 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "d." ;
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability  //305 results
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = diedInYearB3 ; nameVar = nameB ; dataVar = yearPattern ; nameLocation = "" ; dataLocation = "-";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";  prHeuristic=0;   //TODO fix probability  //160 results  , some mistakes
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = diedInInfobox ; nameVar = nameInfobox ; dataVar = yearPattern ; nameLocation = "=" ; dataLocation = "date";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]";   prHeuristic=0;   //TODO fix probability //31 results
        runPersonFullHeuristics(value,2,prHeuristic);

        matcherVar = bornInInfobox ; nameVar = nameInfobox ; dataVar = infoTypePattern ; nameLocation = "=" ; dataLocation = "Infobox";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^a-z A-Z]"; prHeuristic=0;   //TODO fix probability 86 results
        runPersonFullHeuristics(value,3,prHeuristic);
    }

    public void runPersonFullHeuristics(String value,int FLAG,double prHeuristic) //TODO fix probability  Max
    {
        mat.usePattern(matcherVar).reset(value);
        name.usePattern(nameVar);
        data.usePattern(dataVar);

        if (mat.find())
        {
            str= mat.group();
            if (name.reset(str).find()){
                pname = name.group().substring(name.group().indexOf(nameLocation)).replaceAll(nameCleanup,"").trim();
                if(!pname.isEmpty()){
                    if(data.reset(str).find(str.indexOf(dataLocation))){
                        switch(FLAG){
                            case(BIRTH_YEAR):{
                                pdata = data.group().replaceAll(dataCleanup,"");
                                p = new Person(pname,pdata,prHeuristic,"",0,"",0);
                                if ((i = personVec.indexOf(p))>=0){
                                    if (personVec.get(i).bornIn.equalsIgnoreCase(pdata)){
                                        //TODO get the max probability
                                    }
                                    else{
                                        personVec.add(p);
                                    }
                                }
                                else{
                                    personVec.add(p);
                                }
                                break;
                            }
                            case(DEATH_YEAR):{
                                pdata = data.group().replaceAll(dataCleanup,"");
                                p = new Person(pname,"",0,pdata,prHeuristic,"",0);
                                if ((i = personVec.indexOf(p))>=0){
                                    if (personVec.get(i).diedIn.equalsIgnoreCase(pdata)){
                                        //TODO get the max probability
                                    }
                                    else{
                                        personVec.add(p);
                                    }
                                }
                                else{
                                    personVec.add(p);
                                }
                                break;
                            }
                            case(PROFESSION):{
                                pdata = data.group().substring(data.group().indexOf("Infobox")+7).replaceAll(dataCleanup,"").toLowerCase().trim();
                                if(!pdata.equalsIgnoreCase("person")){
                                    if(pdata.equals("football biography")) pdata="football player";
                                    p = new Person(pname,"",0,"",0,pdata,prHeuristic);
                                    if ((i = personVec.indexOf(p))>=0){
                                        if (personVec.get(i).profession.equalsIgnoreCase(pdata)){
                                            //TODO get the max probability
                                        }
                                        else{
                                            personVec.add(p);
                                        }
                                    }
                                    else{
                                        personVec.add(p);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void extractPosPerson(String value, String data) throws IOException
    {
        matcherVar = personPOS ; dataVar = profPOS ; dataLocation = "/DT" ; prHeuristic=0;   //TODO fix probability
        runProfPosHeuristics(value,data,prHeuristic);

        matcherVar = bornInPOS ; dataVar = yearPOS ; dataLocation = "/NNP" ; dataCleanup = "[^0-9]"; ; prHeuristic=0;  //TODO fix probability
        runBornInPosHeuristics(value,data,prHeuristic);
    }

    public void runProfPosHeuristics(String name, String lineData, double prHeuristic)    //TODO fix probability Max
    {
        mat.usePattern(matcherVar).reset(lineData);
        data.usePattern(dataVar);
        if (mat.find())
        {
            str= mat.group();
            pname = name.trim();
            if(!pname.isEmpty()){
                if(data.reset(str).find(str.indexOf(dataLocation))){
                    String[] tempdata = data.group().split("and/CC");
                    for (String s:tempdata){
                        pdata=s.replaceAll("/NN","").trim();
                        p = new Person(pname,"",0,"",0,pdata,prHeuristic);
                        if ((i = personVec.indexOf(p))>=0){
                            if (personVec.get(i).profession.equalsIgnoreCase(pdata)){
                                //TODO get the max probability
                            }
                            else{
                                personVec.add(p);
                            }
                        }
                        else{
                            personVec.add(p);
                        }
                    }
                }
            }
        }
    }

    public void runBornInPosHeuristics(String name, String lineData, double prHeuristic)   //TODO fix probability  Max
    {
        mat.usePattern(matcherVar).reset(lineData);
        data.usePattern(dataVar);
        if (mat.find())
        {
            str= mat.group();
            pname = name.trim();
            if(!pname.isEmpty()){
                if(data.reset(str).find(str.indexOf(dataLocation))){
                    pdata=data.group().replaceAll(dataCleanup,"");
                    p = new Person(pname,pdata,prHeuristic,"",0,"",0);
                    if ((i = personVec.indexOf(p))>=0){
                        if (personVec.get(i).bornIn.equalsIgnoreCase(pdata)){
                            //TODO get the max probability
                        }
                        else{
                            personVec.add(p);
                        }
                    }
                    else{
                        personVec.add(p);
                    }
                }
            }
        }
    }

    public void extractFullCountry(String value) throws IOException
    {
        matcherVar = countryPattern ; nameVar = nameBeforeInfobox ; dataVar = populPattern ; nameLocation = "" ; dataLocation = "population";
        nameCleanup = "[^a-z A-Z]"; dataCleanup = "[^0-9]"; prHeuristic=0;   //TODO fix probability 86 results
        runCountryFullHeuristics(value,prHeuristic);
    }

    public void runCountryFullHeuristics(String value, double prHeuristic) //TODO fix probability  Max
    {
        mat.usePattern(matcherVar).reset(value);
        name.usePattern(nameVar);
        data.usePattern(dataVar);

        if (mat.find())
        {
            str= mat.group();
            if (name.reset(str).find()){
                pname = name.group().substring(name.group().indexOf(nameLocation)).replaceAll(nameCleanup,"").trim();
                if(!pname.isEmpty()){
                    if(data.reset(str).find(str.indexOf(dataLocation))){
                        pdata = data.group().replaceAll(dataCleanup,"").trim();
                        if(!pdata.isEmpty()){
                            c = new Country(pname,pdata,prHeuristic);
                            if ((i = countryVec.indexOf(c))>=0){
                                if (countryVec.get(i).population.equalsIgnoreCase(pdata)){
                                    //TODO get the max probability
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
                System.out.println(str);
                System.out.println(data.group());
                System.out.println(pname);
                System.out.println(pdata);

            }
        }
    }
}
