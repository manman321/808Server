package com.pyzy.server808.first

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.ChannelMatcher
import io.netty.channel.group.ChannelMatchers
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

class MyChatClientTextHandler : SimpleChannelInboundHandler<String>(){


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