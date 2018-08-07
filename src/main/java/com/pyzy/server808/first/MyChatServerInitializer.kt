package com.pyzy.server808.first

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.CharsetUtil

class MyChatServerInitializer : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {

        var pipeline = ch.pipeline()

        pipeline.addLast("idleStateHandler",IdleStateHandler(0,0,60))
        pipeline.addLast("handler",MyChatServerTimeoutHandler())


        pipeline.addLast("delimiter",DelimiterBasedFrameDecoder(4096,*Delimiters.lineDelimiter()))

        ByteArrayDecoder()

        pipeline.addLast("decoder",StringDecoder(CharsetUtil.UTF_8))

        pipeline.addLast("encoder",StringEncoder(CharsetUtil.UTF_8))

        pipeline.addLast("textHandler",MyChatServerTextHandler())



    }

}