# Simple Networking
A library that simplifies asynchronous networking in Java.

## Usage
### Creating a simple TCP echo server.
```java
import net.tcp.TcpClient;
import net.tcp.TcpServer;
import net.traits.IServerListener;

int port = 1000;

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
```

### Creating a simple TCP echo client.
```java
import net.tcp.TcpClient;
import net.traits.IClientListener;

String host = "127.0.0.1";
int port = 1000;

TcpClient client = new TcpClient();

client.AddListener(new IClientListener<>()
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

### Creating a simple UDP echo client.

```java
import net.tcp.UdpClient;
import net.traits.IClientListener;

String host = "127.0.0.1";
int port = 1001;

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
