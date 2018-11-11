package net.udp;

import net.traits.IClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

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
     * The remote host.
     */
    private InetSocketAddress _remoteHost;
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

        try
        {
            _clientSocket.configureBlocking(false);
        } catch (IOException exc)
        {
            // It's not possible to alert listeners as this is during initialisation.
            System.out.println(exc.getMessage());
        }
        // Read(); // todo: consider using threading with it trying to read constantly in a while loop.
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

    public void Connect(String host, int port)
    {
        _remoteHost = new InetSocketAddress(host, port);

        try
        {
            _clientSocket.connect(_remoteHost);
        } catch (IOException e)
        {
            String errorMessage = e.getMessage();
            if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
        }
    }

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
        try
        {
            _clientSocket.send(writeBuffer, _remoteHost);
        } catch (IOException e)
        {
            String errorMessage = e.getMessage();
            if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
        }
    }

    public void Close()
    {
        try
        {
            _clientSocket.close();
        } catch (IOException e)
        {
            String errorMessage = e.getMessage();
            if (errorMessage != null) for (var listener : _listeners) listener.Error(errorMessage);
        }
    }

    private void Read()
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
            return;
        }

        // There was no data received - disconnected.
        if (numberOfBytesRead == -1)
        {
            // Notify the listeners.
            for (var listener : _listeners) listener.Disconnected();
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
        for (var listener : _listeners) listener.DataReceived(this, dataString);
    }
}
