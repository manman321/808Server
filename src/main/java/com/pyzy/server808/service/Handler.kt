package com.pyzy.server808.service

import com.pyzy.server808.message.JTTMessage
import io.netty.channel.ChannelHandlerContext

interface Handler<T : JTTMessage>{

    @Throws(Exception::class)
    fun channelRead0(ctx: ChannelHandlerContext, msg: T)


}