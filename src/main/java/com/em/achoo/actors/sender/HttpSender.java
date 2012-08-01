package com.em.achoo.actors.sender;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.model.HttpSubscription;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;

public class HttpSender extends AbstractSender {

	private Logger logger = LoggerFactory.getLogger(HttpSender.class);			
	
	public HttpSender(Subscription subscription) {
		super(subscription);
	}

	@Override
	public void send(Message message) {
		
		this.logger.info("Sent message through sender at: {}", this.self().path().toString());
		
		Subscription subscription = this.getSubscription();
		
		if(!(subscription instanceof HttpSubscription)) {
			//this is really bad, just return
			return;
		}
		HttpSubscription httpSubscription = (HttpSubscription)subscription;
		
		//create executor
		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
		
		//create parameters from message
		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);			

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
        });
		
		//create connection context and information
        HttpContext context = new BasicHttpContext(null);

        HttpHost host = new HttpHost(httpSubscription.getHost(), httpSubscription.getPort());

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);			
					
		//create put or post
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(httpSubscription.getMethod().toString(), httpSubscription.getPath());
		
		//attach parameters to request
		request.setParams(params);
		
		//attach entity (content) to request body from message
		//todo!
		
		//update executor
		try {
			//create new connection if the connection is not already open
			if(!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}
			
			//pre-process execution
			httpexecutor.preProcess(request, httpproc, context);
			
			//execute connection (don't care about response, not our concern)
			HttpResponse response = httpexecutor.execute(request, conn, context);
			
			//look at response
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);
            
            //no 2XX code is bad!
            if(2 != (response.getStatusLine().getStatusCode() / 100)) {
            	//todo: log error
            }
            
            //close connection
            if (!connStrategy.keepAlive(response, context)) {
                conn.close();
            } else {
                System.out.println("Connection kept alive...");
            }				
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
