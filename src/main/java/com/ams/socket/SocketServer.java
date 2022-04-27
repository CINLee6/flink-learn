package com.ams.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * ClassName： SocketServer
 * Description：SocketServer
 *
 * @author Cody
 * @version 1.0.0
 * @date 2021/4/19 10:13
 */
public class SocketServer {

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(8989)) {
            while (true) {
                System.out.println("start listen...");
                Socket client = socket.accept();
                System.out.println("a client coming.");
                new Thread(() -> {
                    try {
                        OutputStream out = client.getOutputStream();
                        while (true) {
                            Scanner sc = new Scanner(System.in);
                            String in = sc.nextLine();
                            System.out.println("Input: " + in);
                            out.write((in + "\n").getBytes());
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
