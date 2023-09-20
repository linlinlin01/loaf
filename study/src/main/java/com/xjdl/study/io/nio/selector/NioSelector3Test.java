package com.xjdl.study.io.nio.selector;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 高效处理可写事件，减少空读
 */
@Slf4j
public class NioSelector3Test {

	public final int PORT = 9999;

	@Test
	void server() {
		try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
			ssc.configureBlocking(false);
			Selector selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			ssc.bind(new InetSocketAddress(PORT));
			while (selector.select() > 0) {
				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					iter.remove();
					if (key.isAcceptable()) {
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						SelectionKey sckey = sc.register(selector, SelectionKey.OP_READ, null);
						// 1. 向客户端发送大量数据
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < 5000000; i++) {
							sb.append("0");
						}
						ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
						// 2. 返回值代表实际写入的字节数
						int write = sc.write(buffer);
						log.info("{}", write);
						// 3. 判断是否有剩余内容
						if (buffer.hasRemaining()) {
							// 4. 关注可写事件   1                     4
							sckey.interestOps(sckey.interestOps() + SelectionKey.OP_WRITE);
//							sckey.interestOps(sckey.interestOps() | SelectionKey.OP_WRITE);
							// 5. 把未写完的数据挂到 sckey 上
							sckey.attach(buffer);
						}
					} else if (key.isWritable()) {
						ByteBuffer buffer = (ByteBuffer) key.attachment();
						SocketChannel sc = (SocketChannel) key.channel();
						int write = sc.write(buffer);
						log.info("{}", write);
						// 6. 清理操作
						if (!buffer.hasRemaining()) {
							// 需要清除buffer
							key.attach(null);
							// 不需关注可写事件
							key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
						}
					}
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	void client() {
		try (SocketChannel sc = SocketChannel.open(new InetSocketAddress("localhost", PORT))) {
			// 3. 接收数据
			int count = 0;
			while (true) {
				ByteBuffer buffer = ByteBuffer.allocate(1024 << 10);
				count += sc.read(buffer);
				log.info("{}", count);
				buffer.clear();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
