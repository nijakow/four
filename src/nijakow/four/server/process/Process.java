package nijakow.four.server.process;

import nijakow.four.server.nvfs.files.TextFile;
import nijakow.four.server.process.filedescriptor.IFileDescriptor;
import nijakow.four.server.process.filedescriptor.TextFileDescriptor;
import nijakow.four.server.runtime.objects.collections.FMapping;
import nijakow.four.server.runtime.vm.VM;
import nijakow.four.server.users.User;

public class Process {
    private final VM vm;
    private User user;
    private final FMapping statics = new FMapping();
    private final IFileDescriptor[] descriptors = new IFileDescriptor[1024];

    public Process(VM vm, User user) {
        this.vm = vm;
        this.user = user;
    }

    public Process(VM vm) {
        this(vm, vm.getIdentityDB().getUnprivilegedUser());
    }

    public FMapping getStatics() { return statics; }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private final int allocateFD(IFileDescriptor fd) {
        for (int x = 0; x < descriptors.length; x++) {
            if (descriptors[x] == null) {
                descriptors[x] = fd;
                return x;
            }
        }
        return -1;
    }

    public int open(String path, boolean read, boolean write) {
        TextFile file = vm.getFilesystem().resolveTextFile(path);
        if (file == null)
            return -1;
        if ((!read || file.getRights().checkReadAccess(getUser()))
            && (!write || file.getRights().checkWriteAccess(getUser()))) {
            TextFileDescriptor tfd = new TextFileDescriptor(file);
            return allocateFD(tfd);
        }
        return -1;
    }

    public IFileDescriptor get(int index) {
        return descriptors[index];
    }

    public boolean close(int fd) {
        if (descriptors[fd] != null) {
            descriptors[fd].close();
            descriptors[fd] = null;
            return true;
        } else {
            return false;
        }
    }
}
