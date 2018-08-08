package com.pyzy.server808.service

import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.channel.ChannelHandlerContext

interface Handler<in Message>{

    @Throws(Exception::class)
    fun channelRead0(ctx: ChannelHandlerContext, msg: Message)


}