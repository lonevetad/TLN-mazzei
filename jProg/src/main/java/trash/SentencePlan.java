package trash;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Deprecated
public class SentencePlan implements Serializable {

	//

	// TODO TO STRING

	//

	// assumption: using Jackson as parser
	public static SentencePlan fromJson(String json) {
		SentencePlan sp = null;
//		JsonObject jo;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			sp = objectMapper.readValue(json, SentencePlan.class);
		} catch (JsonProcessingException e) { // JsonMappingException
			e.printStackTrace();
		}
		return sp;
	}
}