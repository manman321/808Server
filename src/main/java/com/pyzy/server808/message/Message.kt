package com.pyzy.server808.message

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.concurrent.atomic.AtomicLong
import kotlin.experimental.xor
import kotlin.math.ceil


/**
 * 1.需要从字节数组中将数据转义成对象时,使用Message.decoder 静态方法获取转义后的对象
 * 2.将对象编码成字节数组,设置好header和message后,调用encoder得到编码后的字节数组
 */
class Message<T : JTTMessage>{

    //Header

    //Body

    companion object {

        var MAXLENGTH = 128

        //序列号生成器
        private val serialNo:AtomicLong = AtomicLong(0);

            //只单纯的进行字符转义,不转换成为对象
        /**
         * 将数组中的数据进行反转义
         * 解析出来的数据包含验证码
         */
        private fun decoder0x7e(buffer:ByteBuf):ByteBuf{

            var buf = Unpooled.buffer(buffer.capacity())

            var readable = buffer.readableBytes()

            var x = 0
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

        /**
         * @param buffer 传入带校验的完整消息
         * @return buf 编码后的数据,在首尾直接加上0x7e 即可通过网络流写出去了
         */
        private fun encoder0x7e(buffer: ByteBuf):ByteBuf{

            var buf = Unpooled.buffer((buffer.capacity() * 1.5).toInt())

            for (x in 0 until buffer.readableBytes()){

                val byte = buffer.getByte(x)

                when (byte){

                    0x7e.toByte()->buf.writeBytes(listOf<Byte>(0x7d,0x02).toByteArray())

                    0x7d.toByte()->buf.writeBytes(listOf<Byte>(0x7d,0x01).toByteArray())

                    else -> buf.writeByte(byte.toInt())
                }

            }
            return buf

        }

        //先校验、再转义


        /**
         * 校验数组中的前n为的xor值,与最后一位是否相等
         */
        private fun validate(buffer:ByteBuf):Boolean{

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

        /**
         * 计算校验码
         */
        private fun xorCheckCode(buffer: ByteBuf,length:Int = -1):Byte{

            var length = if(length == -1)  buffer.readableBytes() else length

            var byte = buffer.getByte(0);
            for (x in 1 until length){
                byte  = byte.xor(buffer.getByte(x))
            }
            return byte
        }


        /**
         *  完整消息参数,为进行解码
         *  1.解码消息
         *  2.验证消息完整性
         *  3.解析头信息
         *  4.解析消息
         */
        fun decoder(msg:ByteBuf):Message<JTTMessage>?{

            val buf = decoder0x7e(msg)

            if(!validate(buf))return null

            val buffer = Unpooled.buffer(msg.readableBytes() - 1)

            msg.readBytes(buffer)

            val header = Header.decoder(buffer)

            val map = JTTMessage.convertMap

            if (!map.containsKey(header.id)) {
                throw RuntimeException(String.format("未实现的消息  0x%02x", header.id))
            }

            val clazz = map[header.id]

            val target = clazz!!.newInstance()

            clazz.methods.filter { method -> method.name.equals("decoder") }.firstOrNull()?.invoke(target,buffer)

            return Message(target as JTTMessage,header)

        }


    }

    var header : Header;
    var message:JTTMessage;

    constructor(){
        this.header = Header();
        this.message = JTTMessage();
    }

    constructor(message:T,header: Header){
        this.message = message
        this.header = header
    }

    /**
     * 将数据消息封装在Message中,
     * 然后通过encode将其编码成字节码
     *
     * 使用前需要先设置好message和header信息
     *
     * 1.计算出message消息编码后的字节数组
     * 2.计算消息将分成几次发送
     * 3.修改header信息(这里的header来源,一般都是从终端上传数据中直接拷贝过来的  平台主动发送数据除外)
     *  主要是对property字段进行修改,以及是否需要分包的数据进行修改
     * 4.读取本次需要发送的数据
     * 5.将消息头和读取到需要发送的数据存入新的数组中,计算校验码
     * 6.编码转义
     * 7.添加首尾标识字符
     *
     * 8.空消息处理
     */
    fun encoder():ByteBuf{


        //长度超过限制之后,在这里需要对消息内容进行分包处理,然后返回bytebuf,让程序一次性发送

        var content = message.encoder()

        var buffer = Unpooled.buffer(4096);

        var times = ceil(content.readableBytes() / MAXLENGTH * 1.0).toInt()

        //待测试


        if(times == 0)times = 1

        for (x in 0 until times){

            var limit = if(content.readableBytes() > MAXLENGTH) MAXLENGTH else content.readableBytes()

            header.divider = times > 1

            header.messageLength = limit

            header.packetCount = times

            header.packetIndex = x + 1

            //如果为ACK 消息,是否需要重新设置sn?  待考察
            if(header.sn == 0){
                header.sn = serialNo.getAndIncrement().toInt()
            }


            var buf = header.encoder()

            var innerBuffer = Unpooled.buffer(1000);


            innerBuffer.writeBytes(buf)

            content.readBytes(innerBuffer,limit)


            innerBuffer.writeByte(xorCheckCode(innerBuffer).toInt())

            var msg = encoder0x7e(innerBuffer)


            //转义

            buffer.writeByte(0x7e)

            buffer.writeBytes(msg)

            buffer.writeByte(0x7e)
        }


        return buffer

    }


}
