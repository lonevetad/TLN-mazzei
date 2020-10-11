package testPers.tJackson;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJackson {

	public static void main(String[] args) {
		Car car;
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println("starting ...");
		car = new Car("yellow", "renault");
		System.out.println(car);
		try {
			objectMapper.writeValue(new File("target/car.json"), car);
			System.out.println("done");
		} catch (IOException e) { // JsonGenerationException | JsonMappingException
			e.printStackTrace();
		}
	}
}