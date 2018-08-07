package com.pyzy.server808.service

import com.pyzy.server808.message.JTTMessage
import io.netty.channel.ChannelHandlerContext

class DefaultHandler : Handler<JTTMessage>{

    override fun channelRead0(ctx: ChannelHandlerContext, msg: JTTMessage) {

        println(msg)

    }

}