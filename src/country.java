public class Country {
	
	String name;
	double prName;
	String population;
	double prPopulation;
	
	public Country(String name, double prName, String population, double prPopulation)
	{
		this.name = name;
		this.prName = prName;
		this.population = population;
		this.prPopulation = prPopulation;
	}

	public Country() 
	{
		this.name = "";
		this.prName = 0;
		this.population = "unknown";
		this.prPopulation = 0;
	}
	
	public boolean equals(Country other)
	{
		return this.name.equals(other.name);
	}
}