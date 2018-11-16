package net.udp;

import net.traits.IClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a udp client.
 */
public class UdpClient
{
    /**
     * The underlying client socket.
     */
    private DatagramChannel _clientSocket;
    /**
     * The listeners.
     */
    private List<IClientListener<UdpClient>> _listeners = new ArrayList<>();

    /**
     * Initialises a new instance of the UdpClient class.
     */
    public UdpClient()
    {
        try
        {
            _clientSocket = DatagramChannel.open();
            _clientSocket.configureBlocking(false);
        } catch (IOException e)
        {
            // It's not possible to alert listeners as this is during initialisation.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initialises a new instance of the UdpClient class using the specified socket.
     *
     * @param socket The socket to use for IO operations.
     */
    UdpClient(DatagramChannel socket)
    {
        _clientSocket = socket;
        Read();
    }

    /**
     * Adds the specified listener.
     *
     * @param listener The listener to add.
     */
    public void AddListener(IClientListener<UdpClient> listener)
    {
        _listeners.add(listener);
    }

    /**
     * Begins an asynchronous request for a remote connection.
     *
     * @param host The host.
     * @param port The port.
     */
    public void Connect(String host, int port)
    {
        CompletableFuture.runAsync(() ->
        {
            try
            {
                _clientSocket.connect(new InetSocketAddress(host, port));
                for (var listener : _listeners) listener.ConnectionEstablished();
                Read();
            } catch (IOException e)
            {
                String errorMessage = e.getMessage();
                if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
            }
        });
    }

    /**
     * Writes the specified data asynchronously.
     *
     * @param data The data to write.
     */
    public void Write(String data)
    {
        CompletableFuture.runAsync(() ->
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
            try
            {
                _clientSocket.write(writeBuffer);
            } catch (IOException e)
            {
                String errorMessage = e.getMessage();
                if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
            }
        });
    }

    /**
     * Closes the connection.
     */
    public void Close()
    {
        CompletableFuture.runAsync(() ->
        {
            try
            {
                _clientSocket.close();
            } catch (IOException e)
            {
                String errorMessage = e.getMessage();
                if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
            }
        });
    }

    /**
     * Begins to asynchronously read data.
     */
    private void Read()
    {
        CompletableFuture.runAsync(() ->
        {
            // Continuously attempt to read data until no longer connected.
            while(true)
            {
                final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int numberOfBytesRead;

                try
                {
                    numberOfBytesRead = _clientSocket.read(readBuffer);
                } catch (IOException e)
                {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
                    break;
                }

                // There was no data received - disconnected.
                if (numberOfBytesRead == -1)
                {
                    // Notify the listeners.
                    for (var listener : _listeners) listener.Disconnected();
                    break;
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
                for (var listener : _listeners) listener.DataReceived(this, dataString);
            }
        });
    }
}
