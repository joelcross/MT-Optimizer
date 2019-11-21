package mtOptimizer;

// This abstract class contains all hierarchy-wide attributes and methods.
public abstract class Transportation {
	// variables shared between multiple vehicle types
	private int unit;
	private int id;
	private int capacity;
	
	// subway variables
	private int numberCars;
	private String status;
	private int operationalDate;
	private int initialCapacity; 
	
	// streetcar variable
	private String letterCapacity; // the letter capacity of the streetcar ("S" or "D")
	
	// general setter/getter methods
	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getId() {
		return this.id;
	}
	
	public int getUnit() {
		return this.unit;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	// subway-specific getter/setter methods
	public int getOperationalDate() {
		return this.operationalDate;
	}
	
	public int getNumCars() {
		return this.numberCars;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public int getInitialCapacity() {
		return this.initialCapacity;
	}
	
	public void setOperationalDate(int operationalDate) {
		this.operationalDate = operationalDate;
	}
	
	public void setNumCars(int numberCars) {
		this.numberCars = numberCars;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setInitialCapacity(int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}
	
	// streetcar-specific getter/setter methods
	public void setLetterCapacity(String letterCapacity) {
		this.letterCapacity = letterCapacity;
	}
	
	public String getLetterCapacity() {
		return this.letterCapacity;
	}
}
