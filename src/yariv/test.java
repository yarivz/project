import java.io.*;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: yariv
 * Date: 2/26/13
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class test {
    public static Logger lgr = Logger.getLogger("wdm.Logger");
    static FileHandler fh;
    static String YEAR_PATTERN =  "[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}[^0-9a-zA-Z]{1,2}";
    static String INFOBOX_PATTERN = "Infobox[_\\s]{1}((Person)|(actor)|((M|m)usical\\sartist))";
    static String NAME_PATTERN_A = "'''[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*'''";
    static String NAME_PATTERN_B = "\\[\\[[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\]\\]";

    /*
    Patterns for the semi-structured Full.txt
     */
    static Pattern nameA = Pattern.compile(NAME_PATTERN_A);
    static Pattern nameB = Pattern.compile(NAME_PATTERN_B);

    //static Pattern bornInYear1 = Pattern.compile("born [^\\.\\n\\*]+?"+YEAR_PATTERN);
    //static Pattern bornInYear3 = Pattern.compile("\\((born|b.)"+YEAR_PATTERN);
    //static Pattern diedInYear1 = Pattern.compile("died[^\\.]+?"+YEAR_PATTERN);
   // static Pattern diedInYear3 = Pattern.compile("\\((died|d.)"+YEAR_PATTERN);

    static Pattern bornInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    static Pattern bornInYearA2 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+\\(born"+YEAR_PATTERN);
    static Pattern bornInYearA3 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+\\(b\\. "+YEAR_PATTERN);
    static Pattern bornInYearA4 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    static Pattern bornInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+born\\s[^\\.\\n\\*]+"+YEAR_PATTERN);
    static Pattern bornInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(born"+YEAR_PATTERN);
    static Pattern bornInYearB3 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(b\\. "+YEAR_PATTERN);
    static Pattern bornInYearB4 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);


    static Pattern diedInYearA1 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    static Pattern diedInYearA2 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+\\(died"+YEAR_PATTERN);
    static Pattern diedInYearA3 = Pattern.compile(NAME_PATTERN_A+"[^\\.\\n]+\\(d\\. "+YEAR_PATTERN);
    static Pattern diedInYearA4 = Pattern.compile(NAME_PATTERN_A+".+"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);
    static Pattern diedInYearB1 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+died[^\\.]+?"+YEAR_PATTERN);
    static Pattern diedInYearB2 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(died"+YEAR_PATTERN);
    static Pattern diedInYearB3 = Pattern.compile(NAME_PATTERN_B+"[^\\.\\n]+\\(d\\. "+YEAR_PATTERN);
    static Pattern diedInYearB4 = Pattern.compile(NAME_PATTERN_B+".+\\(.*"+YEAR_PATTERN+"\\s*-[\\[\\]\\w,\\s]+"+YEAR_PATTERN);

    static Pattern nameInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+?name(\\s)*=[a-zA-Z\\s]+?[\\|]");
    static  Pattern bornInInfobox = Pattern.compile("birth(_|\\s)date[^\\|]+?"+YEAR_PATTERN);
    static Pattern diedInInfobox = Pattern.compile("death(_|\\s)date[^\\|]+?"+YEAR_PATTERN);
    static Pattern yearPattern = Pattern.compile(YEAR_PATTERN);
    /*
    Patterns for the tagged POS.txt
     */
    Pattern professionPOS = Pattern.compile("([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+?(is/VBZ|was/VBD)\\s(a/DT|an/DT)([^\\.]+?(er|ian|ist)/NN)+?(\\sand/CC\\s([^\\.]+?(er|ian|ist)/NN)+)*");


    public static void main(String[] args){
        try{
            fh =  new FileHandler("wdm.log");
            lgr.addHandler(fh);
        } catch (IOException ex){
            System.out.println("could not create log file wdm.log");
        }

        FileInputStream fstream;
        DataInputStream in;
        BufferedReader br;
        try{
            fstream = new FileInputStream("full.txt");
        } catch (FileNotFoundException e){
            System.out.println("Could not read file");
            return;
        }
        in = new DataInputStream(fstream);
        br = new BufferedReader(new InputStreamReader(in));
        String line="", value="";
        //Read File Line By Line and concat values (paragraphs) between empty lines
        Vector<Person> persons = new Vector<Person>();

        try
        {
            Matcher mat = yearPattern.matcher(value);
            Matcher year = yearPattern.matcher(value);
            Matcher name = nameA.matcher(value);

           /* Matcher nameF1 = name1.matcher(value);
            Matcher nameF2 = name2.matcher(value);
            Matcher bornIn11 = bornInYear1.matcher(value);
            Matcher bornIn12 = livedInYear1.matcher(value);
            Matcher bornIn13 = bornInYear3.matcher(value);
            Matcher bornIn21 = bornInYear1.matcher(value);
            Matcher bornIn22 = livedInYear2.matcher(value);
            Matcher bornIn23 = bornInYear3.matcher(value);
            Matcher diedIn1 = diedInYear1.matcher(value);
            Matcher diedIn2 = livedInYear2.matcher(value);
            Matcher diedIn3 = diedInYear3.matcher(value);
            Matcher nameInfo = nameInfobox.matcher(value);
            Matcher bornInInfo = bornInInfobox.matcher(value);
            Matcher diedInInfo = diedInInfobox.matcher(value); */

            while ((line = br.readLine()) != null)
            {
                while(!(line.equals(""))){
                    value = value.concat(line+'\n');
                    line = br.readLine();
                }

                mat.reset(value);
                year.reset(value);
                name.reset(value);

               /* nameF1.reset(value);
                nameF2.reset(value);
                bornIn1.reset(value);
                bornIn2.reset(value);
                bornIn3.reset(value);
                diedIn1.reset(value);
                diedIn2.reset(value);
                diedIn3.reset(value);
                nameInfo.reset(value);
                bornInInfo.reset(value);
                diedInInfo.reset(value); */
                int i;
                String pname="",pborn="",pdied="",wordVar="";
                Pattern nameVar,matcherVar;
                Person p;
                matcherVar=bornInYearB4;
                nameVar=nameB;
                wordVar="";
                mat.usePattern(matcherVar);
                name.usePattern(nameVar);
                if (mat.find())
                {
                    if (name.reset(mat.group()).find()) pname = name.group().replaceAll("[^a-z A-Z]","");
                    pborn = year.reset(mat.group()).find(mat.group().indexOf(wordVar)) ? year.group().replaceAll("[^0-9]","") : "";
                    p = new Person(pname,pborn,"","");
                    if ((i = persons.indexOf(p))>=0){
                        persons.get(i).addBornIn(pborn);
                    } else{
                        persons.add(p);
                    }
                    System.out.println(name.group());
                    System.out.println(mat.group());
                    System.out.println(pname);
                    System.out.println(pborn+'\n');
                }
               /* if (bornIn3.find(nameF2
                /*if(nameF1.find())
                {
                    String n = nameF1.group();
                    String pname = n.substring(3,n.length()-3);

                    if (bornIn1.find(nameF1.end()))
                    {
                        String s = bornIn1.group();
                        year.reset(s).find();
                        String pborn = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,pborn,"","");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getBornIn()).equals(pborn)){
                                persons.add(p);
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                    if (bornIn2.find(nameF1.end()))
                    {
                        String s = bornIn2.group();
                        year.reset(s.substring(0,s.indexOf('-'))).find();
                        String pborn = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,pborn,"","");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getBornIn()).equals(pborn)){
                                persons.add(p);
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                    if (bornIn3.find(nameF1.end()))
                    {
                        String s = bornIn3.group();
                        year.reset(s).find();
                        String pborn = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,pborn,"","");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getBornIn()).equals(pborn)){
                                persons.add(p);
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                    if (diedIn1.find(nameF1.end()))
                    {
                        String s = diedIn1.group();
                        year.reset(s).find();
                        String pdied = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,"",pdied,"");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getDiedIn()).equals(pdied)){
                                persons.add(p);   //TODO add place for probability
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                    if (diedIn2.find(nameF1.end()))
                    {
                        String s = diedIn2.group();
                        System.out.println(s);
                        year.reset(s.substring(s.indexOf('-'))).find();
                        String pdied = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,"",pdied,"");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getDiedIn()).equals(pdied)){
                                persons.add(p);   //TODO add place for probability
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                    if (diedIn3.find(nameF1.end()))
                    {
                        String s = diedIn3.group();
                        System.out.println(s);
                        year.reset(s).find();
                        String pdied = year.group().replaceAll("[^0-9]","");
                        Person p = new Person(pname,"",pdied,"");
                        if ((i = persons.indexOf(p))>=0){
                            if(!(persons.get(i).getDiedIn()).equals(pdied)){
                                persons.add(p);   //TODO add place for probability
                            } else {
                                //TODO add choosing max probability
                            }
                        } else{
                            persons.add(p);
                        }

                    }
                } */
               /* if (bornIn21.find())
                {

                    String s = bornIn1.group();
                    String pname = s.substring(s.indexOf("[["),s.indexOf("]]"));
                    System.out.println(pname);
                    year.reset(s).find();
                    String pborn = year.group().replaceAll("[^0-9]","");
                    System.out.println(s+"***********"+pname+pborn);
                    Person p = new Person(pname,pborn,"","");
                    if ((i = persons.indexOf(p))>=0){
                        if(!(persons.get(i).getBornIn()).equals(pborn)){
                            persons.add(p);
                        } else {
                            //TODO add choosing max probability
                        }
                    } else{
                        persons.add(p);
                    }

                }
                mat.usePattern(bornInYear2);
                if (mat.find())
                {
                    String s = mat.group();
                    String pname = s.substring(s.indexOf("[["),s.indexOf("]]"));
                    year.reset(s).find();
                    String pborn = year.group().replaceAll("[^0-9]","");
                    Person p = new Person(pname,pborn,"","");
                    if ((i = persons.indexOf(p))>=0){
                        persons.get(i).addBornIn(pborn);
                    } else{
                        persons.add(p);
                    }

                }
                if (bornIn3.find(nameF2.end()))
                {
                    String s = bornIn3.group();
                    String pname = n.substring(3,n.length()-3);
                    //String pborn = s.substring(s.lastIndexOf(' ')+1,s.lastIndexOf(')'));
                    year.reset(s).find();
                    String pborn = year.group().substring(1,year.group().length()-1);
                    Person p = new Person(pname,pborn,"","");
                    if ((i = persons.indexOf(p))>=0){
                        persons.get(i).addBornIn(pborn);
                    } else{
                        persons.add(p);
                    }

                }
                if (diedIn1.find(nameF2.end()))
                {
                    String s = diedIn1.group();
                    String pname = n.substring(3,n.length()-3);
                    //String pdied = s.substring(s.lastIndexOf("[[")+2,s.lastIndexOf("]]"));
                    year.reset(s).find();
                    String pdied = year.group().substring(1,year.group().length()-1);
                    Person p = new Person(pname,"",pdied,"");
                    if ((i = persons.indexOf(p))>=0){
                        persons.get(i).addDiedIn(pdied);
                    } else{
                        persons.add(p);
                    }

                }
                if (diedIn2.find(nameF2.end()))
                {
                    String s = diedIn2.group();
                    String pname = n.substring(3,n.length()-3);
                    //String pdied = s.substring(s.lastIndexOf("[[")+2,s.lastIndexOf("]]"));
                    year.reset(s).find();
                    String pdied = year.group().substring(1,year.group().length()-1);
                    Person p = new Person(pname,"",pdied,"");
                    if ((i = persons.indexOf(p))>=0){
                        persons.get(i).addDiedIn(pdied);
                    } else{
                        persons.add(p);
                    }

                } */

                /*else if(nameInfo.find())
                {
                    String n = nameInfo.group();
                    String pname = n.substring(n.indexOf('=')+1,n.lastIndexOf('|')).trim();

                    if (bornInInfo.find(nameInfo.end()))
                    {
                        String s = bornInInfo.group();
                        year.reset(s).find();
                        String pborn = year.group().substring(1,year.group().length()-1);
                        Person p = new Person(pname,pborn,"","");
                        if ((i = persons.indexOf(p))>=0){
                            persons.get(i).addBornIn(pborn);
                        } else{
                            persons.add(p);
                        }

                    }
                    if (diedInInfo.find(nameInfo.end()))
                    {
                        String s = diedInInfo.group();
                        year.reset(s).find();
                        String pdied = year.group().substring(1,year.group().length()-1);
                        Person p = new Person(pname,"",pdied,"");
                        if ((i = persons.indexOf(p))>=0){
                            persons.get(i).addDiedIn(pdied);
                        } else{
                            persons.add(p);
                        }

                    }

                } */
                value = "";
            } //while
        } //try
        catch (IOException e){
            lgr.log(Level.WARNING, "Could not read text line");
        }

        System.out.println(persons.toString());
        System.out.println(persons.size());
    }



}

