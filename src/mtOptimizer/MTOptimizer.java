/*
 * 
 * 
 * 
 * It reads the rider data, which consists of which riders used which modes of transportation
 * each hour, as well as vehicle data, which consists of all information 
 * (unit number, ID, capacity, etc) relevant to each transportation type.
 * 
 * Then, based off of this information, it calculates and writes to a text file the data for 
 * the vehicles which should be in use for each hour of the day in order to minimize the number of
 * vehicles used.
 */

package mtOptimizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MTOptimizer {
	// lists
	private static ArrayList<Passenger> passengerList = new ArrayList<Passenger>(); // stores all riders
		// fleet arrayLists
	private static ArrayList<Transportation> busFleet = new ArrayList<Transportation>();
	private static ArrayList<Transportation> goBusFleet = new ArrayList<Transportation>();
	private static ArrayList<Transportation> streetcarFleet = new ArrayList<Transportation>();
	private static ArrayList<Transportation> goTrainFleet = new ArrayList<Transportation>();
	private static ArrayList<Transportation> subwayFleet = new ArrayList<Transportation>();
		// vehicle arrays
	private static Bus[] busList = new Bus[50];
	private static GoBus[] goBusList = new GoBus[15];
	private static Streetcar[] streetcarList = new Streetcar[30];
	private static GoTrain[] goTrainList = new GoTrain[10];
	private static Subway[] subwayList = new Subway[15];

	// passenger parameters
	private static String passengerId;
	private static String mode;
	private static String age;
	private static int hour;
	private static int date;

	// shared parameters
	private static int unit;
	private static int busId;
	private static int capacity;
	
	// streetcar-specific parameters
	private static String letterCapacity;

	// subway-specific parameters
	private static String status; // the status of the subway
	private static int operationalDate; // the date of operation for the subway
	private static int numberCars; // the number of cars on the subway
	private static int initialCapacity;
	private static int subId;

	// files
	private static File inOperationFleets = new File("src/InOperationFleets.txt");
	private static File buses = new File("src/input/buses.txt");
	private static File goBuses = new File("src/input/gobuses.txt");
	private static File goTrains = new File("src/input/gotrains.txt");
	private static File streetcars = new File("src/input/streetcars.txt");
	private static File subways = new File("src/input/subways.txt");
	

	public static void main(String[] args) {
		parseRiders(); // parse data for each rider
		
		// parse vehicle data files
		try {
			// initialize buffered readers
			BufferedReader busesBr = new BufferedReader(new FileReader(buses));
			BufferedReader goBusesBr = new BufferedReader(new FileReader(goBuses));
			BufferedReader goTrainsBr = new BufferedReader(new FileReader(goTrains));
			BufferedReader streetcarsBr = new BufferedReader(new FileReader(streetcars));
			BufferedReader subwaysBr = new BufferedReader(new FileReader(subways));
			
			parseVehicle(buses, busesBr, "bus");
			parseVehicle(goBuses, goBusesBr, "goBus");
			parseVehicle(goTrains, goTrainsBr, "goTrain");
			parseVehicle(streetcars, streetcarsBr, "streetcar");
			parseVehicle(subways, subwaysBr, "subway");

			// close buffered readers
			busesBr.close();
			goBusesBr.close();
			goTrainsBr.close();
			streetcarsBr.close();
			subwaysBr.close();
		}
		catch (IOException e) {
			System.out.println("File I/O error");
		}
		
		// sort vehicle arrays from least capacity to greatest
		sortVehicleList(subwayList);
		sortVehicleList(goTrainList);
		sortVehicleList(streetcarList);
		sortVehicleList(busList);
		sortVehicleList(goBusList);

		// write optimal fleet data for each vehicle to InOperationFleets.txt
		try {
			// use this printwriter to write each fleet to the text file
			PrintWriter pw = new PrintWriter(inOperationFleets);
			writeFleet(subwayList, subwayFleet, "S", pw);
			writeFleet(goTrainList, goTrainFleet, "G", pw);
			writeFleet(streetcarList, streetcarFleet, "X", pw);
			writeFleet(busList, busFleet, "C", pw);
			writeFleet(goBusList, goBusFleet, "D", pw);
			pw.close(); // close printwriter
			
		} catch (IOException e) {
			System.out.println("File I/O error");
		}
	}


	/*
	 * This method parses the data contained in ridership.txt.
	 * Error lines are added to the errorlog.txt file and skipped. 
	 * Otherwise, for each non-error line, a passenger is added to a passenger arraylist.
	 */
	private static void parseRiders() {
		File ridership = new File("src/input/ridership.txt");
		File errorLog = new File("src/errorlog.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(ridership));
			PrintWriter pw = new PrintWriter(errorLog);
			String line;

			// go through each line of ridership file
			while ((line = br.readLine()) != null) {
				try {
					// split line around commas
					String[] strArr = line.split(",");
					// assign passenger, mode, and age variables
					passengerId = strArr[0];
					mode = strArr[1];
					age = strArr[2];
					// For the purpose of finding erros, check if the id, date or hour fields contain a letter
					boolean idHasLetter = strArr[0].matches(".*\\D+.*");
					boolean dateHasLetter = strArr[strArr.length - 1].matches(".*\\D+.*");
					boolean hourHasLetter = strArr[3].matches(".*\\D+.*");

					// Now we look for errors and store them in an error log file
					// if the transportation mode entry contains an invalid letter
					if (!(mode.contains("S") || mode.contains("G") || mode.contains("X") || mode.contains("C")
							|| mode.contains("D"))) {
						pw.write(line + "\n");
						pw.write("Mode field contains invalid letter.\n\n");
						continue;
					}
					// if the age entry contains an invalid letter
					else if (!(age.contains("C") || age.contains("A") || age.contains("S"))) {
						pw.write(line + "\n");
						pw.write("Age field contains letter.\n\n");
						continue;
					}

					// if the id contains any letters
					else if (idHasLetter == true && !(passengerId.contentEquals("*")) && !(passengerId.contains("T"))) {
						pw.write(line + "\n");
						pw.write("ID field contains letter.\n\n");
						continue;
					}

					// if the date contains any letters
					else if (dateHasLetter == true) {
						pw.write(line + "\n");
						pw.write("Date field contains letter.\n\n");
						continue;
					}

					// if the date contains any letters
					else if (hourHasLetter == true) {
						pw.write(line + "\n");
						pw.write("Hour field contains letter.\n\n");
						continue;
					}

					// if there are only four fields
					else if (strArr.length != 5) {
						pw.write(line + "\n");
						pw.write("There is a missing or extra field.\n\n");
						continue;
					}

					// if there is no id
					else if (strArr[0].isEmpty()) {
						pw.write(line + "\n");
						pw.write("There is no ID.\n\n");
						continue;
					}

					// if no error is detected for the current line, create a 
					// new passenger object and add it to the passengers arraylist
					else {
						hour = Integer.parseInt(strArr[3]);
						date = Integer.parseInt(strArr[4]);
						passengerList.add(new Passenger(passengerId, mode, age, hour, date));
					}
				}
				// catches any errors not caught in the above statements
				catch (Exception e) {
					pw.write(line + "\n");
					pw.write("Unknown error.\n");
					continue;
				}
			}
			br.close(); // close buffered reader
			pw.close(); // close print writer
		} catch (IOException e) {
			System.out.println("File I/O error");
		}
	}
	
	
	// This method parses the data for any of the vehicle text files.
	// It will create new objects of the appropriate type and add them into an array of that type.
	private static void parseVehicle(File file, BufferedReader br, String mode) {
		try {
			String line;
			int count = 0;
			// go through each line of text file
			while ((line = br.readLine()) != null) {
				String[] strArr = line.split(",");
				// assign variables common to all vehicle types
				unit = Integer.parseInt(strArr[0]);
				busId = Integer.parseInt(strArr[1].substring(2));
				
				// We must overwrite the goTrain ID because it 
				// only has 1 letter before its ID rather than 2
				if (mode.contentEquals("goTrain")) {
					busId = Integer.parseInt(strArr[1].substring(1));
				}
				
				// streetcar must be treated specially because its capacity is a String
				if (!(mode.contentEquals("streetcar"))) {
					capacity = Integer.parseInt(strArr[2]);
				}
				else {
					// assign initial capacity (which is a letter, unlike the other vehicle types)
					letterCapacity = strArr[2];
					// assign capacity
					if (strArr[2].contentEquals("S")) {
						capacity = 40;
					} else if (strArr[2].contentEquals("D")) {
						capacity = 80;
					}
				}

				// subway type:
				// subway has extra parameters which must be assigned
				if (mode.equals("subway")) {
					subId = Integer.parseInt(strArr[1]);
					numberCars = Integer.parseInt(strArr[2]);
					initialCapacity = Integer.parseInt(strArr[3]);
					capacity = Integer.parseInt(strArr[3]) * numberCars;
					status = strArr[4];
					operationalDate = Integer.parseInt(strArr[5]);
					
					// only make new subway if it is "available"
					if (status.contentEquals("A")) {
						subwayList[count] = new Subway(unit, subId, initialCapacity, capacity, numberCars, status, operationalDate);
	 					count++;
					}
				}
				// goTrain type:
				if (mode.equals("goTrain")) {
					goTrainList[count] = new GoTrain(unit, busId, capacity);
					count++;
				}
				// streetcar type:
				if (mode.equals("streetcar")) {
					streetcarList[count] = new Streetcar(unit, busId, capacity, letterCapacity);
					count++;
				}
				// bus type:
				if (mode.equals("bus")) {
					busList[count] = new Bus(unit, busId, capacity);
					count++;
				}
				// goBus type:
				if (mode.equals("goBus")) {
					goBusList[count] = new GoBus(unit, busId, capacity);
					count++;
				}
			}
			br.close(); // close buffered reader
		}
		catch (IOException e) {
			System.out.println("File I/O error");
		}
	}

	
	// This method returns the number of riders on a certain mode of transportation during a certain hour.
	private static int getNumRiders(int hour, String mode) {
		float numRiders = 0;
		// go through passenger list
		for (int i = 0; i < passengerList.size(); i++) {
			// for the specified hour, look through each passenger's ride hour
			if (passengerList.get(i).getHour() == hour && passengerList.get(i).getMode().equals(mode)) {
				// if they match, sum all passengers for that specific hour & transportation mode
				// children count as 0.75 passengers each
				if (passengerList.get(i).getAge().equals("C")) {
					numRiders += 0.75;
				// adults count as 1 passenger each
				} else if (passengerList.get(i).getAge().equals("A")) {
					numRiders += 1;
				// seniors count as 1.25 passengers each
				} else {
					numRiders += 1.25;
				}
			}
		}
		// round all passenger numbers up
		return (int) Math.ceil(numRiders);
	}

	
	// This method uses bubble sort to sort a vehicle list
	// from smallest to greatest capacity
	private static void sortVehicleList(Transportation[] list) {
		int arrayLength = list.length;
		Transportation temp = null;
		for (int i = 0; i < arrayLength; i++) {
			for (int j = 1; j < (arrayLength - i); j++) {
				if (list[j - 1].getCapacity() > list[j].getCapacity()) {
					temp = list[j - 1];
					list[j - 1] = list[j];
					list[j] = temp;
				}
			}
		}
	}

	
	/*
	 * This method calculates the optimal fleet for a specified transportation method.
	 * It adds every vehicle in the fleet to an array list.
	 */
	private static void getOptimalFleet(Transportation[] list, ArrayList<Transportation> fleet, int hour, String mode) {
		int seats = 0; // represents the number of existing seats currently in the fleet
		int count = 0; // keeps track of how many vehicles have been added to the fleet
		// retrieve number of riders for the desired hour and mode
		int riders = getNumRiders(hour, mode);
		// go through each vehicle in the list
		for (int x = 0; x < list.length; x++) {
			// check if any single vehicle can store all riders for that specific hour
			if (list[x].getCapacity() >= riders && riders != 0) {
				// if yes, add that vehicle to the fleet
				fleet.add(list[x]);
				return; // all passengers have seats so the fleet list is complete
			}
		}
		// Otherwise, multiple vehicles are needed to store all riders for that hour.
		// While there are still more riders needing seats:
		while (seats < riders && riders != 0) {
			seats = 0;
			// add the largest, then next largest (and so on) capacity vehicles to the fleet
			fleet.add(list[list.length - 1 - count]);
			count++;

			// after adding a vehicle, calculate how many riders will fit onto the current fleet
			for (int y = 0; y < fleet.size(); y++) {
				seats += fleet.get(y).getCapacity();
			}

			// if we now have enough seats for every rider
			if (seats > riders) {
				// We might be wasting seats and could use a smaller vehicle in the fleet.
				// So, we remove the most recently added one:
				fleet.remove(fleet.size() - 1);
				// Then, we add the smallest capacity vehicle as possible to the fleet
				for (int i = 0; i < list.length; i++) {
					if ((list[i].getCapacity() + (seats - fleet.get(fleet.size() - 1).getCapacity())) >= riders) {
						fleet.add(list[i]); // add that vehicle to the fleet
						return; // all riders have seats- the fleet is complete
					}
				}
			}
		}
	}

	
	/*
	 * For each vehicle type, this method writes the data regarding 
	 * the optimal fleet to the InOperationFleets.txt file.
	 */
	private static void writeFleet(Transportation[] list, ArrayList<Transportation> fleet, String mode, PrintWriter pw) {
		String transportationMode = null;
		String header = null;
		String shortForm = null;
		
		// Subways:
		if (mode.equals("S")) {
			transportationMode = "subway";
			header = "[Subways]";
		}
		// Go Trains:
		if (mode.equals("G")) {
			transportationMode = "gotrain";
			header = "[GoTrains]";
			shortForm = "G";
		}
		// Streetcars:
		if (mode.equals("X")) {
			transportationMode = "streetcar";
			header = "[Streetcars]";
			shortForm = "SC";
		}
		// City buses:
		if (mode.equals("C")) {
			transportationMode = "bus";
			header = "[Buses]";
			shortForm = "MB";
		}
		// Go Buses:
		if (mode.equals("D")) {
			transportationMode = "gobus";
			header = "[GoBuses]";
			shortForm = "GB";
		}

		// print the header at the beginning of each vehicle type section
		// We don't want a space at the beginning
		if (header.contentEquals("[Subways]")){
			pw.write(header + "\n");
		}
		else {
			pw.write("\n" + header + "\n");
		}
		
		// go through each hour
		for (int h = 1; h < 25; h++) {
			fleet.clear(); // clear the fleet from the previous hour
			// print the hour at the beginning of each hour
			pw.write("[Hour = " + h + "]\n");

			// find the optimal fleet for the current mode and hour
			getOptimalFleet(list, fleet, h, mode);

			// print information for every vehicle in the fleet (for that hour and transportation mode)
			for (int x = 0; x < fleet.size(); x++) {
				
				// Special case for subways- there are more fields to write to the file
				if (mode.equals("S")) {
					// write description to text file
					pw.write(transportationMode + ": " + fleet.get(x).getUnit() + "," +
							fleet.get(x).getId() + "," + fleet.get(x).getNumCars() + "," +
							fleet.get(x).getInitialCapacity() + "," + fleet.get(x).getStatus() + 
							"," + fleet.get(x).getOperationalDate() + "\n");
				}
				
				// Special case for streetcars- they have a letter rather than number capacity
				else if (mode.equals("X")){
					// write description to text file
					pw.write(transportationMode + ": " + fleet.get(x).getUnit() + 
							"," + shortForm + fleet.get(x).getId() + ","
							+ fleet.get(x).getLetterCapacity() + "\n");
				}
				
				// For all other transportation types:
				else {
					// write description to text file
					pw.write(transportationMode + ": " + fleet.get(x).getUnit() + 
							"," + shortForm + fleet.get(x).getId() + ","
							+ fleet.get(x).getCapacity() + "\n");
				}
			}
			// print number of vehicles in operation for that hour and transportation mode
			if (h == 24 && mode.contentEquals("D")) {
				// don't print blank line at the very end
				pw.write("[Count=" + fleet.size() + "]");
			}
			else {
				pw.write("[Count=" + fleet.size() + "]\n\n");
			}
		}
	}
}