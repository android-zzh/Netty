import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {

    public void server(int port) throws IOException{
        final ServerSocket socket = new ServerSocket(port);     //1.绑定服务器到指定的端口
        try {
            for(;;){
                final Socket clientSocket = socket.accept();        //2.接受一个连接
                System.out.println("Accepted connection from " + clientSocket);
                new Thread(new Runnable() {     //3.创建一个新的线程来处理连接
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));        //4.将消息发送到连接的客户端
                            out.flush();
                            clientSocket.close();       //5.一旦消息被写入和刷新时就关闭连接
                        } catch (IOException e) {
                            e.printStackTrace();

                            try {
                                clientSocket.close();
                            } catch (IOException e1) {
                                // ignore on close
                            }
                        }
                    }
                }).start();     //6.启动线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
