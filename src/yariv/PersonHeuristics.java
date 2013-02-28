import java.io.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonHeuristics {


        static String YEAR_PATTERN =  "[^0-9a-zA-Z]{1,2}[1-9][0-9]{2,3}[^0-9a-zA-Z]{1,2}";
        static String INFOBOX_PATTERN = "Infobox[_\\s]{1}((Person)|(actor)|((M|m)usical\\sartist))";
        static String NAME_PATTERN_A = "'''[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*'''";
        static String NAME_PATTERN_B = "\\[\\[[A-Z][a-z]+\\s[A-Z][a-z]+(\\s[a-zA-Z])*\\]\\]";
    /*
Patterns for the semi-structured Full.txt
 */
    static Pattern nameA = Pattern.compile(NAME_PATTERN_A);
    static Pattern nameB = Pattern.compile(NAME_PATTERN_B);

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




        /*
        bornIn PersonHeuristics
         */
    public void runHeuristics() throws IOException {
        FileInputStream fstream = new FileInputStream("full.txt");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line="", value="";
        Vector<Person> persons = new Vector<Person>();
        try
        {

        Matcher mat = yearPattern.matcher(value);
        Matcher year = yearPattern.matcher(value);
        Matcher name = nameA.matcher(value);
        while ((line = br.readLine()) != null)
        {
            while(!(line.equals(""))){
                value = value.concat(line+'\n');
                line = br.readLine();
            }

            mat.reset(value);
            year.reset(value);
            name.reset(value);
            int i;
            String pname="",pborn="",pdied="",wordVar="";
            Pattern nameVar,matcherVar;
            Person p;
            /*
            For bornInYearA1,A2 use nameA & "born"
            for bornInYearB1,B2 use nameB & "born"
            For bornInYearA3 use nameA & "b."
            For bornInYearB3 use nameB & "b."
            For bornInYearA4 use nameA & ""
            For bornInYearB4 use nameB & ""

            For diedInYearA1,A2 use nameA & "died"
            for diedInYearB1,B2 use nameB & "died"
            For diedInYearA3 use nameA & "d."
            For diedInYearB3 use nameB & "d."
            For diedInYearA3 use nameA & "-"
            For diedInYearB3 use nameB & "-"

             */
            matcherVar=bornInYearA3;
            nameVar=nameA;
            wordVar="b.";

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
            }

        }
            value = "";
        }
        catch (IOException e){
        }

        System.out.println(persons.toString());
        System.out.println(persons.size());
    }


}









