package com.pyzy.server808.message

import com.pyzy.server808.ext.*
import com.pyzy.server808.utils.ClassHelper
import com.sun.xml.internal.xsom.XSWildcard
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import jdk.nashorn.internal.ir.WhileNode

open class JTTMessage{


//    var msgId:Int = 0

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

    open fun messageId():Int{
        return 0;
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
            ackSn = readInt16()
            sn = readInt16()
            result = readInt8()
        }
    }

    override fun messageId(): Int{
        return 0x0001
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

    override fun messageId(): Int {
        return 0x8001
    }

}


//终端心跳
class JTT0x0002:JTTMessage(){
    override fun messageId(): Int {
        return 0x0002
    }
}


enum class PlateColor private constructor(val value: Int, val colorName: String) {

    BLUE(1, "蓝色"),
    YELLOW(2, "黄色"),
    BLACK(3,"黑色"),
    WHITE(4,"白色"),
    OTHER(9,"其他");


    override fun toString(): String {
        return colorName
    }

    companion object {
        fun color(color:Int):PlateColor{
            return when(color){
                1 -> BLUE
                2 -> YELLOW
                3 -> BLACK
                4 -> WHITE
                9 -> OTHER
                else -> OTHER
            }
        }
    }

}


//终端注册
class JTT0x0100:JTTMessage(){

    var provinceId:Int = 0

    var cityId:Int = 0

    var manufacturerId:String = ""//5字节

    var terminalModelNo:String = ""//20字节

    var terminalId:String = "" //7字节

    var plateColor:PlateColor = PlateColor.BLUE//1字节

    var plateNo: String = ""//不固定


    override fun decoder(buffer: ByteBuf) {
        with(buffer){

            provinceId = readInt16()

            cityId = readInt16()

            manufacturerId = readAsciiString(5)

            terminalModelNo = readString(20)
            terminalId = readString(7)
            plateColor = PlateColor.color(readInt8())
            plateNo = readLastString()
        }
    }



    override fun toString(): String {
        return "省:$provinceId ,市:$cityId ,终端制造商Id: $manufacturerId ,终端型号:$terminalModelNo , 终端id:$terminalId,车牌颜色:$plateColor,车牌号码:$plateNo"
    }
}

//终端注册应答
class JTT0x8100:JTTMessage(){
    var sn :Int = 0
    var result:Int = 0
    var token:String = ""

    override fun encoder(): ByteBuf {

        var bytes = token.ascii();
        var buffer = buffer(3 + bytes.limit())
        buffer.writeShort(sn)
        buffer.writeByte(result)

        if(result == 0){
            buffer.writeBytes(bytes)
        }
        return buffer
    }

    override fun messageId(): Int {
        return 0x8100
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


















