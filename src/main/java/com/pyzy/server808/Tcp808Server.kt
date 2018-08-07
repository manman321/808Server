package com.pyzy.server808

import com.pyzy.server808.server.ServerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler


fun main(args: Array<String>) {
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

            var channelFuture = serverBootstrap.bind(8899).sync()

            channelFuture.channel().closeFuture().sync()


        }finally {
            parentGroup.shutdownGracefully()
            childGroup.shutdownGracefully()
        }

    }

}