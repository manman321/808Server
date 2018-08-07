package com.pyzy.server808.handler

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

public class MessageDecoderHandler : SimpleChannelInboundHandler<ByteBuf>(){

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {




    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        cause.printStackTrace()
    }


}