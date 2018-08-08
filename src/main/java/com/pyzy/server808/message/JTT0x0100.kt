package com.pyzy.server808.message

import com.pyzy.server808.ext.*
import io.netty.buffer.ByteBuf

/***
 * 注册、注册应答、注销、鉴权、心跳   这几个相关的放在一个文件中
 *
 */


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

//终端注销应答
class JTT0x8003:JTTMessage()

//终端鉴权
class JTT0x0102:JTTMessage(){
    var authCode:String = ""

    override fun decoder(buffer: ByteBuf) {
        authCode = buffer.readLastString()
    }
}