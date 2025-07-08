package io.metersphere.api.tcp.server;

import io.metersphere.commons.utils.LogUtil;
import io.metersphere.commons.utils.NamedThreadFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author song.tianyang
 * @Date 2021/8/11 10:35 上午
 */
public class TCPServer implements Runnable {
    private final int port;
    private ServerSocket serverSocket;

    private TCPService server;

    private final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(10, new NamedThreadFactory("tcp-mock-server"));

    public TCPServer(int port) {
        this.port = port;
    }

    public void openSocket() throws Exception {
        this.serverSocket = new ServerSocket(this.port);

        do {
            if (!this.serverSocket.isClosed()) {
                Socket socket = this.serverSocket.accept();
                server = new TCPService(socket, port);
                threadPool.execute(server);
            }
        } while (!this.serverSocket.isClosed());
    }

    public boolean isSocketOpen() {
        return this.serverSocket != null && !this.serverSocket.isClosed();
    }

    public void closeSocket() throws Exception {
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            if (server != null) {
                server.close();
            }
            this.serverSocket.close();
        }
    }

    @Override
    public void run() {
        try {
            this.openSocket();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }
}
