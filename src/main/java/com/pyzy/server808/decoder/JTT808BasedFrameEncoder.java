package com.pyzy.server808.decoder;

import com.pyzy.server808.message.JTTMessage;
import com.pyzy.server808.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JTT808BasedFrameEncoder extends MessageToByteEncoder<JTTMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, JTTMessage msg, ByteBuf out) throws Exception {
        ByteBuf buf = msg.encoder();
        out.writeBytes("7e".getBytes());
        Byte bytes = Message.Companion.xorCheckCode(buf,buf.readableBytes());
        out.writeBytes(buf);
        out.writeByte(bytes);
        out.writeBytes("7e".getBytes());
    }
}
