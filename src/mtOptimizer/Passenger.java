package mtOptimizer;

/*
 * This class contains the constructor to make a Passenger object.
 * It also contains several "get" methods in order to access 
 * passenger variables in other classes.
 */
public class Passenger {
	private String id;
	private String mode;
	private String age;
	private int hour;
	private int date;
	
	public Passenger(String id, String mode, String age, int hour, int date) {
		this.id = id;
		this.mode = mode;
		this.age = age;
		this.hour = hour;
		this.date = date;
	}
	
	public int getHour() {
		return this.hour;
	}
	
	public String getAge() {
		return this.age;
	}
	
	public String getMode() {
		return this.mode;
	}
}
