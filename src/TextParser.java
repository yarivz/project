import java.io.*;
import java.util.logging.Level;

public class TextParser {
    FileInputStream fstream;
    DataInputStream in;
    BufferedReader br;

    public void initParser(String fileName){
        try{
            fstream = new FileInputStream(fileName);
        } catch (FileNotFoundException e){
            System.out.println("Could not read file "+fileName);
            return;
        }
        in = new DataInputStream(fstream);
        br = new BufferedReader(new InputStreamReader(in));
    }

    public void parseText(){
        String line="", value="";
        //Read File Line By Line
        try{
            while ((line = br.readLine()) != null)
            {
                while(!line.equals('\n')&&!line.equals("\r\n")){
                    value.concat(line);
                    line = br.readLine();
                }
               runHeuristics(value);
            }
        } catch (IOException e){
            main.lgr.log(Level.WARNING, "Could not read text line");
        }
    }

    void runHeuristics(String str){
       for (heuristic h in


    }


}
