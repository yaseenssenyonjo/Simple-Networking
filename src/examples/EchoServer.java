package examples;

import net.Client;
import net.Server;
import net.traits.IServerListener;

public class EchoServer
{
    public static void CreateServer(int port)
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

        server.Start();
    }
}
