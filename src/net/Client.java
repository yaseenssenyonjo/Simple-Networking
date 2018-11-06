package net;

import net.traits.IClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an asynchronous client.
 */
public class Client
{
    /**
     * The underlying client socket.
     */
    private AsynchronousSocketChannel _clientSocket;
    /**
     * The listeners.
     */
    private List<IClientListener> _listeners = new ArrayList<>();

    /**
     * Initialises a new instance of the Client class.
     */
    public Client()
    {
        try
        {
            _clientSocket = AsynchronousSocketChannel.open();
        }
        catch (IOException e)
        {
            // It's not possible to alert listeners as this is during initialisation.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initialises a new instance of the Server class using the specified socket.
     * @param socket The socket to use for IO operations.
     */
    Client(AsynchronousSocketChannel socket)
    {
        _clientSocket = socket;
        Read();
    }

    /**
     * Adds the specified listener.
     * @param listener The listener to add.
     */
    public void AddListener(IClientListener listener)
    {
        _listeners.add(listener);
    }

    /**
     * Begins an asynchronous request for a remote connection.
     * @param host The host.
     * @param port The port.
     */
    public void Connect(String host, int port)
    {
        _clientSocket.connect(new InetSocketAddress(host, port), null, new CompletionHandler<>()
        {
            @Override
            public void completed(Void result, Object emptyAttachment)
            {
                for (var listener: _listeners) listener.ConnectionEstablished();

                // Prepare to start receiving data.
                Read();
            }

            @Override
            public void failed(Throwable exc, Object emptyAttachment)
            {
                String errorMessage = exc.getMessage();
                if(errorMessage != null) for (var listener: _listeners) listener.Error(errorMessage);
            }
        });
    }

    /**
     * Writes the specified data asynchronously.
     * @param data The data to write.
     */
    public void Write(String data)
    {
        // Convert the data into bytes.
        byte[] dataBytes = data.getBytes();

        // Allocate a buffer.
        ByteBuffer writeBuffer = ByteBuffer.allocate(dataBytes.length);

        // Transfer the data into the buffer.
        writeBuffer.put(dataBytes);

        // Sets the limit to the current position.
        // Sets the read head position to zero.
        writeBuffer.flip();

        // Write the data.
        _clientSocket.write(writeBuffer, null, new CompletionHandler<>()
        {
            @Override
            public void completed(Integer result, Object emptyAttachment) {}

            @Override
            public void failed(Throwable exc, Object emptyAttachment)
            {
                String errorMessage = exc.getMessage();
                if(errorMessage != null) for (var listener: _listeners) listener.Error(errorMessage);
            }
        });
    }

    /**
     * Closes the connection.
     */
    public void Close()
    {
        try
        {
            _clientSocket.close();
        }
        catch (IOException exc)
        {
            String errorMessage = exc.getMessage();
            if(errorMessage != null) for (var listener: _listeners) listener.Error(errorMessage);
        }
    }

    /**
     * Begins to asynchronously read data.
     */
    private void Read()
    {
        final ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        _clientSocket.read(readBuffer, this, new CompletionHandler<>()
        {
            @Override
            public void completed(Integer numberOfBytesRead, Client client)
            {
                // There was no data received - disconnected.
                if(numberOfBytesRead == -1)
                {
                    // Notify the listeners.
                    for (var listener: _listeners) listener.Disconnected();
                    return;
                }

                // Sets the limit to the current position.
                // Sets the read head position to zero.
                readBuffer.flip();

                byte[] receiveBytes = new byte[numberOfBytesRead];

                // Transfers the data in the buffer to byte array.
                readBuffer.get(receiveBytes);

                // Convert byte array into a string.
                String dataString = new String(receiveBytes);

                // Clear the receive buffer to be reused again.
                readBuffer.clear();

                // Notify the listeners.
                for (var listener: _listeners) listener.DataReceived(client, dataString);

                // Prepare to read the next chunk of data using this same handler.
                _clientSocket.read(readBuffer, client, this);
            }

            @Override
            public void failed(Throwable exc, Client client)
            {
                String errorMessage = exc.getMessage();
                if(errorMessage != null) for (var listener: _listeners) listener.Error(errorMessage);

                // Check if the socket is still open, if not notify the listeners.
                if(!client._clientSocket.isOpen()) for (var listener: _listeners) listener.Disconnected();
            }
        });
    }
}
