package net.traits;

import net.tcp.TcpClient;

/**
 * Represents a listener for a tcp or udp client.
 */
public interface IClientListener<T>
{
    /**
     * Occurs when a connection is established with the server.
     */
    void ConnectionEstablished();

    /**
     * Occurs when the data is received.
     *
     * @param client The client receiving data.
     * @param data   The data received represented as a string.
     */
    void DataReceived(T client, String data);

    /**
     * Occurs when a client is disconnected.
     */
    void Disconnected();

    /**
     * Occurs when a error occurs during an operation.
     *
     * @param message The error message.
     */
    void Error(String message);
}
