package com.pyzy.server808.ext

import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

fun ByteBuf.printHexString(){
    for (x in 0 until readableBytes()){
        print(kotlin.String.format("0x%02x,",getByte(x)))
    }
    println()
}

fun ByteBuf.readInt16():Int{
    return readShort().toInt()
}

fun ByteBuf.readInt8():Int{
    return readByte().toInt()
}

fun ByteBuf.readBuffer(length:Int):ByteBuffer{
    var bytes = ByteBuffer.allocate(length)
    readBytes(bytes)
    bytes.flip()
    return bytes
}

fun ByteBuf.readAsciiString(length: Int):String{
    return readBuffer(length).asciiString()
}

fun ByteBuf.readString(length:Int):String{
    return readBuffer(length).string()
}

fun ByteBuf.readLastString():String{
    return readString(readableBytes())
}


fun ByteBuf.writeString(value:String){
    var buffer = Charset.forName("GBK").encode(value)
    writeBytes(buffer)

}


