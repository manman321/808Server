package com.pyzy.server808.decoder

import com.pyzy.server808.ext.printHexString
import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class JTT808BasedFrameEncoder : MessageToByteEncoder<Message<JTTMessage>>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, msg: Message<JTTMessage>, out: ByteBuf) {

        val bytes = msg.encoder()
        println("终端注册响应: ");bytes.printHexString()
        out.writeBytes(bytes)
    }
}
