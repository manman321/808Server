package com.pyzy.server808.service

import com.pyzy.server808.message.JTT0x0100
import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.channel.ChannelHandlerContext

interface Handler<in Message>{

    @Throws(Exception::class)
    fun channelRead0(ctx: ChannelHandlerContext, msg: Message){
        //通用回复

        msg as com.pyzy.server808.message.Message<JTTMessage>

        var response = Message(JTT0x0100(),msg.header)

        ctx.write(response)

    }


}