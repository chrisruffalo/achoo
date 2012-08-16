package com.em.achoo.senders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.model.Message;
import com.em.achoo.model.interfaces.ISubscription;
import com.em.achoo.model.subscription.HttpSubscription;

public class HttpSender extends AbstractSender {

	private Logger logger = LoggerFactory.getLogger(HttpSender.class);			
	
	@Override
	public void send(Message message) {
		
		ISubscription subscription = this.getSubscription();
		
		if(!(subscription instanceof HttpSubscription)) {
			//this is really bad, just return
			return;
		}
		HttpSubscription httpSubscription = (HttpSubscription)subscription;
		
		//create uri stuff
		String path = httpSubscription.getPath();
		String host = httpSubscription.getHost();
		int port = httpSubscription.getPort();

		//build uri
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http")
			   .setHost(host)
		       .setPort(port)
		       .setPath(path);
		//create list of name value pairs
		for(String key : message.getParameters().keySet()) {
			Object value = message.getParameters().get(key);
			if(value == null) {
				value = "";
			}
			builder.setParameter(key, value.toString());
		}
		
		//get url string
		URI url = null;
		try {
			url = builder.build();
		} catch (URISyntaxException e1) {
			this.logger.info("Could not create a URL for sender: {}", e1.getMessage());
			return;
		}
		
		//create http client
		HttpClient client = new DefaultHttpClient();
		HttpRequestBase request = null;
		switch(httpSubscription.getMethod()) {
			case POST:
				HttpPost post = new HttpPost(url);

				Object body = message.getBody();
				if(body != null) {
					byte[] bodyBinary = new byte[0];
					if(body instanceof String) {
						bodyBinary = ((String) body).getBytes();
					} else if(body instanceof byte[]) {
						bodyBinary = (byte[]) body;
					} else {
						bodyBinary = body.toString().getBytes();
					}
					//create input stream for entity to read
					ByteArrayInputStream inputStream = new ByteArrayInputStream(bodyBinary);
					BasicHttpEntity entity = new BasicHttpEntity();
					entity.setContent(inputStream);
					post.setEntity(entity);									}
												
				request = post;
				break;
			case PUT:
				request = new HttpPut(url);
				break;
			case GET:
				request = new HttpGet(url);
				break;
		}
		
		try {
			//execute request
			HttpResponse response = client.execute(request);
			
			//log
			this.logger.info("Got status: {} for message sent to {}", response.getStatusLine().getStatusCode(), url);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
