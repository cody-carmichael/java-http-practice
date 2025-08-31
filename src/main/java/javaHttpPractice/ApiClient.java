package javaHttpPractice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ApiClient {
	
	private final String baseUrl;
	private final HttpClient client;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public ApiClient(String baseUrl) {
		
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
		this.client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(5))
				.build();
	}
	
	// Public Methods //
	// GET /posts/{id}
	public Post getPost(int id) throws IOException, InterruptedException {
		HttpRequest req = base("GET", "/posts/"+  id).build();
		String body = send(req);
		return mapper.readValue(body, Post.class);
	}
	
	//GET /posts?userId=1
	public List<Post> listPosts(Map<String, String> query) throws IOException, InterruptedException {
		String path = "/posts" + (query == null || query.isEmpty() ? "" : toQuery(query));
		HttpRequest req = base("GET", path).build();
		String body = send(req);
		return mapper.readValue(body, new TypeReference<List<Post>>() {});
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//POST
	public Post createPost(PostCreate payload) throws IOException, InterruptedException {
		String json = mapper.writeValueAsString(payload);
		HttpRequest req = base("POST", "/posts")
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();
		
		String body = send(req);
		return mapper.readValue(body, Post.class);
	}
	
	
	//delete post by id
	public boolean deletePost(int id) throws IOException, InterruptedException {
		HttpRequest req = base("DELETE", "/posts/" + id).build();
		HttpResponse<Void> res = client.send(req, HttpResponse.BodyHandlers.discarding());
		//JSONPlaceholder returns 200; many real APIs return 204
		int sc = res.statusCode();
		if (sc / 100 !=2) {
			throw new ApiException(sc, "<no body>", "DELETE failed");
		}
		return true;
	}
	
	//let callers pass raw fields
	public Post updatePostPut(int id, int userId, String Title, String Body)
		throws IOException, InterruptedException {
		PostCreate payload = new PostCreate(userId, Title, Body);
		return updatePostPut(id, payload);
	}	
	//PUT /posts/{id} (replace full resource)
	public Post updatePostPut(int id, PostCreate payload) throws IOException, InterruptedException {
		String json = mapper.writeValueAsString(payload);
		HttpRequest req = base("PUT", "/posts/" + id)
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json))
				.build();
		String body = send(req);
		return mapper.readValue(body, Post.class);
	}
	
	
    // PATCH /posts/{id} (partial update)
	public Post updatePostPatch(int id, String newTitle, String newBody, Integer newUserId)
		throws IOException, InterruptedException {
		PostPatch patch = new PostPatch();
		if (newTitle  != null) patch.title  = newTitle;
	    if (newBody   != null) patch.body   = newBody;
	    if (newUserId != null) patch.userId = newUserId;
		return updatePostPatch(id, patch);
	}
	
    public Post updatePostPatch(int id, PostPatch payload) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(payload);
        HttpRequest req = base("PATCH", "/posts/" + id)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        String body = send(req);
        return mapper.readValue(body, Post.class);
    }
    
 // HEAD /posts
    public int headPosts() throws IOException, InterruptedException {
        HttpRequest req = base("HEAD", "/posts").method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<Void> res = client.send(req, HttpResponse.BodyHandlers.discarding());
        return res.statusCode();
    }
	

	//internals//
	//base method
	private HttpRequest.Builder base(String method, String path) {
		return HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + path))
				.timeout(Duration.ofSeconds(10))
				.header("Accept", "application/json");
				
	}
	
	//send
	private String send(HttpRequest req) throws IOException, InterruptedException {
		HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
		int sc = res.statusCode();
		String body = res.body() == null ? "" : res.body();
		if (sc / 100 !=2) {
			throw new ApiException(sc, body, "HTTP " + sc);
		}
		return body;
	}
	
	//query
	private static String toQuery(Map<String, String> q) {
		StringBuilder sb = new StringBuilder("?");
		boolean first = true;
		for (var e : q.entrySet()) {
			if (!first) sb.append('&');
			first = false;
			sb.append(encode(e.getKey())).append('=').append(encode(e.getValue()));
		}
		return sb.toString();
	}
	
	//encoder
	private static String encode(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}
	
	
	
	//simple exception with status code & body
	public static class ApiException extends RuntimeException {
		public final int statusCode;
		public final String responseBody;
		public ApiException(int statusCode, String responseBody, String message) {
			super(message);
			this.statusCode = statusCode;
			this.responseBody = responseBody;
		}
	}
	
}
