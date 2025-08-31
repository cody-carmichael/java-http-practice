package javaHttpPractice;

public class PostCreate {
	
	public int userId;
	public String title;
	public String body;
	
	public PostCreate() {
		
	}
	public PostCreate(int userId, String title, String body) {
		this.userId = userId;
		this.title = title;
		this.body = body;
	}

}
