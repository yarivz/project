public class Country {
	
	String name;
	String population;
	double prPopulation;
	
	public Country(String name, String population, double prPopulation)
	{
		this.name = name;
		this.population = population;
		this.prPopulation = prPopulation;
	}

	public Country() 
	{
		this.name = "";
		this.population = "unknown";
		this.prPopulation = 0;
	}
	
	public boolean equals(Country other)
	{
		return this.name.equals(other.name);
	}
}