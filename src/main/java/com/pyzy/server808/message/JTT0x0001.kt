package com.pyzy.server808.message

import com.pyzy.server808.ext.readInt16
import com.pyzy.server808.ext.readInt8
import io.netty.buffer.ByteBuf

/**
 * 终端应答、平台应答  这两个相关的消息放在一起
 *
 */

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
