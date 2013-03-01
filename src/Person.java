import java.util.Vector;

public class Person implements Comparable{

    String name;
    double prName;
    String bornIn;
    double prBornIn;
    String diedIn;
    double prDiedIn;
    String profession;
    double prProf;

    public Person(String name, double probName, String bornInYear, double probBornIn, String diedInYear, double probDiedIn, String prof, double probProf){
        this.name = name;
        bornIn = bornInYear;
        diedIn = diedInYear;
        profession = prof;
        prName = probName;
        prBornIn = probBornIn;
        prDiedIn = probDiedIn;
        prProf = probProf;
    }

    public int compareTo(Object o){
        if (!(o instanceof Person)){
            return 1;
        }
        if (((Person)o).getName().equals(this.name)|| isContained(((Person)o).getName(),this.name)){
            return 0;
        } else {
            return 1;
        }
    }

    public boolean equals(Object o){
      if (o!=null && o instanceof Person) return name.equals(((Person)o).getName());
      else return false;
    }

    public String getName(){
        return name;
    }

    public double getPrName(){
        return prName;
    }

    public String getBornIn(){
        return bornIn;
    }

    public double getPrBornIn(){
        return prBornIn;
    }

    public void addBornIn(String str){
        bornIn=str;
    }

    public String getDiedIn(){
        return diedIn;
    }

    public double getPrDiedIn(){
        return prDiedIn;
    }

    public void addDiedIn(String str){
        diedIn=str;
    }

    public String getProf(){
        return profession;
    }

    public double getPrProf(){
        return prBornIn;
    }

    public void addProf(String str){
        profession=str;
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
