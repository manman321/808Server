package com.pyzy.server808.message

import com.sun.org.apache.xpath.internal.operations.Bool
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.experimental.and
import kotlin.experimental.xor

class Header{

    companion object {
        fun decoder(buffer: ByteBuf):Header{

            var header = Header()

            with(header){
                id = buffer.readShort().toInt()
                property = buffer.readShort().toInt()

                for (x in 0 until 6){
                    phone += String.format("%02x",buffer.readByte())
                }

                sn = buffer.readShort().toInt()

            }

            if(header.isDivider()){

                header.packetCount = buffer.readShort().toInt()
                header.packetIndex = buffer.readShort().toInt()

            }

            return header
        }

        fun encoder(header:Header,buffer: ByteBuf){

            with(header){

                with(buffer){
                    writeShort(id)
                    writeShort(property)

                    if(phone.length == 11)phone = "0$phone"

                    for(x in 0 until 12 step 2){
                        writeByte(phone.substring(x,x + 2).bcd())
                    }

                    writeShort(sn)
                    if(isDivider()){
                        writeShort(packetCount)
                        writeShort(packetIndex)
                    }

                }
            }

        }

    }


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
    var sn:Int = 0//2字节 10

    //消息包封装项
    /**
     * 动态解析,只有当存在分包时,才解析以下两项
     * 包总数
     */
    var packetCount:Int = -1//2字节  有分包才有这个字节的数据,无分包,无内容
    var packetIndex:Int = -1//2字节


    fun isDivider():Boolean{
        //第13位为1时表示分包
        return property and 0x2000 != 0
    }

    fun isRsaEncrypt():Boolean{
        //第10位为1,rsa加密
        return property and 0x400 != 0
    }

    fun messageLength():Int{
        return property and 0x3FF
    }


    override fun toString(): String {

        return "消息id:$id  消息体属性:$property  终端手机号:$phone  消息流水号:$sn  分包后的包总数:$packetCount  包序号:$packetIndex"
    }

    fun print(){
        println(this)
    }

}

class Message{

    //Header

    //Body

    companion object {
        var Identification :Byte = 0x7e

        //只单纯的进行字符转义,不转换成为对象
        fun decoder0x7e(buffer:ByteBuf):ByteBuf{

            var buf = Unpooled.buffer(buffer.capacity())

            var readable = buffer.readableBytes() - 1

            var x = 1
            while (x < readable){

                val byte = buffer.getByte(x)

                if(byte == 0x7d.toByte()){

                    if(x + 1 < readable){

                        var temp = buffer.getByte(x + 1)
                        when(temp){
                            0x01.toByte()-> buf.writeByte(0x7d)

                            0x02.toByte()-> buf.writeByte(0x7e)

                            else->{
                                buf.writeByte(byte.toInt())
                                buf.writeByte(temp.toInt())
                            }
                        }
                        x+=2
                        continue
                    }

                }

                buf.writeByte(byte.toInt())

                ++x
            }

            return buf;

        }

        fun encoder0x7e(buffer: ByteBuf):ByteBuf{

            var buf = Unpooled.buffer((buffer.capacity() * 1.5).toInt())


            buf.writeByte(0x7e)

            for (x in 0 until buffer.readableBytes()){

                val byte = buffer.getByte(x)

                when (byte){

                    0x7e.toByte()->buf.writeBytes(listOf<Byte>(0x7d,0x02).toByteArray())

                    0x7d.toByte()->buf.writeBytes(listOf<Byte>(0x7d,0x01).toByteArray())

                    else -> buf.writeByte(byte.toInt())
                }

            }

            buf.writeByte(0x7e)

            return buf

        }

        //先校验、再转义


        fun validate(buffer:ByteBuf):Boolean{

//            var byte = buffer.getByte(0);
//            for (x in 1 until buffer.readableBytes() - 1){
//                byte  = byte.xor(buffer.getByte(x))
//            }
//
            var byte = xorCheckCode(buffer,buffer.readableBytes() - 1)

            if(byte == buffer.getByte(buffer.readableBytes() - 1)){
                return true
            }

            return false
        }

        //计算校验码
        fun xorCheckCode(buffer: ByteBuf,length:Int = -1):Byte{

            var length = if(length == -1)  buffer.readableBytes() else length

            var byte = buffer.getByte(0);
            for (x in 1 until length){
                byte  = byte.xor(buffer.getByte(x))
            }
            return byte
        }


    }

    var header : Header?;
    var body:ByteBuffer;


    constructor(){
        this.body = ByteBuffer.allocate(0);
        this.header = null;
    }

    constructor(body:ByteBuffer,header: Header){
        this.body = body
        this.header = header
    }


}


fun ByteBuf.printHexString(){
    for (x in 0 until readableBytes()){
        print(String.format("0x%02x ",getByte(x)))
    }
    println()
}

fun ByteBuf.readInt16():Int{
    return readShort().toInt()
}

fun ByteBuf.readInt8():Int{
    return readByte().toInt()
}

fun ByteBuf.readString(length:Int):String{
    var bytes = ByteBuffer.allocate(length)
    readBytes(bytes)
    return String(Charset.defaultCharset().decode(bytes).array())
}

fun ByteBuf.readLastString():String{
    var length = readableBytes() - readerIndex()
    return readString(length)
}

fun ByteBuf.writeString(value:String){

    var buffer = Charset.defaultCharset().encode(value)

    writeBytes(buffer)

}


fun List<Byte>.printHexString(){
    for (x in 0 until size){
        print(String.format("0x%02x ",get(x)))
    }
    println()
}



fun String.bcd():Int{
    var result = 0
    var reverse = reversed()

    for(x in 0 until length){
//        println(" x : ${reverse[x]}  parse:  ${reverse[x].toString().toInt()}" )
        result += (reverse[x].toString().toInt() shl (4 * x))//只适用于2位字符串转bcd  更多为的请使用移位操作来完成
    }
    return result
}


fun testMessage(){
    var data = listOf<Byte>(0x30,0x7e,0x08,0x7d,0x3b)

    val validate = Message.validate(Unpooled.copiedBuffer(data.toByteArray()))

    println(validate)

    data.printHexString()

    println("=============")

    var buffer = Unpooled.copiedBuffer(data.toByteArray())

    var encoder = Message.encoder0x7e(buffer)

    encoder.printHexString()


    println("\n-------------------------")

    var uploadData = listOf<Byte>(0x7e,0x30,0x7d,0x02,0x08,0x7d,0x01,0x55,0x7e)

    var decoder = Message.decoder0x7e(Unpooled.copiedBuffer(uploadData.toByteArray()))

    decoder.printHexString()


}


fun testHeader(){

    //待处理 添加校验位、去除校验位   应该在Message中进行处理的


    val data = listOf<Byte>(
            0x00,0x01,//id
            0x00,0x01,//property
            0x01,0x53,0x51,0x23,0x50,0x44,//phone
            0x00,0x05
            )

    data.printHexString()



    val header = Header.decoder(Unpooled.copiedBuffer(data.toByteArray()))

    header.print()

    var buffer = Unpooled.buffer(100);



    Header.encoder(header,buffer)

    buffer.printHexString()




}


fun main(args: Array<String>) {

    testHeader()



}