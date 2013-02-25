import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Heuristics {

    class bornIn {
        public void runHeuristics(String str){
        /* if the line includes a name and the word "born" look for the year */
            Pattern nameFULL = Pattern.compile("(\'\'\'[a-zA-Z\\s]+\'\'\')");
            Pattern bornInYearFULL = Pattern.compile("(born[^\\.]+?\\[\\[[0-9]+\\]\\])|(\\[\\[[0-9]+\\]\\]\\s-)|(\\(born [0-9]+\\))");
            Pattern diedInYearFULL = Pattern.compile("(died.+?\\[\\[[0-9]+\\]\\])|(-\\s\\[\\[.+?\\]\\]\\s\\[\\[[0-9]+\\]\\])");
            Pattern professionPOS = Pattern.compile("([A-Z][a-z]+/NNP\\s){2,}?[^\\.]+?(is/VBZ|was/VBD)\\s(a/DT|an/DT)([^\\.]+?(er|ian|ist)/NN)+?(\\sand/CC\\s([^\\.]+?(er|ian|ist)/NN)+)*");

            //Pattern bornInCountry = Pattern.compile("(born[^\\.]+?\\[\\[[a-zA-Z]+\\]\\])");
            Matcher m = name.matcher(str);
            if(m.find() && str.contains("born")){

            }









        }


    }
    class bornAt{
    //TODO add 3 heuristics


    }
    class diedIn{
    //TODO add 3 heuristics


    }
    class prof{
    //TODO add 3 heuristics



    }

}










