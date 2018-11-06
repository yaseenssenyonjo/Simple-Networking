package net.traits;

import net.Client;

/**
 * Represents a listener for a server.
 */
public interface IServerListener
{
    /**
     * Occurs when a client has connected to the server.
     * @param client The accepted client.
     */
    void ConnectionEstablished(Client client);
    /**
     * Occurs when the server receives data from a client.
     * @param client The client the server is receiving data from.
     * @param data The data received represented as a string.
     */
    void DataReceived(Client client, String data);
    /**
     * Occurs when a client is disconnected from the server.
     */
    void Disconnected();
    /**
     * Occurs when a error occurs during an operation.
     * @param message The error message.
     */
    void Error(String message);
}