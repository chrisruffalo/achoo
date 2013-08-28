package com.achoo.input.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.achoo.broker.Dispatcher;
import com.achoo.model.Document;
import com.achoo.model.Token;
import com.achoo.store.DocumentStorer;
import com.achoo.uuid.UuidGenerator;
import com.lmax.disruptor.RingBuffer;

@Sharable
public class HttpAchooServerMessageInputHandler extends	SimpleChannelInboundHandler<FullHttpRequest> {

	private Logger logger;
	
    private Dispatcher dispatcher;
    
    private DocumentStorer storer;
    
    private UuidGenerator generator;
	
	private HttpAchooServerMessageInputHandler() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public HttpAchooServerMessageInputHandler(Dispatcher dispatcher, DocumentStorer storer) {
		this();
		
		// create and start generator
		this.generator = new UuidGenerator();
		this.generator.start();
		
		this.dispatcher = dispatcher;
		this.storer = storer;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		this.logger.trace("Opened request!");
		
		if (!request.getDecoderResult().isSuccess()) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		
		this.logger.trace("Decoded request!");
		
		// create uuid
		String uuid = this.generator.get();
		String uuidResponse = "{uuid: '" + uuid + "'}";

		// dispatch token
		RingBuffer<Token> ringBuffer = this.dispatcher.getRingBuffer();
        long sequence = ringBuffer.next();
        Token token = ringBuffer.get(sequence);
        token.setUuid(uuid);
        token.setDestinationKey("default");
        ringBuffer.publish(sequence);
        this.logger.debug("Published with uuid: '{}'", uuid);
		
		/*
		if (request.getMethod() != HttpMethod.GET) {
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		*/
		
		// get request path
		String uri = request.getUri();
		this.logger.debug("Request to: {}", uri);
		
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		
		response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, uuidResponse.length());
		response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		response.content().writeBytes(uuidResponse.getBytes());

		// handle keep-alive
		if(HttpHeaders.isKeepAlive(request)) {
			response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		
		// write back the response
        ctx.write(response);
        
        // read the request body
        ByteBuf messageContent = request.content();
        if(!HttpMethod.GET.equals(request.getMethod()) && messageContent != null) {
        	// read content
        	byte[] readContent = messageContent.array();
        	messageContent.discardReadBytes();
        	
        	// send content to storage
        	RingBuffer<Document> documentBuffer = this.storer.getRingBuffer();
        	long documentSequence = documentBuffer.next();
        	Document document = documentBuffer.get(documentSequence);
        	document.setContents(readContent);
        }
		
        // Write the end marker
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not.
        if (!HttpHeaders.isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
	}

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
    	// create error response
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
