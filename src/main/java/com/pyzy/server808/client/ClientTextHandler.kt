package com.pyzy.server808.client

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ClientTextHandler : SimpleChannelInboundHandler<String>(){


    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {
        val channel = ctx.channel()

        println("接受到服务端发来消息: $msg")


        if(msg.contains("网络异常断开")){
            ctx.channel().close()
        }


    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

        cause.printStackTrace()
        ctx.close()
    }



}