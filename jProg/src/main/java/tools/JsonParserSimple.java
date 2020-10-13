package tools;

public interface JsonParserSimple {
	public <T> T parseJson(String json, Class<T> clazz);
}