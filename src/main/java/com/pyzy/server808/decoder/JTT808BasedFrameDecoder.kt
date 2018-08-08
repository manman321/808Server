/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.pyzy.server808.decoder

import com.pyzy.server808.message.Header
import com.pyzy.server808.message.JTTMessage
import com.pyzy.server808.message.Message
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.TooLongFrameException
import org.omg.CORBA.Object

/**
 * A decoder that splits the received [ByteBuf]s by one or more
 * delimiters.  It is particularly useful for decoding the frames which ends
 * with a delimiter such as [NUL][Delimiters.nulDelimiter] or
 * [newline characters][Delimiters.lineDelimiter].
 *
 * <h3>Predefined delimiters</h3>
 *
 *
 * [Delimiters] defines frequently used delimiters for convenience' sake.
 *
 * <h3>Specifying more than one delimiter</h3>
 *
 *
 * [JTT808BasedFrameDecoder] allows you to specify more than one
 * delimiter.  If more than one delimiter is found in the buffer, it chooses
 * the delimiter which produces the shortest frame.  For example, if you have
 * the following data in the buffer:
 * <pre>
 * +--------------+
 * | \r\nABC\nDEF\r\n |
 * +--------------+
</pre> *
 * a [JTT808BasedFrameDecoder]([Delimiters.lineDelimiter()][Delimiters.lineDelimiter])
 * will choose `'\n'` as the client delimiter and produce two frames:
 * <pre>
 * +-----+-----+
 * | ABC | DEF |
 * +-----+-----+
</pre> *
 * rather than incorrectly choosing `'\r\n'` as the client delimiter:
 * <pre>
 * +----------+
 * | ABC\nDEF |
 * +----------+
</pre> *
 */
class JTT808BasedFrameDecoder
/**
 * Creates a new instance.
 *
 * @param maxFrameLength  the maximum length of the decoded frame.
 * A [TooLongFrameException] is thrown if
 * the length of the frame exceeds this value.
 * @param stripDelimiter  whether the decoded frame should strip out the
 * delimiter or not
 * @param failFast  If <tt>true</tt>, a [TooLongFrameException] is
 * thrown as soon as the decoder notices the length of the
 * frame will exceed <tt>maxFrameLength</tt> regardless of
 * whether the entire frame has been read.
 * If <tt>false</tt>, a [TooLongFrameException] is
 * thrown after the entire frame that exceeds
 * <tt>maxFrameLength</tt> has been read.
 * @param delimiters  the delimiters
 */
