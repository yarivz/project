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
    static String YEAR_PATTERN =  "[^0-9a-zA-Z]{1,2}[0-9]{3,4}[^0-9a-zA-Z]{1,2}";
    static String INFOBOX_PATTERN = "Infobox[_\\\\s]{1}((Person)|(actor)|((M|m)usical\\\\sartist))";

    /*
    Patterns for the semi-structured Full.txt
     */
    static Pattern name1 = Pattern.compile("\\'\\'\\'[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\'\\'\\'");
    static Pattern name2 = Pattern.compile("\\*\\[\\[[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\]\\]");
    static Pattern bornInYear1 = Pattern.compile("born [^\\.]+?"+YEAR_PATTERN);
    static Pattern bornInYear2 = Pattern.compile(YEAR_PATTERN+"\\s*-");
    static Pattern bornInYear3 = Pattern.compile("\\(born"+YEAR_PATTERN);
    static Pattern diedInYear1 = Pattern.compile("died[^\\.]+?"+YEAR_PATTERN);
    static Pattern diedInYear2 = Pattern.compile("-\\s\\[\\[[^\\.]+?\\]\\]\\s\\[\\["+YEAR_PATTERN+"\\]\\]");
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
            Matcher nameF1 = name1.matcher(value);
            Matcher nameF2 = name2.matcher(value);
            Matcher bornIn1 = bornInYear1.matcher(value);
            Matcher bornIn2 = bornInYear2.matcher(value);
            Matcher bornIn3 = bornInYear3.matcher(value);
            Matcher diedIn1 = diedInYear1.matcher(value);
            Matcher diedIn2 = diedInYear2.matcher(value);
            Matcher nameInfo = nameInfobox.matcher(value);
            Matcher bornInInfo = bornInInfobox.matcher(value);
            Matcher diedInInfo = diedInInfobox.matcher(value);
            Matcher year = yearPattern.matcher(value);

            while ((line = br.readLine()) != null)
            {
                while(!(line.equals(""))){
                    value = value.concat('\n'+line);
                    line = br.readLine();
                }
                nameF1.reset(value);
                nameF2.reset(value);
                bornIn1.reset(value);
                bornIn2.reset(value);
                bornIn3.reset(value);
                diedIn1.reset(value);
                diedIn2.reset(value);
                nameInfo.reset(value);
                bornInInfo.reset(value);
                diedInInfo.reset(value);
                year.reset(value);
                int i;

                if(nameF1.find())
                {
                    String n = nameF1.group();
                    if (bornIn1.find(nameF1.end()))
                    {
                        String s = bornIn1.group();
                        String pname = n.substring(3,n.length()-3);
                        year.reset(s).find();
                        String pborn = year.group().substring(1,year.group().length()-1);
                        Person p = new Person(pname,pborn,"","");
                        persons.add(p);

                    }
                    if (bornIn2.find(nameF1.end()))
                    {
                        String s = bornIn2.group();
                        String pname = n.substring(3,n.length()-3);
                        //String pborn = s.substring(s.lastIndexOf("[[")+2,s.lastIndexOf("]]"));
                        year.reset(s).find();
                        String pborn = year.group().substring(1,year.group().length()-1);
                        Person p = new Person(pname,pborn,"","");
                        if ((i = persons.indexOf(p))>=0){
                            persons.get(i).addBornIn(pborn);
                        } else{
                            persons.add(p);
                        }

                    }
                    if (bornIn3.find(nameF1.end()))
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
                    if (diedIn1.find(nameF1.end()))
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
                    if (diedIn2.find(nameF1.end()))
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

                    }
                }
                else if(nameF2.find())
                {
                    String n = nameF2.group();
                    if (bornIn1.find(nameF2.end()))
                    {
                        String s = bornIn1.group();
                        String pname = n.substring(3,n.length()-3);
                        System.out.println(s);
                        System.out.println(s.lastIndexOf("]"));
                        //String pborn = s.substring(s.lastIndexOf("[")+1,s.lastIndexOf("]"));
                        year.reset(s).find();
                        String pborn = year.group().substring(1,year.group().length()-1);
                        Person p = new Person(pname,pborn,"","");
                        persons.add(p);

                    }
                    if (bornIn2.find(nameF2.end()))
                    {
                        String s = bornIn2.group();
                        String pname = n.substring(3,n.length()-3);
                        //String pborn = s.substring(s.lastIndexOf("[")+1,s.lastIndexOf("]"));
                        year.reset(s).find();
                        String pborn = year.group().substring(1,year.group().length()-1);
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

                    }
                }
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
            }
        }
        catch (IOException e){
            lgr.log(Level.WARNING, "Could not read text line");
        }

        System.out.println(persons.toString());
        System.out.println(persons.size());
    }







}

