
public class Car implements Comparable<Car>{
	private String VIN;
	private String make;
	private String model;
	private int price;
	private int mileage;
	private String color;
	private boolean minPrice;
	
	public Car(String VIN, String make, String model, int price, int mileage, String color, boolean minPrice) {
		this.VIN = VIN;
		this.make = make;
		this.model = model;
		this.price = price;
		this.mileage = mileage;
		this.color = color;
		this.minPrice = minPrice;
	}
	
	public Car() {
		
	}

	/**
	 * @return the vIN
	 */
	public String getVIN() {
		return VIN;
	}

	/**
	 * @param vIN the vIN to set
	 */
	public void setVIN(String vIN) {
		VIN = vIN;
	}

	/**
	 * @return the make
	 */
	public String getMake() {
		return make;
	}

	/**
	 * @param make the make to set
	 */
	public void setMake(String make) {
		this.make = make;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * @return the mileage
	 */
	public int getMileage() {
		return mileage;
	}

	/**
	 * @param mileage the mileage to set
	 */
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	
	/**
	 * @param minPrice whether to compare price or mileage
	 */
	public void setMinPrice(boolean minPrice) {
		this.minPrice = minPrice;
	}
	
	public String toString() {
		String str = "";
		str += String.format("	VIN: %s\n", VIN);
		str += String.format("	Make: %s\n", make);
		str += String.format("	Model: %s\n", model);
		str += String.format("	Price: $%d\n", price);
		str += String.format("	Mileage: %d\n", mileage);
		str += String.format("	Color: %s\n", color);
		return str;
	}

	@Override
	public int compareTo(Car arg0) {
		if(minPrice) {//compare prices
			if(this.price > arg0.price) {
				return 1;
			} else if(this.price < arg0.price) {
				return -1;
			} else {
				return 0;
			}
		} else {//compare mileage
			if(this.mileage > arg0.mileage) {
				return 1;
			} else if(this.mileage < arg0.mileage) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public Car copy() {
		return new Car(VIN, make, model, price, mileage, color, minPrice);
	}
	
}
