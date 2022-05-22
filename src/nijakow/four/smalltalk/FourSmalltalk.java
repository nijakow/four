package nijakow.four.smalltalk;

import nijakow.four.smalltalk.logging.Logger;
import nijakow.four.smalltalk.net.Server;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;

import java.io.IOException;

public class FourSmalltalk {
    private final World world;
    private final SmalltalkVM vm;
    private final Server server;
    private final Logger logger;

    public FourSmalltalk() throws IOException {
        this.logger = new Logger();
        this.world = new World();
        this.vm = new SmalltalkVM(this.world);
        this.server = new Server(this.logger);
        this.world.buildDefaultWorld();
    }

    public void serveOn(String hostname, int port) throws IOException {
        this.server.serveOn(hostname, port);
    }

    public void run() throws IOException {
        vm.startFiber(this.world.getValue(STSymbol.get("Four")), STSymbol.get("run"), new STInstance[]{});
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
