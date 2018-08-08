package com.pyzy.server808.message

import com.pyzy.server808.utils.ClassHelper
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled


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






















