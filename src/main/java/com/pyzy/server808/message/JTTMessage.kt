package com.pyzy.server808.message

import com.pyzy.server808.ext.readInt16
import com.pyzy.server808.ext.readInt8
import com.pyzy.server808.ext.readLastString
import com.pyzy.server808.ext.readString
import com.pyzy.server808.utils.ClassHelper
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

open class JTTMessage{


    var msgId:Int = 0

    companion object {
        var convertMap:Map<Int,Class<*>>

        init {

            var children = ClassHelper.getAllChildren(JTTMessage::class.java)

            convertMap = children.map { child-> toInt(child.simpleName) to child }.toMap()

        }

        //类名转换成对应的数字，只
        private fun toInt(clazzName:String):Int{

            var index = clazzName.indexOf("0x",0,true)

            if(index == -1)throw RuntimeException("不可转换的类型错误 Message.toInt")

            var number = clazzName.substring(index + 2)

            return Integer.valueOf(number, 16)
        }
    }

    open fun decoder(buffer:ByteBuf){
    }

    open fun encoder():ByteBuf{
        return buffer(0)
    }

    fun buffer(capacity:Int):ByteBuf{
        return Unpooled.buffer(capacity)
    }

}

//终端通用应答
class JTT0x0001 : JTTMessage(){

    companion object {
        val RESULT_SUCCESS = 0
        val RESULT_FAILURE = 1
        val RESULT_MESSAGE_ERROR = 2
        val RESULT_NOT_SUPPORT = 3
    }

    var ackSn:Int = 0
    var sn:Int = 0
    var result = 0

    override fun decoder(buffer: ByteBuf){
        with(buffer){
            msgId = 0x0001
            ackSn = readInt16()
            sn = readInt16()
            result = readInt8()
        }
    }

}


//平台通用应答
class JTT0x8001 : JTTMessage(){

    var ackSn = 0
    var sn = 0
    var result = 0


    override fun encoder(): ByteBuf {
        var buffer = buffer(0)
        with(buffer){
            writeShort(ackSn)
            writeShort(sn)
            writeByte(result)
        }

        return buffer
    }

}


//终端心跳
class JTT0x0002:JTTMessage()

//终端注册
class JTT0x0100:JTTMessage(){

    var province:Int = 0

    var city:Int = 0

    var manufacturer:String = ""//5字节

    var mode:String = ""//20字节

    var terminalId:String = "" //7字节

    var color:Int = 0//1字节

    var vin : String = ""//不固定


    override fun decoder(buffer: ByteBuf) {
        with(buffer){

            province = readInt16()
            city = readInt16()
            manufacturer = readString(5)
            mode = readString(20)
            terminalId = readString(7)
            color = readInt8()
            vin = readLastString()
        }
    }





}

//终端注册应答
class JTT0x8100:JTTMessage(){
    var sn :Int = 0
    var result:Int = 0

    override fun encoder(): ByteBuf {

        var buffer = buffer(3)

        buffer.writeShort(sn)
        buffer.writeByte(result)

        return buffer
    }
}


//终端注销
class JTT0x0003:JTTMessage()

//终端鉴权
class JTT0x0102:JTTMessage(){
    var authCode:String = ""

    override fun decoder(buffer: ByteBuf) {
        authCode = buffer.readLastString()
    }

}


















