package tap.europe;

public class EuropeanaLocation {
	
	private int ID;
	private String Name;
	private double Longitude, Latitude;
	private String Description;
	private int Visited;

	public EuropeanaLocation() {
		// TODO Auto-generated constructor stub
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getVisited() {
		return Visited;
	}

	public void setVisited(int visited) {
		Visited = visited;
	}
}
