package net.traits;

import net.Client;

public interface IClientListener
{
    /**
     * Occurs when a connection is established with the server.
     */
    void ConnectionEstablished();
    /**
     * Occurs when the data is received.
     * @param client The client receiving data.
     * @param data The data received represented as a string.
     */
    void DataReceived(Client client, String data);
    /**
     * Occurs when a client is disconnected.
     */
    void Disconnected();
    /**
     * Occurs when a error occurs during an operation.
     * @param message The error message.
     */
    void Error(String message);
}
