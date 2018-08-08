package com.pyzy.server808.service

import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.channel.ChannelHandlerContext

class DefaultHandler : Handler<Message<JTTMessage>>{

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message<JTTMessage>) {

        println(msg.message)

    }

}