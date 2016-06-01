package com.heresysoft.arsenal.socket;

import java.io.IOException;
import java.net.Socket;

public abstract class ServerConnectionHandler extends ConnectionHandler {
    private long id = 0;
    private ServerHandler server = null;

    public ServerConnectionHandler(ServerHandler server, Socket socket) throws IOException {
        super(socket);

        // Initialize variables
        this.server = server;

        // Add to server
        if (server != null)
            this.id = server.addConnection(this);
    }

    public final long getId() {
        // Id of this client handler
        return id;
    }

    public final ServerHandler getServer() {
        // Handle to the server who created this client
        return server;
    }

    @Override
    public void terminated() {
        // Remove from server
        if (server != null)
            server.removeConnection(this.id);
    }

}
