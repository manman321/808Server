package com.pyzy.server808.message

import com.pyzy.server808.ext.bcd
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * 使用方法和Message相同,主要是提供给Message使用的
 *
 * 对外暴露了静态的decoder方法，返回Header对象
 * 暴露encoder方法,将header编码成字节数组
 */
class Header{

    var id:Int = 0//2byte  0
    var property:Int = 0//2byte 2
    // 16位  按位处理

    /***
     *  0   1   2   3   4   5   6   7   8   9   | 10   11   12|  13  |14  15
     *  ---------------消息体长度-------------    |--数据加密方式-|-分包-|-保留-|
     *
     *  数据加密: bit 10 - 12
     *  不加密：     000
     *  RSA加密:    100
     *
     */
    var phone:String = ""//6byte    4
    var sn:Int = 0//2字节 10 无符号

    //消息包封装项
    /**
     * 动态解析,只有当存在分包时,才解析以下两项
     * 包总数
     */
    var packetCount:Int = -1//2字节  有分包才有这个字节的数据,无分包,无内容
    var packetIndex:Int = -1//2字节

    var divider:Boolean
        set(value) {
            divider = value
            property = if(value){
                property or 0x2000
            }else{
                property and 0x2000.inv()
            }
        }
        get() {
            return divider || property and 0x2000 != 0
        }

    fun isRsaEncrypt():Boolean{
        //第10位为1,rsa加密
        return property and 0x400 != 0
    }




    var messageLength:Int
        set(value) {
            messageLength = value
            property = property or value or if(divider) 0x2000 else 0
        }
        get(){
            return property and 0x3FF
        }


    companion object {
        fun decoder(buffer: ByteBuf):Header{

            var header = Header()

            with(header){
                id = buffer.readShort().toInt()
                property = buffer.readShort().toInt()

                for (x in 0 until 6){
                    phone += String.format("%02x",buffer.readByte())
                }

                sn = buffer.readUnsignedShort()

            }

            if(header.divider){

                header.packetCount = buffer.readShort().toInt()
                header.packetIndex = buffer.readShort().toInt()

            }

            return header
        }
    }

    fun encoder(): ByteBuf {

        var buffer = Unpooled.buffer(50);

        with(buffer){
            writeShort(id)
            writeShort(property)

            if(phone.length == 11)phone = "0$phone"

            for(x in 0 until 12 step 2){
                writeByte(phone.substring(x,x + 2).bcd())
            }

            writeShort(sn)
            if(divider){
                writeShort(packetCount)
                writeShort(packetIndex)
            }

        }
        return buffer
    }


    override fun toString(): String {

        return "消息id:$id  消息长度:${messageLength}  消息是否加密:${isRsaEncrypt()}   终端手机号:$phone  消息流水号:$sn  消息是否被分包:${divider} 分包后的包总数:$packetCount  包序号:$packetIndex"
    }

    fun print(){
        println(this)
    }

}
