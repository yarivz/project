import java.util.Vector;

public class Person implements Comparable{

    String name;
    String bornIn;
    double prBornIn;
    String diedIn;
    double prDiedIn;
    String profession;
    double prProf;

    public Person(String name, String bornInYear, double probBornIn, String diedInYear, double probDiedIn, String prof, double probProf){
        this.name = name;
        bornIn = bornInYear;
        diedIn = diedInYear;
        profession = prof;
        prBornIn = probBornIn;
        prDiedIn = probDiedIn;
        prProf = probProf;
    }

    public int compareTo(Object o){
        if (!(o instanceof Person)){
            return 1;
        }
        if (((Person)o).name.equals(this.name)|| isContained(((Person)o).name,this.name)){
            return 0;
        } else {
            return 1;
        }
    }

    public boolean equals(Object o){
      if (o!=null && o instanceof Person) return name.equals(((Person)o).name);
      else return false;
    }

    public String toString(){
        return name+':'+bornIn+':'+diedIn+':'+profession+'\n';
    }

    public boolean isContained(String a,String b){
        if (a.contains(" ") && b.contains(" ")){
            String[] as = a.split(" ");
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
