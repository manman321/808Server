package com.pyzy.server808

import com.pyzy.server808.client.ClientInitializer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.*

fun main(args: Array<String>) {
    MyChatClient().execute()
}

class MyChatClient{

    @Throws(Exception::class)
    fun execute(){


        val eventGroup = NioEventLoopGroup()

        var uid = UUID.randomUUID().toString()

        try {

            val bootstrap = Bootstrap()

            bootstrap.group(eventGroup).channel(NioSocketChannel::class.java)
                    .handler(ClientInitializer())

            var channel : Channel? = null

            while (true){
                var line = readLine()

                if (channel == null || !channel.isOpen || !channel.isWritable || !channel.isRegistered || !channel.isActive){

                    channel = connectServer(bootstrap)

                    while (true){
                        if(channel != null && channel.isOpen && channel.isActive && channel.isRegistered && channel.isWritable){
                            break
                        }
                    }
                    //user login


                    channel!!.writeAndFlush("【客户端登录】 UID:$uid\r\n")

                }

                channel!!

//                println("channel open: ${channel.isOpen} , active: ${channel.isActive} register:${channel.isRegistered} writable: ${channel.isWritable}")

                channel.writeAndFlush(line + "\r\n")
            }

        }finally {
            eventGroup.shutdownGracefully()
        }




    }

    private fun connectServer(bootstrap: Bootstrap):Channel{
       return bootstrap.connect("localhost",8899).channel()
    }

}