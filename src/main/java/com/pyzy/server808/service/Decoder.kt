package com.pyzy.server808.service

import io.netty.channel.ChannelHandlerContext

interface Decoder<T : Any>{

    @Throws(Exception::class)
    fun channelRead0(ctx: ChannelHandlerContext, msg: T)


}