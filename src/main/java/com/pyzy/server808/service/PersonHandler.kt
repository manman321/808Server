package com.pyzy.server808.service

import com.alibaba.fastjson.JSON
import com.pyzy.server808.model.Person
import io.netty.channel.ChannelHandlerContext

class PersonHandler : Decoder<Person>{

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Person) {

        println("Reflect invoke => $msg")

    }

}