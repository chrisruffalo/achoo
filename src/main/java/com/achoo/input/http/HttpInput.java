package com.achoo.input.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.achoo.broker.Dispatcher;
import com.achoo.store.DocumentStorer;

public class HttpInput {

	private HttpAchooServerInitializer serverInitializer;
	
	private Logger logger;
	
	public HttpInput(Dispatcher dispatcher, DocumentStorer storer) {
		this.serverInitializer = new HttpAchooServerInitializer(dispatcher, storer);
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public void create(int port) {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(this.serverInitializer);

            Channel ch = b.bind(port).sync().channel();
            
            // log
            this.logger.info("Starting HTTP input on port: {}", port);
            
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}

}
