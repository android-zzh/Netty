import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {
    public void server(int port) throws IOException{
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);       //1.绑定服务器到指定的端口
        Selector selector = Selector.open();        //2.打开selector处理channel
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);       //3.注册selector到serverChannel,并指定这是专门接收任意连接
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes("UTF-8"));
        for(;;){
            try {
                selector.select();      //4.等待新的事件来处理.这将阻塞,知道一个事件传入
            } catch (IOException e) {
                e.printStackTrace();
                // handle exception
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();      //5.从收到的所有事件中获取SelectionKey实例
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if (key.isAcceptable()){        //6.检查该事件是一个新的连接准备好接收
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());       //7.接受客户端.并用selector注册
                        System.out.println("Accepted connection from " + client);
                    }

                    if(key.isWritable()){       //8.检查socket是否准备好写数据
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()){
                            if(client.write(buffer) == 0){      //9.将数据写入到所有连接的客户端.如果网络饱和,连接是科协的,那么这个循环将写入数据,知道该缓冲区是空的
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException e) {
                    key.cancel();

                    try {
                        key.channel().close();      //10.关闭连接
                    } catch (IOException e1) {
                        // 在关闭时忽略
                    }
                }
            }
        }

    }
}
