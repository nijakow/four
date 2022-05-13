#include "/lib/app.c"
#include "/lib/sys/identity/user.c"
#include "/lib/sys/identity/group.c"
#include "/lib/sys/identity/mod.c"

private void add_user(string group, string name)
{
    if (!User_AmIRoot())
        printf("Permission denied.\n");
    else {
        if (!System_AddUserToGroup(name, group))
            printf("Error, user could not be added to group.\n");
    }
}

private void delete_user(string group, string name)
{
    if (!User_AmIRoot())
        printf("Permission denied.\n");
    else {
        if (!System_RemoveUserFromGroup(name, group))
            printf("Error, user could not be deleted.\n");
    }
}

private void list_users(string group)
{
    string* members = Group_GetMembers(group);
    foreach (string member : members)
        printf("%s\n", member);
}

private void usage(string appname)
{
    printf("usage: %s list|new|delete|add|remove <groupname> [username]\n", appname);
}

void main(string* argv)
{
    /* TODO: commands 'new' and 'delete' */
    if (argv.length == 3 && argv[1] == "list")
        list_users(argv[2]);
    else if (argv.length == 4 && argv[1] == "add")
        add_user(argv[2], argv[3]);
    else if (argv.length == 4 && argv[1] == "remove")
        remove_user(argv[2], argv[3]);
    else
        usage(argv[0]);
    exit(0);
}
