# Simple Networking
Simple Networking is a Java library that **simplifies asynchronous networking** by abstracting the default libraries.

## TODO
 * Add UDP support in terms of the tcpServer and client.

## Usage
### Creating a simple TCP echo tcpServer.
```java
import net.tcp.TcpClient;
import net.tcp.TcpServer;
import net.traits.ITcpServerListener;

int port = 1000;

TcpServer tcpServer = new TcpServer(port);
        
tcpServer.AddListener(new ITcpServerListener()
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
```

### Creating a simple TCP echo client.
```java
import net.tcp.TcpClient;
import net.traits.ITcpClientListener;

String host = "127.0.0.1";
int port = 1000;

TcpClient client = new TcpClient();

client.AddListener(new ITcpClientListener()
{
    @Override
    public void ConnectionEstablished()
    {
        client.Write("Hello World"); // Write some data.
    }

    @Override
    public void DataReceived(TcpClient client, String data)
    {
        System.out.printf("The tcpServer has sent data! - '%s'\n", data);
        client.Write(data); // Echo the data received.
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
```