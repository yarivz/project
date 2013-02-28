
public class Person implements Comparable{

    String name;
    String bornIn;
    String diedIn;
    String profession;

    public Person(String name, String bornIn, String diedIn, String profession){
        this.name = name;
        this.bornIn = bornIn;
        this.diedIn = diedIn;
        this.profession = profession;
    }

    public int compareTo(Object o){
        if (!(o instanceof Person)){
            return 1;
        }
        if (((Person)o).getName().equals(this.name)|| iscontained(((Person)o).getName(),this.name)){
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

    public String getBornIn(){
        return bornIn;
    }

    public void addBornIn(String str){
        if(bornIn.isEmpty()) bornIn=str;
        else bornIn = bornIn+'('+str+')';
    }
    public String getDiedIn(){
        return diedIn;
    }

    public void addDiedIn(String str){
        if(diedIn.isEmpty()) diedIn=str;
        else diedIn = diedIn+'('+str+')';
    }
    public String getProf(){
        return profession;
    }

    public void addProf(String str){
        if(profession.isEmpty()) profession=str;
        else profession = profession+'('+str+')';
    }

    public String toString(){
        return name+':'+bornIn+':'+diedIn+':'+profession+'\n';
    }

    public boolean iscontained(String a,String b){
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
