package nijakow.four.server;

import nijakow.four.server.logging.LogLevel;
import nijakow.four.server.logging.Logger;
import nijakow.four.server.net.Server;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.storage.StorageManager;
import nijakow.four.server.storage.serialization.fs.BasicFSSerializer;
import nijakow.four.server.storage.serialization.fs.deserializer.BasicFSDeserializer;
import nijakow.four.server.users.IdentityDatabase;
import nijakow.four.server.runtime.vm.VM;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Four implements Runnable {
	private final Logger logger;
	private final IdentityDatabase db;
	private final NVFileSystem fs;
	private final StorageManager storageManager;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;

	public Four(IdentityDatabase db, NVFileSystem fs, String storagePath, String hostname, int[] ports) throws IOException {
		this.logger = new Logger();
		this.db = db;
		this.fs = fs;
		this.storageManager = new StorageManager(storagePath);
		this.server = new Server(this.logger);
		this.vm = new VM(this);

		logger.println(LogLevel.INFO, "Storage path is '" + storagePath + "'.");
		for (int port : ports)
			server.serveOn(hostname, port);
	}

	public Logger getLogger() { return this.logger; }
	public IdentityDatabase getIdentityDB() { return this.db; }
	public NVFileSystem getFilesystem() { return this.fs; }
	public StorageManager getStorageManager() { return this.storageManager; }
	public Server getServer() { return this.server; }
	
	public void start() throws FourRuntimeException {
		if (!wasStarted) {
			wasStarted = true;
			
			{
				Blue master = fs.getBlue(this.vm,"/secure/master.c");
				
				if (master == null) {
					throw new FourRuntimeException("/secure/master.c is not defined!");
				}
				
				vm.startFiber(master, Key.get("create"));
			}
		}
	}

	public boolean takeSnapshot() {
		try {
			final OutputStream outputStream = getStorageManager().startNewSnapshot();
			final BasicFSSerializer serializer = new BasicFSSerializer(outputStream);
			serializer.newMetaEntry("users", getIdentityDB().serializeAsBytes());
			serializer.serialize(getFilesystem());
			outputStream.close();
			return true;
		} catch (IOException e) {
			getLogger().printException(e);
			return false;
		}
	}

	private boolean loadSnapshot(InputStream stream) {
		if (stream == null) return false;
		try {
			BasicFSDeserializer deserializer = new BasicFSDeserializer(stream);
			deserializer.restore(getFilesystem(), getIdentityDB());
			stream.close();
			return true;
		} catch (IOException e) {
			getLogger().printException(e);
			return false;
		}
	}

	public boolean loadLatestSnapshot() {
		return loadSnapshot(getStorageManager().getLatestSnapshot());
	}
	
	public void run() {
		try {
			start();
		} catch (FourRuntimeException e) {
			logger.printException(LogLevel.CRITICAL, e);
			logger.println(LogLevel.CRITICAL, "Fatal error! Stopping...");
			return;
		}

		while (true) {
			try {
				vm.tick();
			} catch (Exception e) {
				logger.printException(e);
				logger.println(LogLevel.ERROR, "This error was not fatal. Execution will continue.");
				try {
					vm.reportError("exception", e.getClass().getName(), e.getMessage());
				} catch (FourRuntimeException ex) {
					logger.printException(e);
					logger.println(LogLevel.CRITICAL, "Unable to notify the server of exceptions!");
				}
			}
			long wish = vm.notificationWish();
			if (wish > 0) {
				try {
					server.tick(wish);
				} catch (IOException e) {
					logger.printException(e);
				}
			}
		}
	}
}
