import net.Client;
import net.Server;
import net.traits.IClientListener;
import net.traits.IServerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        StartEchoServer(1000);
        ConnectClient("127.0.0.1", 1000);


        while (true)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
        }
    }

    private static void StartEchoServer(int port)
    {
        Server server = new Server(port);
        server.AddListener(new IServerListener()
        {
            @Override
            public void ConnectionEstablished(Client client)
            {
                System.out.println("A client has connected!");
            }

            @Override
            public void DataReceived(Client client, String data)
            {
                System.out.printf("A client has sent data! - '%s'\n", data);
                client.Write(data);
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
        server.Start();
    }

    private static void ConnectClient(String host, int port)
    {
        Client client = new Client();

        client.AddListener(new IClientListener()
        {
            @Override
            public void ConnectionEstablished()
            {
                client.Write("Hello World");
            }

            @Override
            public void DataReceived(Client client, String data)
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
