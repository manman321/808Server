package com.pyzy.server808.server

import com.pyzy.server808.decoder.JTT808BasedFrameDecoder
import com.pyzy.server808.decoder.JTT808BasedFrameEncoder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler

class ServerInitializer : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {

        var pipeline = ch.pipeline()

        //空闲检测,Netty提供的
        pipeline.addLast("idleStateHandler",IdleStateHandler(0,0,60))

        //自定义空闲处理器,链接超时处理器  待完善
        pipeline.addLast("handler", ServerTimeoutHandler())

        //TCP分包处理器,  拆包  并去除首尾标识 发送给下一个处理器
        //消息解析器,将从TCP拆解出来的包进行解析然后验证，最后组装成JTTMessage对象
        //如果解析的包无误,那么同时在这个地方返回服务端通用应答  ACK
        //可以将两个 decoder合成为一个Decoder   ,先试试两个能不能够行得通
        pipeline.addLast("decoder",JTT808BasedFrameDecoder())

        //消息编码器,将消息编码成字节码传送给客户端
        pipeline.addLast("encoder",JTT808BasedFrameEncoder())


//        pipeline.addLast("messageDecoder",JTT808MessageDecoder())


        //消息处理器
        pipeline.addLast("messageHandler", ServerMessageHandler())



    }

}