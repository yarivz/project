import java.io.*;
import java.util.logging.Level;

public class TextParser {
    FileInputStream fstream;
    DataInputStream in;
    BufferedReader br;

    public TextParser(String fileName){
        try{
            fstream = new FileInputStream(fileName);
        } catch (FileNotFoundException e){
            System.out.println("Could not read file "+fileName);
            return;
        }
        in = new DataInputStream(fstream);
        br = new BufferedReader(new InputStreamReader(in));
    }

    public String parseText(){
        String line="", value="";
        //Read File Line By Line and concat values (paragraphs) between empty lines
        try{
            while ((line = br.readLine()) != null)
            {
                while(!(line.equals(""))){
                    value = value.concat(line);
                    line = br.readLine();
                }
               return value;
               //runHeuristics(value);
            }
        } catch (IOException e){
           // main.lgr.log(Level.WARNING, "Could not read text line");
        }
        return "bla";
    }



}
