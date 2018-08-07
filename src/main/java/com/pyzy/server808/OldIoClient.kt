package com.pyzy.server808

import java.io.RandomAccessFile
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

fun main(args: Array<String>) {

    OldIoClient().execute()
}

class OldIoClient{

    constructor(){}


    fun execute(){

        var socket = Socket()

        socket.connect(InetSocketAddress("127.0.0.1",8899))

        if(socket.isConnected){

            var randomAccessFile = RandomAccessFile("/Users/hr/Downloads/PDF中文完整清晰版）.pdf","rw")

            var channel = randomAccessFile.channel

            channel.transferTo(0,channel.size(),socket.channel)

//            var buffer = ByteBuffer.allocate(4096);
//
//            val outputStream = socket.getOutputStream()
//
//            while (channel.read(buffer) != -1){
//
//                buffer.flip()
//
//                outputStream.write(buffer.array())
//
//                buffer.clear()
//
//            }

            socket.close()


        }


    }
}