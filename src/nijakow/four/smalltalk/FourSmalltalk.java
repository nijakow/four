package nijakow.four.smalltalk;

import nijakow.four.smalltalk.logging.Logger;
import nijakow.four.smalltalk.net.Server;

import java.io.IOException;

public class FourSmalltalk {
    private SmalltalkVM vm;
    private Server server;
    private Logger logger;

    public FourSmalltalk() throws IOException {
        this.logger = new Logger();
        this.vm = new SmalltalkVM();
        this.server = new Server(this.logger);
    }

    public void serveOn(String hostname, int port) throws IOException {
        this.server.serveOn(hostname, port);
    }

    public void run() throws IOException {
        while (true) {
            vm.tick();
            server.tick(vm.getPreferredIdleTime());
        }
    }

    public static void main(String[] args) throws IOException {
        FourSmalltalk four = new FourSmalltalk();
        four.serveOn("localhost", 4242);
        four.run();
    }
}
