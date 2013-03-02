public class Country {
	
	String name;           //Country's name
	String population;     //Country's population
	double prPopulation;   //probability of Country's population correctness
	
	public Country(String name, String population, double prPopulation)
	{
		this.name = name;
		this.population = population;
		this.prPopulation = prPopulation;
	}
    //compare Country objects, by comparing their name values
    public boolean equals(Country other)
	{
		return this.name.equals(other.name);
	}
}