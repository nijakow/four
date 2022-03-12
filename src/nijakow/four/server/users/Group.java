package nijakow.four.server.users;

import java.util.HashSet;
import java.util.Set;

public class Group extends Identity {
    private final Set<Identity> members = new HashSet<>();

    protected Group(IdentityDatabase db, String name) {
        super(db, name);
    }

    @Override
    public Group asGroup() {
        return this;
    }

    @Override
    public boolean includes(Identity identity) {
        for (Identity member : members) {
            if (member.includes(identity))
                return true;
        }
        return super.includes(identity);
    }

    public void add(Identity identity) {
        // TODO: Check for circular structures
        if (identity == null || identity.includes(this))
            return;
        members.add(identity);
        identity.inGroup(this);
    }

    public void remove(Identity identity) {
        if (identity != null && members.contains(identity)) {
            members.remove(identity);
            identity.notInGroup(this);
        }
    }

    public Identity[] getMembers() {
        return members.toArray(new Identity[0]);
    }

    @Override
    public void unlink() {
        for (Identity i : getMembers())
            remove(i);
        super.unlink();
    }
}
