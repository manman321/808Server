package com.pyzy.server808

import java.net.InetSocketAddress
import java.net.ServerSocket

class OldIoServer{


    fun execute(){


        var serverSocket = ServerSocket();

        serverSocket.bind(InetSocketAddress(8899))


        while(true){

            var socket = serverSocket.accept()

            var inputStream = socket.getInputStream()

            var readBytes = 0;

            var readLength = 0L;

            var bytes = ByteArray(4096);

            var startTime = System.currentTimeMillis();

            while (inputStream.read(bytes).apply { readBytes = this } != -1){
                readLength += readBytes
            }

            var useTime = System.currentTimeMillis() - startTime;

            println("读取字节数:$readLength ,耗时:$useTime")

        }


    }

}

fun main(args: Array<String>) {
    OldIoServer().execute()
}