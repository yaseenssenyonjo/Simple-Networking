import examples.EchoServer;

import net.tcp.TcpClient;
import net.traits.IClientListener;
import net.udp.UdpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        EchoServer.CreateTcpServer(1000);
        ConnectTcpClient("127.0.0.1", 1000);

        // EchoServer.CreateUdpServer(1000);
        // ConnectUdpClient("127.0.0.1", 1001);

        while (true)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
        }
    }

    private static void ConnectTcpClient(String host, int port)
    {
        TcpClient client = new TcpClient();

        client.AddListener(new IClientListener<>()
        {
            @Override
            public void ConnectionEstablished()
            {
                client.Write("Hello World");
            }

            @Override
            public void DataReceived(TcpClient client, String data)
            {
                System.out.printf("The server has sent data! - '%s'\n", data);
                // Disconnect after receiving data.
                client.Close();
            }

            @Override
            public void Disconnected()
            {
                System.out.println("We are no longer connected.");
            }

            @Override
            public void Error(String message)
            {
                System.out.println(message);
            }
        });

        client.Connect(host, port);
    }

    private static void ConnectUdpClient(String host, int port)
    {
        UdpClient client = new UdpClient();

        client.AddListener(new IClientListener<>()
        {
            @Override
            public void ConnectionEstablished()
            {
                client.Write("Hello World");
            }

            @Override
            public void DataReceived(UdpClient client, String data)
            {
                System.out.printf("The server has sent data! - '%s'\n", data);
                // Disconnect after receiving data.
                client.Close();
            }

            @Override
            public void Disconnected()
            {
                System.out.println("We are no longer connected.");
            }

            @Override
            public void Error(String message)
            {
                System.out.println(message);
            }
        });

        client.Connect(host, port);
    }
}
