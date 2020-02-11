import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class CarTracker {
	//priority queue
	public static void main(String[] args) throws FileNotFoundException {
		//read from file
		File input = new File("cars2.txt");
		Scanner fileIn = new Scanner(input);
		HashMap<String, Integer> vinHash = new HashMap<String, Integer>();
		IndexNode head = null;
		
		fileIn.nextLine();
		//fileIn.useDelimiter(":");
		int index = 0;
		IndexMinPQ pricePQ = new IndexMinPQ(100);
		IndexMinPQ mileagePQ = new IndexMinPQ(100);
		Car cur;
		while(fileIn.hasNext()) {
			
			fileIn.useDelimiter(":");
			cur = new Car();
			cur.setVIN(fileIn.next());
			cur.setMake(fileIn.next());
			cur.setModel(fileIn.next());
			cur.setPrice(fileIn.nextInt());
			cur.setMileage(fileIn.nextInt());
			fileIn.useDelimiter("\n");
			String temp = fileIn.next();
			cur.setColor(temp.substring(1, temp.length()));
			cur.setMinPrice(true);
			
			fileIn.nextLine();
			
			vinHash.put(cur.getVIN(), index++);
			
			Car tempCar = cur.copy();
			tempCar.setMinPrice(false);
			
			//add cars to PQs
			pricePQ.insert(vinHash.get(cur.getVIN()), cur);
			mileagePQ.insert(vinHash.get(cur.getVIN()), tempCar);
		}
		
		//input loop
		Scanner in = new Scanner(System.in);
		boolean end = false;
		while(!end) {
			System.out.println("What do you want to do?");
			System.out.println("	1) Add a car");
			System.out.println("	2) Update a car");
			System.out.println("	3) Remove a car");
			System.out.println("	4) Retrieve the lowest price car");
			System.out.println("	5) Retrieve the lowest mileage car");
			System.out.println("	6) Retrieve the lowest price car by make and model");
			System.out.println("	7) Retrieve the lowest mileage car by make and model");
			System.out.println("	8) Quit");
			System.out.print("Enter your option:");
			
			int option = in.nextInt();
			in.nextLine();
			
			String vin;
			Car c;
			switch(option) {
				case 1://add car
					c = new Car();
					System.out.print("What is the VIN?");
					c.setVIN(in.next());
					in.nextLine();
					System.out.print("What is the make?");
					c.setMake(in.next());
					in.nextLine();
					System.out.print("What is the model?");
					c.setModel(in.next());
					in.nextLine();
					System.out.print("What is the price?");
					c.setPrice(in.nextInt());
					in.nextLine();
					System.out.print("What is the mileage?");
					c.setMileage(in.nextInt());
					in.nextLine();
					System.out.print("What is the color?");
					c.setColor(in.next());
					in.nextLine();
					
					c.setMinPrice(true);
					
					if(head == null) {
						vinHash.put(c.getVIN(), index++);
					} else {
						vinHash.put(c.getVIN(), head.index);
						head = head.pop();
					}
					//ADD ADDCAR TO PRIORITY QUEUE
					
					Car tempCar = c.copy();
					tempCar.setMinPrice(false);
					
					//add cars to PQs
					pricePQ.insert(vinHash.get(c.getVIN()), c);
					mileagePQ.insert(vinHash.get(c.getVIN()), tempCar);
					
					System.out.println("Car successfully added\n");
				break;
				case 2://update car
					System.out.print("What is the VIN?");
					vin = in.next();
					in.nextLine();
					
					//RETRIEVE CAR FROM PRIORITY QUEUE
					c = pricePQ.keyOf(vinHash.get(vin));
					System.out.println();
					System.out.println(c);
					
					System.out.println("What would you like to update?");
					System.out.println("	1) The price");
					System.out.println("	2) The mileage");
					System.out.println("	3) The color");
					System.out.print("Enter your option:");
					
					int opt = in.nextInt();
					in.nextLine();
					
					switch(opt) {
						case 1:
							System.out.print("Enter the new price:");
							
							c.setPrice(in.nextInt());
							in.nextLine();
						break;
						case 2:
							System.out.print("Enter the new mileage:");
							
							c.setMileage(in.nextInt());
							in.nextLine();
						break;
						case 3:
							System.out.print("Enter the new color:");
							
							c.setColor(in.next());
							in.nextLine();
						break;
					}
					System.out.println();
					System.out.println(c);
					
					//change key
					pricePQ.changeKey(vinHash.get(vin), c);
					Car temp = c.copy();
					temp.setMinPrice(false);
					mileagePQ.changeKey(vinHash.get(vin), c);
					
				break;
				case 3://remove car
					System.out.print("What is the VIN?");
					vin = in.next();
					in.nextLine();
				
					c = pricePQ.keyOf(vinHash.get(vin));
					System.out.println();
					System.out.println(c);
					
					pricePQ.delete(vinHash.get(vin));
					mileagePQ.delete(vinHash.get(vin));
					
					if(head == null) {
						head = new IndexNode(vinHash.get(vin));
					} else {
						head = head.push(new IndexNode(vinHash.get(vin)));
					}
					
					vinHash.remove(vin);
				break;
				case 4://lowest price car
					System.out.println("Car with the lowest price:");
					c = pricePQ.minKey();
					
					System.out.println(c);
				break;
				case 5://lowest mileage car
					System.out.println("Car with the lowest mileage:");
					c = mileagePQ.minKey();
					
					System.out.println(c);
					
				break;
				case 6://lowest price by make and model
					System.out.print("What is the make?");
					String make = in.next();
					in.nextLine();
					
					System.out.print("What is the model?");
					String model = in.next();
					in.nextLine();
					
					c = pricePQ.minMakeModel(make, model);
					
					System.out.println(c);
					if(c != null) {
						System.out.printf("%s %s with the lowest mileage:\n", make, model);
						System.out.println(c);
					} else {
						System.out.println("Car not found");
					}
				break;
				case 7://lowest mileage by make and model
					System.out.print("What is the make?");
					String mak = in.next();
					in.nextLine();
					
					System.out.print("What is the model?");
					String mod = in.next();
					in.nextLine();
					
					c = pricePQ.minMakeModel(mak, mod);
					
					if(c != null) {
						System.out.printf("%s %s with the lowest mileage:\n", mak, mod);
						System.out.println(c);
					} else {
						System.out.println("Car not found");
					}
					
				break;
				case 8://quit
					in.close();
					
					System.exit(0);
				break;
				default:
					System.out.println("Invalid entry");
				
			}
		}
	}

}
