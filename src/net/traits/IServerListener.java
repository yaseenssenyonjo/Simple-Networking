package net.traits;

/**
 * Represents a listener for a tcp or udp server.
 */
public interface IServerListener<T>
{
    /**
     * Occurs when a client has connected to the server.
     *
     * @param client The accepted client.
     */
    void ConnectionEstablished(T client);

    /**
     * Occurs when the server receives data from a client.
     *
     * @param client The client the server is receiving data from.
     * @param data   The data received represented as a string.
     */
    void DataReceived(T client, String data);

    /**
     * Occurs when a client is disconnected from the server.
     */
    void Disconnected();

    /**
     * Occurs when a error occurs during an operation.
     *
     * @param message The error message.
     */
    void Error(String message);
}