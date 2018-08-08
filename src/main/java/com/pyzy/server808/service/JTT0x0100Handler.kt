package com.pyzy.server808.service

import com.pyzy.server808.message.JTT0x0100
import com.pyzy.server808.message.JTT0x8100
import com.pyzy.server808.message.Message
import io.netty.channel.ChannelHandlerContext

class JTT0x0100Handler : Handler<Message<JTT0x0100>>{
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message<JTT0x0100>) {


        println(msg.message)

        val response = JTT0x8100()
        with(response){
            token = "0123456789"
            sn = msg.header.sn
            result = 0
        }

        var result = Message(response,msg.header!!)
        ctx.write(result)

//        super.channelRead0(ctx, msg)
    }

}