(
        private val maxFrameLength: Int, private val stripDelimiter: Boolean, private val failFast: Boolean, vararg delimiters: ByteBuf) : ByteToMessageDecoder() {

    private val delimiters: Array<ByteBuf>
    private var discardingTooLongFrame: Boolean = false
    private var tooLongFrameLength: Int = 0

    /**
     * Return `true` if the current instance is a subclass of DelimiterBasedFrameDecoder
     */
    private val isSubclass: Boolean
        get() = javaClass != JTT808BasedFrameDecoder::class.java


    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     * A [TooLongFrameException] is thrown if
     * the length of the frame exceeds this value.
     * @param delimiter  the delimiter
     */
    @JvmOverloads constructor(maxFrameLength: Int = 4096, delimiter: ByteBuf = Unpooled.wrappedBuffer(arrayOf<Byte>(0x7e).toByteArray())) : this(maxFrameLength, true, delimiter) {}

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     * A [TooLongFrameException] is thrown if
     * the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     * delimiter or not
     * @param delimiter  the delimiter
     */
    constructor(
            maxFrameLength: Int, stripDelimiter: Boolean, delimiter: ByteBuf) : this(maxFrameLength, stripDelimiter, true, delimiter) {
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     * A [TooLongFrameException] is thrown if
     * the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     * delimiter or not
     * @param failFast  If <tt>true</tt>, a [TooLongFrameException] is
     * thrown as soon as the decoder notices the length of the
     * frame will exceed <tt>maxFrameLength</tt> regardless of
     * whether the entire frame has been read.
     * If <tt>false</tt>, a [TooLongFrameException] is
     * thrown after the entire frame that exceeds
     * <tt>maxFrameLength</tt> has been read.
     * @param delimiter  the delimiter
     */
    constructor(
            maxFrameLength: Int, stripDelimiter: Boolean, failFast: Boolean,
            delimiter: ByteBuf) : this(maxFrameLength, stripDelimiter, failFast, *arrayOf<ByteBuf>(delimiter.slice(delimiter.readerIndex(), delimiter.readableBytes()))) {
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     * A [TooLongFrameException] is thrown if
     * the length of the frame exceeds this value.
     * @param delimiters  the delimiters
     */
    constructor(maxFrameLength: Int, vararg delimiters: ByteBuf) : this(maxFrameLength, true, *delimiters) {}

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     * A [TooLongFrameException] is thrown if
     * the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     * delimiter or not
     * @param delimiters  the delimiters
     */
    constructor(
            maxFrameLength: Int, stripDelimiter: Boolean, vararg delimiters: ByteBuf) : this(maxFrameLength, stripDelimiter, true, *delimiters) {
    }

    init {
        validateMaxFrameLength(maxFrameLength)
        if (delimiters == null) {
            throw NullPointerException("delimiters")
        }
        if (delimiters.size == 0) {
            throw IllegalArgumentException("empty delimiters")
        }

        ByteArray(1)

        this.delimiters =  arrayOfNulls<ByteBuf>(delimiters.size) as Array<ByteBuf>

        for (i in delimiters.indices) {
            val d = delimiters[i]
            validateDelimiter(d)
            this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes())
        }
    }

    /** Returns true if the delimiters are "\n" and "\r\n".   */
    private fun isLineBased(delimiters: Array<ByteBuf>): Boolean {
        if (delimiters.size != 2) {
            return false
        }
        var a = delimiters[0]
        var b = delimiters[1]
        if (a.capacity() < b.capacity()) {
            a = delimiters[1]
            b = delimiters[0]
        }
        return (a.capacity() == 2 && b.capacity() == 1
                && a.getByte(0) == '\r'.toByte() && a.getByte(1) == '\n'.toByte()
                && b.getByte(0) == '\n'.toByte())
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val decoded = decode(ctx, `in`) ?: return

        val msg = decodeMessage(decoded as ByteBuf) ?: return

        out.add(msg)
    }

    /**
     * Create a frame out of the [ByteBuf] and return it.
     *
     * @param   ctx             the [ChannelHandlerContext] which this [ByteToMessageDecoder] belongs to
     * @param   buffer          the [ByteBuf] from which to read data
     * @return  frame           the [ByteBuf] which represent the frame or `null` if no frame could
     * be created.
     */
    @Throws(Exception::class)
    protected fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf): Any? {

        // Try all delimiters and choose the delimiter which yields the shortest frame.
        var minFrameLength = Integer.MAX_VALUE
        var minDelim: ByteBuf? = null
        for (delim in delimiters) {
            val frameLength = indexOf(buffer, delim, true)
            if (frameLength >= 0 && frameLength < minFrameLength) {
                minFrameLength = frameLength
                minDelim = delim
            }
        }


        if (minDelim != null) {
            val minDelimLength = minDelim.capacity()
            val frame: ByteBuf

            if (discardingTooLongFrame) {
                // We've just finished discarding a very large frame.
                // Go back to the initial state.
                discardingTooLongFrame = false
                buffer.skipBytes(minFrameLength + minDelimLength)

                val tooLongFrameLength = this.tooLongFrameLength
                this.tooLongFrameLength = 0
                if (!failFast) {
                    fail(tooLongFrameLength.toLong())
                }
                return null
            }

            if (minFrameLength > maxFrameLength) {
                // Discard read frame.
                buffer.skipBytes(minFrameLength + minDelimLength)
                fail(minFrameLength.toLong())
                return null
            }

            val skip = indexOf(buffer, minDelim) + minDelimLength

            if (stripDelimiter) {
                buffer.skipBytes(skip)
                frame = buffer.readRetainedSlice(minFrameLength - skip)
                buffer.skipBytes(minDelimLength)
            } else {
                buffer.skipBytes(skip)
                frame = buffer.readRetainedSlice(minFrameLength + minDelimLength - skip)
            }

            return frame
        } else {
            if (!discardingTooLongFrame) {
                if (buffer.readableBytes() > maxFrameLength) {
                    // Discard the content of the buffer until a delimiter is found.
                    tooLongFrameLength = buffer.readableBytes()
                    buffer.skipBytes(buffer.readableBytes())
                    discardingTooLongFrame = true
                    if (failFast) {
                        fail(tooLongFrameLength.toLong())
                    }
                }
            } else {
                // Still discarding the buffer since a delimiter is not found.
                tooLongFrameLength += buffer.readableBytes()
                buffer.skipBytes(buffer.readableBytes())
            }
            return null
        }
    }


    protected fun decodeMessage(msg:ByteBuf):Any?{


        val buf = Message.decoder0x7e(msg)

        if(!Message.validate(buf))return null

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


    private fun fail(frameLength: Long) {
        if (frameLength > 0) {
            throw TooLongFrameException(
                    "frame length exceeds " + maxFrameLength +
                            ": " + frameLength + " - discarded")
        } else {
            throw TooLongFrameException(
                    "frame length exceeds " + maxFrameLength +
                            " - discarding")
        }
    }


    private fun indexOf(haystack: ByteBuf, needle: ByteBuf, skip: Boolean = false): Int {
        var skip = skip
        for (i in haystack.readerIndex() until haystack.writerIndex()) {
            var haystackIndex = i
            var needleIndex: Int
            needleIndex = 0
            while (needleIndex < needle.capacity()) {
                if (haystack.getByte(haystackIndex) != needle.getByte(needleIndex)) {
                    break
                } else {
                    haystackIndex++
                    if (haystackIndex == haystack.writerIndex() && needleIndex != needle.capacity() - 1) {
                        return -1
                    }
                }
                needleIndex++
            }

            if (needleIndex == needle.capacity()) {
                // Found the needle from the haystack!
                if (skip) {
                    skip = false
                    continue
                }
                return i - haystack.readerIndex()
            }
        }
        return -1
    }


    private fun validateDelimiter(delimiter: ByteBuf?) {
        if (delimiter == null) {
            throw NullPointerException("delimiter")
        }
        if (!delimiter.isReadable) {
            throw IllegalArgumentException("empty delimiter")
        }
    }

    private fun validateMaxFrameLength(maxFrameLength: Int) {
        if (maxFrameLength <= 0) {
            throw IllegalArgumentException(
                    "maxFrameLength must be a positive integer: $maxFrameLength")
        }
    }
}
/**
 * Returns the number of bytes between the readerIndex of the haystack and
 * the client needle found in the haystack.  -1 is returned if no needle is
 * found in the haystack.
 */
