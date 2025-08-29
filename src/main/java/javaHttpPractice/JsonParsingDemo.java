package javaHttpPractice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonParsingDemo {
	//here i will work with the data
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		//A) Fetch a single post (object JSON)
		String singleJson = httpGet("https://jsonplaceholder.typicode.com/posts/1");
		Post one = mapper.readValue(singleJson, Post.class);
		System.out.println("Single Post Title: "+  one.title);
		
		//B) fetch a lot of posts
		String manyJson = httpGet("https://jsonplaceholder.typicode.com/posts");
		List<Post> posts = mapper.readValue(manyJson, new TypeReference<List<Post>>() {});
		System.out.println("Total posts: "+ posts.size());
		
		//C) Simple analytics (practicing working with the data)
		
		//1) Count posts per user ID
		Map<Integer, Long> countsByUser = posts.stream()
				.collect(Collectors.groupingBy(p -> p.userId, Collectors.counting()));
		System.out.println("Counts by UserID:" + countsByUser);
		
		//2) Top 3 longest titles
		List<Post> top3LongestTitles = posts.stream()
				.sorted(Comparator.comparingInt((Post p) -> p.title.length()).reversed())
				.limit(3)
				.toList();
		System.out.println("Top 3 Longest Titles: ");
		top3LongestTitles.forEach(p -> System.out.println(" (" + p.id + ") " + p.title));
		
		//3) Filter: find posts whose body contians the word "qui"
		List<Post> containsQui = posts.stream()
				.filter(p -> p.body != null && p.body.toLowerCase().contains("qui"))
				.toList();
		System.out.println("Number of posts containing 'quit': " + containsQui.size());
		System.out.println("Posts: " + containsQui);
		
		
		//Export a CSV (id, title, userID) to project root
		StringBuilder  csv = new StringBuilder("id, title, userId\n");
		for (Post p: posts) {
			//escape quotes and commas 
			String safeTitle = p.title.replace("\"", "\"\"");
			csv.append(p.id).append(",\"").append(safeTitle).append("\", ").append(p.userId).append("\n");
		}
		Path out = Path.of("posts.csv");
		Files.writeString(out, csv.toString());
		System.out.println("Wrote CSV -->" + out.toAbsolutePath());
			
		}
		

	//the below will perform a GET and return the body as a string. Contains the headers and timeouts
	private static String httpGet(String endpoint) throws Exception {
		
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//set headers
		conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "java-http-practice/0.1");
        
        int status = conn.getResponseCode();
        boolean ok = status / 100 == 2;
        
        //create string to store response
        StringBuilder body = new StringBuilder();
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
        		ok ? conn.getInputStream() : conn.getErrorStream()
        ))) {
        	String line;
        	while ((line = reader.readLine()) != null) body.append(line);
        } finally {
        	conn.disconnect();
        }
        
        if (!ok) {
        	throw new RuntimeException("HTTP " + status + " -> " + body);
        }
        
        return body.toString();
        
	}

}
