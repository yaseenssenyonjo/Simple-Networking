package examples;

import net.tcp.TcpClient;
import net.tcp.TcpServer;
import net.traits.IServerListener;

public class EchoServer
{
    public static void CreateTcpServer(int port)
    {
        TcpServer tcpServer = new TcpServer(port);

        tcpServer.AddListener(new IServerListener<>()
        {
            @Override
            public void ConnectionEstablished(TcpClient client)
            {
                System.out.println("A client has connected!");
            }

            @Override
            public void DataReceived(TcpClient client, String data)
            {
                System.out.printf("A client has sent data! - '%s'\n", data);
                client.Write(data); // Echo the data.
            }

            @Override
            public void Disconnected()
            {
                System.out.println("A client has disconnected!");
            }

            @Override
            public void Error(String message)
            {
                System.out.println("An error has occurred during an operation!");
            }
        });

        tcpServer.Start();
    }
}
