import java.util.Vector;

public class Person {

    String name;       //Person's name - assumed unique value
    String bornIn;     //Person's year of birth  (Y.O.B)
    double prBornIn;   //Person's probability of Y.O.B correctness
    String diedIn;     //Person's year of death  (Y.O.D)
    double prDiedIn;   //Person's probability of Y.O.D correctness
    String profession; //Person's profession
    double prProf;     //Person's probability of profession correctness

    public Person(String name, String bornInYear, double probBornIn, String diedInYear, double probDiedIn, String prof, double probProf){
        this.name = name;
        bornIn = bornInYear;
        diedIn = diedInYear;
        profession = prof;
        prBornIn = probBornIn;
        prDiedIn = probDiedIn;
        prProf = probProf;
    }

    //compare Person objects, by comparing their name values
    public boolean equals(Person p){
       return this.name.equals(p.name);
    }
    //String representation of Person
    public String toString(){
        return name+", born:"+bornIn+", died:"+diedIn+", profession:"+profession+'\n';
    }

    public boolean isContained(String a,String b){     //check whether two people are the same person,
        if (a.contains(" ") && b.contains(" ")){       //when one has a different name, but it contains in it the other's name
            String[] as = a.split(" ");                //like first-middle-last name VS. first-last name, for example
            boolean ansa=true;
            for(String s:as){
                if(!b.contains(s)){
                    ansa=false;
                    break;
                }
            }
            String[] bs = b.split(" ");
            boolean ansb=true;
            for(String s:bs){
                if(!a.contains(s)){
                    ansb=false;
                    break;
                }
            }
            return ansa || ansb;
        }
        return false;
    }
}
