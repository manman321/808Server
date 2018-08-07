package com.pyzy.server808.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

class ServerTimeoutHandler : ChannelInboundHandlerAdapter() {

    companion object {
        var timeout = 10;
        var baseTime = System.currentTimeMillis()
    }

    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        super.userEventTriggered(ctx, evt)

        if(evt is IdleStateEvent){

            val id = ctx.channel().id();

            when(evt.state()){

                IdleState.ALL_IDLE->{
                    println("All   [${ctx.channel().id()}] -> ${(System.currentTimeMillis() - baseTime)/ 1000}")

                    val channel = ctx.channel()

                    channel.writeAndFlush("【服务器通知】 请重新连接服务器,网络异常断开 \r\n")

                    channel.close()

                    ServerTextHandler.channelGroup.remove(channel)
//                    println("服务器空闲 通道:$id")
                }

                IdleState.WRITER_IDLE->{
                    println("WRITE [${ctx.channel().id()}] -> ${(System.currentTimeMillis() - baseTime)/1000}")

//                    println("服务器写入空闲 通道:$id")
                }

                IdleState.READER_IDLE->{
                    println("READER[${ctx.channel().id()}] -> ${(System.currentTimeMillis() - baseTime)/1000}")

//                    println("服务器读取空闲 通道:$id")
                }
            }
        }

    }

}