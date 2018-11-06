package net;

import net.traits.IClientListener;
import net.traits.IServerListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an asynchronous server.
 */
public class Server
{
    /**
     * The underlying server socket.
     */
    private AsynchronousServerSocketChannel _serverSocket;
    /**
     * The listeners.
     */
    private List<IServerListener> _listeners = new ArrayList<>();

    /**
     * The listener for all clients.
     */
    private IClientListener _clientListener = new IClientListener()
    {
        @Override
        public void ConnectionEstablished() {} // This will never be used as the Connect method will never be called.

        @Override
        public void DataReceived(Client client, String data)
        {
            for (var listener: _listeners) listener.DataReceived(client, data);
        }

        @Override
        public void Disconnected()
        {
            for (var listener: _listeners) listener.Disconnected();
        }

        @Override
        public void Error(String message)
        {
            for (var listener: _listeners) listener.Error(message);
        }
    };

    /**
     * Initialises a new instance of the Server class using the specified port.
     * @param port The port to bind to.
     */
    public Server(int port)
    {
        try
        {
            _serverSocket = AsynchronousServerSocketChannel.open();
            _serverSocket.bind(new InetSocketAddress(port));
        }
        catch (IOException e)
        {
            // It's not possible to alert listeners as this is during initialisation.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds the specified listener.
     * @param listener The listener to add.
     */
    public void AddListener(IServerListener listener)
    {
        _listeners.add(listener);
    }

    /**
     * Starts the server.
     */
    public void Start()
    {
        _serverSocket.accept(null, new CompletionHandler<>()
        {
            @Override
            public void completed(AsynchronousSocketChannel clientSocket, Object emptyAttachment)
            {
                // Wrap the socket.
                Client client = new Client(clientSocket);

                // Add the listener.
                client.AddListener(_clientListener);

                // Notify the listeners.
                for (var listener: _listeners) listener.ConnectionEstablished(client);

                // Prepare to accept the next client using this same handler.
                _serverSocket.accept(null, this);
            }

            @Override
            public void failed(Throwable exc, Object emptyAttachment)
            {
                String errorMessage = exc.getMessage();
                if(errorMessage != null) for (var listener: _listeners) listener.Error(errorMessage);
            }
        });
    }
}
