package javaHttpPractice;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostPatch {
	public String title;
	public String body;
	public int userId;
}
