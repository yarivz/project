import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Heuristics {

    class PersonTable {
        String YEAR_PATTERN =  "[0-9]{3,}?";
        String INFOBOX_PATTERN = ".+?Infobox(_|\\\\s)(Person|actor|(M|m)usical artist)";
        //Pattern bornInCountry = Pattern.compile("(born[^\\.]+?\\[\\[[a-zA-Z]+\\]\\])");

        /*
        Patterns for the semi-structured Full.txt
         */
        Pattern name1 = Pattern.compile("\\'\\'\\'[a-zA-Z\\s]+\\'\\'\\'");
        Pattern bornInYear1 = Pattern.compile("born[^\\.]+?\\[\\["+YEAR_PATTERN+"\\]\\]");
        Pattern bornInYear2 = Pattern.compile("\\[\\["+YEAR_PATTERN+"\\]\\]\\s-");
        Pattern bornInYear3 = Pattern.compile("\\(born "+YEAR_PATTERN+"\\)");
        Pattern diedInYear1 = Pattern.compile("died.+?\\[\\["+YEAR_PATTERN+"\\]\\]");
        Pattern diedInYear2 = Pattern.compile("-\\s\\[\\[.+?\\]\\]\\s\\[\\["+YEAR_PATTERN+"\\]\\]");
        Pattern nameInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+?name\\s*=");
        Pattern bornInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+?birth(_|\\s)date[^\\.]+?("+YEAR_PATTERN+")");
        Pattern diedInInfobox = Pattern.compile(INFOBOX_PATTERN+"[^\\.]+?death(_|\\s)date[^\\.]+?("+YEAR_PATTERN+")");
        /*
        Patterns for the tagged POS.txt
         */
        Pattern professionPOS = Pattern.compile("([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+?(is/VBZ|was/VBD)\\s(a/DT|an/DT)([^\\.]+?(er|ian|ist)/NN)+?(\\sand/CC\\s([^\\.]+?(er|ian|ist)/NN)+)*");


        public Vector<Person> runHeuristics(String str, int FLAG){
        /* FLAG = 0 for Full.txt, FLAG = 1 for POS.txt */
            Vector<Person> persons = new Vector<Person>();
            if(FLAG == 0){
            Matcher name = name1.matcher(str);
            Matcher bornIn = bornInYear1.matcher(str);
            if(name.find()){
                if (bornIn.find(name.end()))
                {
                    bornIn.group();


                }
            }







        }

            return persons;

        }


}
}









