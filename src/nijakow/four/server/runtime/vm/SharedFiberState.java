package nijakow.four.server.runtime.vm;

import nijakow.four.server.runtime.objects.collections.FMapping;
import nijakow.four.server.runtime.security.users.User;

public class SharedFiberState {
    private User user;
    private final FMapping statics = new FMapping();

    public SharedFiberState(VM vm, User user) {
        this.user = user;
    }

    public SharedFiberState(VM vm) {
        this(vm, vm.getIdentityDB().getNewUnprivilegedUser());
    }

    public FMapping getStatics() { return statics; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
