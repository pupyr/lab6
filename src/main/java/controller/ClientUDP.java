package controller;

import controller.absCommand.Data;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;

public class ClientUDP {

    private static ClientUDP Instance;

    public static ClientUDP getInstance() {
        if (Instance == null) {
            Instance = new ClientUDP();
        }
        return Instance;
    }

    byte[] arr=new byte[10000];

    SocketAddress adr;
    DatagramSocket ds;
    DatagramPacket dp;
    DatagramChannel dc;

    ByteBuffer buf;

    int port=7579;


    public void sendKeyForSelector() throws IOException {
        adr = new InetSocketAddress(InetAddress.getLocalHost(),port);
        ServerSocketChannel serverSocket= ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        int selectionKey=SelectionKey.OP_CONNECT;
    }


    public void clientConnection(Data data) throws IOException, ClassNotFoundException {

        buf=Data.getInstance().DataToByte(data);

        //sendKeyForSelector();

        /*dc = DatagramChannel.open();
        dc.send(buf, adr);

        ByteBuffer buf1 = (ByteBuffer) buf.clear();

        adr=dc.receive(buf1);
        Data data1=Data.getInstance().byteToData(buf1);
        */


        arr= buf.array();
        ds = new DatagramSocket();
        dp = new DatagramPacket(arr, arr.length, InetAddress.getLocalHost(), port);
        ds.send(dp);

        arr=new byte[10000];
        dp = new DatagramPacket(arr, arr.length);
        ds.receive(dp);

        Data data1=Data.getInstance().byteToData(ByteBuffer.wrap(dp.getData()));

        System.out.println(data1.getDataForClient());
    }
}