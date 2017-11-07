package malbeth.javautils.socket;

public abstract class UDPServerHandler extends ServerHandler {
    public UDPServerHandler(ServerBinding binding) {
        super(binding);
    }

    @Override
    protected void closing() {
    }

    @Override
    protected boolean execute() {
        return false;
    }

    @Override
    protected boolean open() {
        return false;
    }

}
