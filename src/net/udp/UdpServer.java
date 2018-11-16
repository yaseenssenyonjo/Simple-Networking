package net.udp;

import net.traits.IClientListener;
import net.traits.IServerListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a udp server.
 */
public class UdpServer
{
    /**
     * The underlying server socket.
     */
    private DatagramChannel _serverSocket;
    /**
     * The listeners.
     */
    private List<IServerListener<UdpClient>> _listeners = new ArrayList<>();

    /**
     * The listener for all clients.
     */
    private IClientListener<UdpClient> _clientListener = new IClientListener<>()
    {
        @Override
        public void ConnectionEstablished() {} // This will never be used as the Connect method will never be called.

        @Override
        public void DataReceived(UdpClient client, String data)
        {
            for (var listener : _listeners) listener.DataReceived(client, data);
        }

        @Override
        public void Disconnected()
        {
            for (var listener : _listeners) listener.Disconnected();
        }

        @Override
        public void Error(String message)
        {
            for (var listener : _listeners) listener.Error(message);
        }
    };

    /**
     * Initialises a new instance of the UdpServer class using the specified port.
     *
     * @param port The port to bind to.
     */
    public UdpServer(int port)
    {
        try
        {
            _serverSocket = DatagramChannel.open();
            _serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e)
        {
            // It's not possible to alert listeners as this is during initialisation.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds the specified listener.
     *
     * @param listener The listener to add.
     */
    public void AddListener(IServerListener<UdpClient> listener)
    {
        _listeners.add(listener);
    }

    /**
     * Starts the server.
     */
    public void Start()
    {
        // TODO: Rethink the interface for the UDP server.
        /*
        final ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        CompletableFuture.runAsync(() ->
        {
            while(true)
            {
                try
                {
                    SocketAddress client = _serverSocket.receive(readBuffer);
                    //System.out.println(client);
                    readBuffer.flip();
                    _serverSocket.send(readBuffer, client);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        */
    }
}
