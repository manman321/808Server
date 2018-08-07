package com.pyzy.server808.service

import com.pyzy.server808.message.JTT0x8001
import io.netty.channel.ChannelHandlerContext

class PersonHandler : Handler<JTT0x8001>{

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: JTT0x8001) {

        println("Reflect invoke => $msg")

    }

}