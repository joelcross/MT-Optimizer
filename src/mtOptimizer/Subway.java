package mtOptimizer;

// This class contains the constructor to create a Subway object.
public class Subway extends Transportation {
	public Subway(int unit, int id, int initialCapacity, int capacity, int numberCars, String status, int operationalDate) {
		setUnit(unit);
		setId(id);
		setCapacity(capacity);
		setStatus(status);
		setOperationalDate(operationalDate);
		setNumCars(numberCars);
		setInitialCapacity(initialCapacity);
	}
}
