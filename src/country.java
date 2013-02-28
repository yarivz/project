public class country {
	
	String name;
	int population;
	double prPopulation;
	
	public country(String name, int population, double prPopulation)
	{
		this.name = name;
		this.population = population;
		this.prPopulation = prPopulation;
	}

	public country() 
	{
		this.name = "";
		this.population = 0;
		this.prPopulation = 0;
	}
	
	public boolean equals(country other)
	{
		return this.name.equals(other.name);
	}
}