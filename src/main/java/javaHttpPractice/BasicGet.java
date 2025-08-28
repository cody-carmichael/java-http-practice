package javaHttpPractice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/* my goal here is to perform a simple GET request and print the raw JSON to the console. */

public class BasicGet {

	public static void main(String[] args) {
		//1) prepare an endpoint URL object.
		String endpoint = "https://jsonplaceholder.typicode.com/posts/1";
		
		try {
			URL url = new URL(endpoint); 
			
			// 2) open a connection. This gives a HttpURLConnection for HTTP Methods
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			//3) configure the request
			conn.setRequestMethod("GET");							//want to do a get
			conn.setConnectTimeout(5000);							//decent timeout
			conn.setReadTimeout(5000);								//decent timeout
			conn.setRequestProperty("Accept", "application/json");	//what data we want
			
			
			
			//4) send the request and check the http status code
			int status = conn.getResponseCode();
			//not a 200 status
			if(status / 100 !=2) {
				try (BufferedReader err = new BufferedReader(
						new InputStreamReader(conn.getErrorStream()))) {
					String line;
					StringBuilder errorBody = new StringBuilder();
					while ((line = err.readLine()) != null) errorBody.append(line);
				}			
			}
			
			//5) Transform the response body into a string
			StringBuilder body = new StringBuilder();
			try(BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) body.append(line);
			}
			
			//6) disconnect when done
			conn.disconnect();
			
			//7)print response to console
			System.out.println(body.toString());
			
			
		} catch (Exception e) {
			//if something fails i want to print the stack trace so I can see where it failed
			e.printStackTrace();
		}
	}
	
	
}
