package com.heresysoft.arsenal.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerBinding {
    private String address = null;
    private int port = 0;

    public ServerBinding(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return (address != null && address.length() > 0 ? InetAddress.getByName(address.trim()) : null);
    }

    public int getPort() {
        return port;
    }

}
