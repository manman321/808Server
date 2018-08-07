package com.pyzy.server808.decoder

import com.pyzy.server808.message.Header
import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder

class JTT808MessageDecoder : MessageToMessageDecoder<ByteBuf>() {

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {

        val buf = Message.decoder0x7e(msg)

        val header = Header.decoder(buf)

        val map = JTTMessage.convertMap

        if (!map.containsKey(header.id)) {
            throw RuntimeException(String.format("未实现的消息  0x%02x", header.id))
        }

        val clazz = map[header.id]

        val target = clazz!!.newInstance()

        clazz.methods.filter { method -> method.name.equals("decoder") }.firstOrNull()?.invoke(target)

        out.add(target)

    }
}
