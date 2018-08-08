package com.pyzy.server808

import com.pyzy.server808.server.ServerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.net.ServerSocket


fun main(args: Array<String>) {


    val serverSocket = ServerSocket()

    serverSocket.reuseAddress = true

    MyChatServer().execute()
}

class MyChatServer{


    fun execute(){


        var parentGroup = NioEventLoopGroup()

        var childGroup = NioEventLoopGroup()

        try {

            var serverBootstrap  = ServerBootstrap()

            serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .childHandler(ServerInitializer())


            serverBootstrap.option(ChannelOption.SO_REUSEADDR,true)

            serverBootstrap.childOption(ChannelOption.SO_REUSEADDR,true)

            var channelFuture = serverBootstrap.bind(8899).sync()

            channelFuture.channel().closeFuture().sync()


        }finally {
            parentGroup.shutdownGracefully()
            childGroup.shutdownGracefully()
        }

    }

}