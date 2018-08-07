package com.pyzy.server808.first

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.ChannelMatcher
import io.netty.channel.group.ChannelMatchers
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

class MyChatServerMessageHandler : SimpleChannelInboundHandler<String>(){

    companion object {
        var channelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    }



    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {
        val channel = ctx.channel()

        println("接受到客户端发来消息: $msg")

        channelGroup.write("${channel.remoteAddress()}$msg\r\n", ChannelMatchers.isNot(channel))

        channelGroup.write("我:$msg \r\n",ChannelMatchers.`is`(channel))

        channelGroup.flush()

    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

        cause.printStackTrace()
        ctx.close()
    }


    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        var channel = ctx.channel()

        channelGroup.writeAndFlush("【服务器消息】 ${channel.remoteAddress()} offline \r\n")

    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {

        var channel = ctx.channel()

        channelGroup.writeAndFlush("【服务器消息】${channel.remoteAddress()} online \r\n")

        channelGroup.add(channel)

    }

}