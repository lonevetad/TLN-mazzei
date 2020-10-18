package tools.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tools.JsonParserSimple;

public class JsonParserJackson implements JsonParserSimple {
	static final ObjectMapper om = new ObjectMapper();

	@Override
	public <T> T parseJson(String json, Class<T> clazz) {
		try {
			return om.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}