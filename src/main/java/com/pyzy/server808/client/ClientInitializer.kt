package com.pyzy.server808.client

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil

class ClientInitializer : ChannelInitializer<SocketChannel>(){
    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {


        var pipeline = ch.pipeline()

//        pipeline.addLast("idle",IdleStateHandler(3,6,9))

//        pipeline.addLast("delimiter",DelimiterBasedFrameDecoder(4096, *Delimiters.lineDelimiter()))
//
//        pipeline.addLast(StringDecoder(CharsetUtil.UTF_8))
//
//        pipeline.addLast(StringEncoder(CharsetUtil.UTF_8))

        pipeline.addLast("delimiter",DelimiterBasedFrameDecoder(4096,*Delimiters.lineDelimiter()))

        pipeline.addLast("decoder",StringDecoder(CharsetUtil.UTF_8))

        pipeline.addLast("encoder",StringEncoder(CharsetUtil.UTF_8))

        pipeline.addLast("chatHandler",ClientTextHandler())


    }


}