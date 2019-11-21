# MT-Optimizer
A prototype tool which optimizes the hourly fleet sizes of the vehicles used (i.e., buses, subways, trains) in the city of Toronto.

## mtOptimizer .java files
MTOptimizer.java: Reads the rider data, which consists of which riders used which modes of transportation each hour, as well as vehicle data, which consists of all information (unit number, ID, capacity, etc) relevant to each transportation type. Then, based off of this information, it calculates and writes to a text file the data for the vehicles which should be in use for each hour of the day in order to minimize the number of vehicles used.

## Input .txt files
buses.txt, gotrains.txt, gobuses.txt, streetcars.txt, and subways.txt each contain data specific to their own method of transportation (vehicle id's, number of riders/hour, etc).
ridership.txt shows all rider information for a single day (method of transportation chosen, time of day, etc).
