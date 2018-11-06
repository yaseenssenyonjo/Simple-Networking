# Simple Networking
Simple Networking is a Java library that simplifies asynchronous networking by abstracting the default libraries.

## TODO
 * Add UDP support in terms of the server and client.

## Usage
### Creating a simple TCP echo server.
```java
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
```

### Creating a simple TCP echo client.
```java
Client client = new Client();

client.AddListener(new IClientListener()
{
    @Override
    public void ConnectionEstablished()
    {
        client.Write("Hello World"); // Write some data.
    }

    @Override
    public void DataReceived(Client client, String data)
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