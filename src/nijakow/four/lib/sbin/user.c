#include "/lib/app.c"
#include "/lib/sys/identity/user.c"
#include "/lib/sys/identity/group.c"
#include "/lib/sys/identity/mod.c"

private void add_user(string name)
{
    if (!User_AmIRoot())
        printf("Permission denied.\n");
    else {
        if (!System_AddUser(name))
            printf("Error, user could not be created.\n");
    }
}

private void delete_user(string name)
{
    if (!User_AmIRoot())
        printf("Permission denied.\n");
    else {
        if (!System_DeleteUser(name))
            printf("Error, user could not be deleted.\n");
    }
}

private void list_users(string group)
{
    string* members = Group_GetMembers(group);
    foreach (string member : members)
        printf("%s\n", member);
}

private void setpass(string pw, string name)
{
    if (User_ChangePassword(name, pw))
        printf("Password updated.\n");
    else
        printf("Error, password could not be set!\n");
    exit(0);
}

private void usage(string appname)
{
    printf("usage: %s list|new|delete|pass <username>\n", appname);
}

void main(string* argv)
{
    if (argv.length == 2 && argv[1] == "list")
        list_users("users");
    else if (argv.length == 3 && argv[1] == "new")
        add_user(argv[2]);
    else if (argv.length == 3 && argv[1] == "delete")
        delete_user(argv[2]);
    else if ((argv.length == 2 || argv.length == 3) && argv[1] == "pass") {
        string user;
        if (argv.length == 3) {
            if (!User_AmIRoot()) {
                printf("Only root can do this!\n");
                exit(1);
                return;
            }
            user = argv[2];
        } else
            user = User_Whoami();
        password(this::(argv[2])setpass, "Password for %s: ", argv[2]);
        return;
    } else
        usage(argv[0]);
    exit(0);
}
