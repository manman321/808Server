package com.pyzy.server808.server

import com.pyzy.server808.ext.classExists
import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import com.pyzy.server808.service.DefaultHandler
import com.pyzy.server808.service.Handler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.ChannelMatchers
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

class ServerMessageHandler : SimpleChannelInboundHandler<Message<JTTMessage>>(){

    companion object {
        var channelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    }



    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, wrapper: Message<JTTMessage>) {
        val channel = ctx.channel()

        val msg = wrapper.message!!


        val handlerName = "com.pyzy.server808.service.${msg.javaClass.simpleName}Handler"

        try {

            if(handlerName.classExists()){

                val clazz = Class.forName(handlerName)

                val handler = clazz!!.newInstance() as Handler<Message<JTTMessage>>

                handler.channelRead0(ctx,wrapper)
            }else{
                DefaultHandler().channelRead0(ctx, wrapper)
            }

        }catch (e:Exception){

            DefaultHandler().channelRead0(ctx, wrapper)

            e.printStackTrace()
        }
//         msg.javaClass.simpleName

//        println("接受到客户端发来消息: $msg")
//
//        channelGroup.write("${channel.remoteAddress()}$msg\r\n", ChannelMatchers.isNot(channel))
//
//        channelGroup.write("我:$msg \r\n",ChannelMatchers.`is`(channel))
//
//        channelGroup.flush()

    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

        cause.printStackTrace()
        ctx.close()
    }


    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        var channel = ctx.channel()

//        channelGroup.writeAndFlush("【服务器消息】 ${channel.remoteAddress()} offline \r\n")

    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        println("客户端已连接上服务器")

//        var channel = ctx.channel()
//
//        channelGroup.writeAndFlush("【服务器消息】${channel.remoteAddress()} online \r\n")
//
//        channelGroup.add(channel)

    }

}