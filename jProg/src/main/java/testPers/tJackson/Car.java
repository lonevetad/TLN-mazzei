package testPers.tJackson;

public class Car {

	private String color;
	private String type;

	// standard getters setters

	public String getColor() { return color; }

	public Car() {}

	public Car(String color, String type) {
		super();
		this.color = color;
		this.type = type;
	}

	public String getType() { return type; }

	public void setColor(String color) { this.color = color; }

	public void setType(String type) { this.type = type; }

	//

	@Override
	public String toString() { return "Car [color=" + color + ", type=" + type + "]"; }
}