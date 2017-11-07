package malbeth.javautils;

public abstract class Worker implements Runnable {
    private boolean active = false;
    private boolean stop = false;

    public boolean isActive() {
        return active;
    }

    public boolean isStopping() {
        return stop;
    }

    @Override
    public void run() {
        if (!stop) {
            active = true;

            while (!stop) {
                try {
                    work();
                } catch (Exception e) {
                }
            }

            terminate();
        }
    }

    public void stop() {
        stop = true;
    }

    public void terminate() {
        if (!stop)
            stop();

        active = false;
    }

    public void waitUntilStopped(int timeout) {
        long timestamp = System.currentTimeMillis();
        while (active && System.currentTimeMillis() - timestamp < timeout) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void work();

}
