package com.xjdl.study.io.nio.util;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * ByteBuffer 展示工具
 *
 * @see io.netty.buffer.ByteBufUtil.HexUtil
 * @see StringUtil#byteToHexStringPadded(int)
 */
@Slf4j
public class ByteBufUtil {
	private static final String NEWLINE = get("line.separator", "\n");
	private static final char[] BYTE2CHAR = new char[256];
	private static final char[] HEXDUMP_TABLE = new char[256 * 4];
	private static final String[] HEXPADDING = new String[16];
	private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4];
	private static final String[] BYTE2HEX = new String[256];
	private static final String[] BYTEPADDING = new String[16];
	private static final String[] BYTE2HEX_PAD = new String[256];
	private static final String[] BYTE2HEX_NOPAD = new String[256];

	static {
		// Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
		for (int i = 0; i < BYTE2HEX_PAD.length; i++) {
			String str = Integer.toHexString(i);
			BYTE2HEX_PAD[i] = i > 0xf ? str : ('0' + str);
			BYTE2HEX_NOPAD[i] = str;
		}

		final char[] DIGITS = "0123456789abcdef".toCharArray();
		for (int i = 0; i < 256; i++) {
			HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
			HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
		}

		int i;

		// Generate the lookup table for hex dump paddings
		for (i = 0; i < HEXPADDING.length; i++) {
			int padding = HEXPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding * 3);
			for (int j = 0; j < padding; j++) {
				buf.append("   ");
			}
			HEXPADDING[i] = buf.toString();
		}

		// Generate the lookup table for the start-offset header in each row (up to 64KiB).
		for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i++) {
			StringBuilder buf = new StringBuilder(12);
			buf.append(NEWLINE);
			buf.append(Long.toHexString((long) i << 4 & 0xFFFFFFFFL | 0x100000000L));
			buf.setCharAt(buf.length() - 9, '|');
			buf.append('|');
			HEXDUMP_ROWPREFIXES[i] = buf.toString();
		}

		// Generate the lookup table for byte-to-hex-dump conversion
		for (i = 0; i < BYTE2HEX.length; i++) {
			BYTE2HEX[i] = ' ' + byteToHexStringPadded(i);
		}

		// Generate the lookup table for byte dump paddings
		for (i = 0; i < BYTEPADDING.length; i++) {
			int padding = BYTEPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding);
			for (int j = 0; j < padding; j++) {
				buf.append(' ');
			}
			BYTEPADDING[i] = buf.toString();
		}

		// Generate the lookup table for byte-to-char conversion
		for (i = 0; i < BYTE2CHAR.length; i++) {
			if (i <= 0x1f || i >= 0x7f) {
				BYTE2CHAR[i] = '.';
			} else {
				BYTE2CHAR[i] = (char) i;
			}
		}
	}

	private static String byteToHexStringPadded(int value) {
		return BYTE2HEX_PAD[value & 0xff];
	}

	private static <T> T checkNotNull(T arg, String text) {
		if (arg == null) {
			throw new NullPointerException(text);
		}
		return arg;
	}

	private static String checkNonEmpty(final String value, final String name) {
		if (checkNotNull(value, name).isEmpty()) {
			throw new IllegalArgumentException("Param '" + name + "' must not be empty");
		}
		return value;
	}

	private static String get(final String key, String def) {
		checkNonEmpty(key, "key");

		String value = null;
		try {
			if (System.getSecurityManager() == null) {
				value = System.getProperty(key);
			} else {
				value = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(key));
			}
		} catch (SecurityException e) {
			log.warn("Unable to retrieve a system property '{}'; default values will be used.", key, e);
		}

		if (value == null) {
			return def;
		}

		return value;
	}

	/**
	 * 打印所有内容
	 */
	public static void debugAll(ByteBuffer buffer) {
		int oldLimit = buffer.limit();
		buffer.limit(buffer.capacity());
		StringBuilder origin = new StringBuilder(256);
		appendPrettyHexDump(origin, buffer, 0, buffer.capacity());
		log.info("\n+--------+-------------------- All ------------------------+----------------+\n\t\t position: {}, limit: {}\n{}"
				, buffer.position(), oldLimit, origin);
		buffer.limit(oldLimit);
	}

	/**
	 * 打印可读取内容
	 */
	public static void debugRead(ByteBuffer buffer) {
		StringBuilder builder = new StringBuilder(256);
		appendPrettyHexDump(builder, buffer, buffer.position(), buffer.limit() - buffer.position());
		log.info("\n+--------+-------------------- Read -----------------------+----------------+\n\t\t position: {}, limit: {}\n{}"
				, buffer.position(), buffer.limit(), builder);
	}

	private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf, int offset, int length) {
		if (isOutOfBounds(offset, length, buf.capacity())) {
			throw new IndexOutOfBoundsException(
					"expected: " + "0 <= offset(" + offset + ") <= offset + length(" + length
							+ ") <= " + "buf.capacity(" + buf.capacity() + ')');
		}
		if (length == 0) {
			return;
		}
		dump.append("         +-------------------------------------------------+")
				.append(NEWLINE)
				.append("         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |")
				.append(NEWLINE)
				.append("+--------+-------------------------------------------------+----------------+");

		final int fullRows = length >>> 4;
		final int remainder = length & 0xF;

		// Dump the rows which have 16 bytes.
		for (int row = 0; row < fullRows; row++) {
			int rowStartIndex = (row << 4) + offset;

			// Per-row prefix.
			appendHexDumpRowPrefix(dump, row, rowStartIndex);

			// Hex dump
			int rowEndIndex = rowStartIndex + 16;
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
			}
			dump.append(" |");

			// ASCII dump
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
			}
			dump.append('|');
		}

		// Dump the last row which has less than 16 bytes.
		if (remainder != 0) {
			int rowStartIndex = (fullRows << 4) + offset;
			appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

			// Hex dump
			int rowEndIndex = rowStartIndex + remainder;
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
			}
			dump.append(HEXPADDING[remainder]);
			dump.append(" |");

			// Ascii dump
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
			}
			dump.append(BYTEPADDING[remainder]);
			dump.append('|');
		}

		dump.append(NEWLINE)
				.append("+--------+-------------------------------------------------+----------------+");
	}

	private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
		if (row < HEXDUMP_ROWPREFIXES.length) {
			dump.append(HEXDUMP_ROWPREFIXES[row]);
		} else {
			dump.append(NEWLINE);
			dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
			dump.setCharAt(dump.length() - 9, '|');
			dump.append('|');
		}
	}

	private static short getUnsignedByte(ByteBuffer buffer, int index) {
		return (short) (buffer.get(index) & 0xFF);
	}

	private static boolean isOutOfBounds(int index, int length, int capacity) {
		return (index | length | capacity | (index + length) | (capacity - (index + length))) < 0;
	}
}
