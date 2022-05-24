package nijakow.four.smalltalk;

import nijakow.four.client.ClientWindow;
import nijakow.four.smalltalk.logging.Logger;
import nijakow.four.smalltalk.net.MUDConnection;
import nijakow.four.smalltalk.net.Server;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STPort;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.vm.FourException;

import java.io.IOException;

public class FourSmalltalk {
    private final World world;
    private final SmalltalkVM vm;
    private final Server server;
    private final Logger logger;

    public FourSmalltalk() throws IOException, FourException {
        this.logger = new Logger();
        this.world = new World();
        this.vm = new SmalltalkVM(this.world);
        this.server = new Server(this.logger);
        this.world.buildDefaultWorld();
    }

    public void serveOn(String hostname, int port) throws IOException {
        this.server.serveOn(hostname, port, (connection) -> {
            final STInstance master = this.world.getValue("Four");
            try {
                this.vm.startFiber(master, "newConnection:", new STInstance[]{
                        new STPort(connection)
                });
            } catch (FourException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void fourconnectOn(String hostname, int port, STInstance home) throws IOException {
        this.server.serveOn(hostname, port, (connection) -> {
            MUDConnection mudConnection = new MUDConnection(this.vm, connection);
            mudConnection.writeResult(home);
        });
    }

    public void run() throws IOException, FourException {
        vm.startFiber(this.world.getValue(STSymbol.get("Four")), STSymbol.get("main"), new STInstance[]{});
        while (true) {
            vm.tick();
            server.tick(vm.getPreferredIdleTime());
        }
    }

    public static void main(String[] args) throws IOException, FourException {
        FourSmalltalk four = new FourSmalltalk();
        four.serveOn("localhost", 4242);
        ClientWindow.openWindow("localhost", new int[] {4242});
        four.run();
    }
}
