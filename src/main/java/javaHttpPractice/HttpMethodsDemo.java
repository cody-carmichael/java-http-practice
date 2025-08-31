package javaHttpPractice;

import java.util.List;
import java.util.Map;

public class HttpMethodsDemo {

	public static void main(String[] args) {
		try {
			
			ApiClient api = new ApiClient("https://jsonplaceholder.typicode.com");
			
			//HEAD
			int headStatus = api.headPosts();
			System.out.println("HEAD /posts status= " + headStatus);
			
			//GET
			Post p1 = api.getPost(1);
			System.out.println("GET 1 post title : " + p1.title);
			
			
			//QUERY
			List<Post> user1 = api.listPosts(Map.of("userId", "1"));
			System.out.println("count how many where userId = 1 " + user1.size());
			
			//POST
			PostCreate create = new PostCreate(99, " Hello This is Cody ", " Testing a POST Method ");
			Post created = api.createPost(create);
			System.out.println("POST id: " + created.id + "title : " + created.title + "body: " + created.body);
			
			//PUT
			Post afterPut = api.updatePostPut(1, 77, "Updated Title", "Replaced Body");
			System.out.println("Updated Title :" + afterPut.title);
			
			//GET Post after PUT
			Post getUpdatedPost = api.getPost(afterPut.id);
			System.out.println("Updated Post title : " + getUpdatedPost.title + "Updated Post Body : " + getUpdatedPost.body);
			
			//PATCH
			Post afterPatch = api.updatePostPatch(1, "patched the title", null, null);
			
			//GET Post after patch 
			Post getPatchedPost = api.getPost(afterPatch.id);
			
			


			
			
			
			
			
			
			
		
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			System.out.println("Patched Post Title : " + getPatchedPost.title);
	
			//DELETE
			boolean deleted = api.deletePost(1);
			System.out.println("Deleted Post Id : " + deleted);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
