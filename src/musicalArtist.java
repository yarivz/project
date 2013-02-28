public class musicalArtist {
	
	String name;
	String genre;
	double prGenre;
	String type;
	double prType;
	String nationality;
	double prNationality;
	
	public musicalArtist(String name, String genre, String type, String nationality)
	{
		this.name = name;
		this.genre = genre;
		this.type = type;
		this.nationality = nationality;
	}

	public musicalArtist() 
	{
		name = "";
		genre = "unknown";
		prGenre = 0;
		type = "unknown";
		prType = 0;
		nationality = "unknown";
		prNationality = 0;
	}
	
	public boolean equals(musicalArtist other)
	{
		return this.name.equals(other.name);
	}
}
