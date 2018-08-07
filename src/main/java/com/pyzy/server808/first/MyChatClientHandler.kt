package com.pyzy.server808.first

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent

class MyChatClientHandler : SimpleChannelInboundHandler<String>(){

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {

        println("receive message from server: $msg")

    }

}