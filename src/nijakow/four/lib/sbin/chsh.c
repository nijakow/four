inherits "/lib/app.c";
inherits "/lib/sys/identity/user.c";

private void perform_chsh(string user, string path)
{
    if (User_IsRoot(user))
        printf("For stability reasons, changing the root shell is not possible.\n");
    else {
        if (User_ChangeShell(user, path))
            printf("Shell changed.\n");
        else
            printf("Error, could not set the shell!\n");
    }
}

void main(string* argv)
{
    string user = User_Whoami();
    string shell;

    if (argv.length == 2)
        perform_chsh(user, argv[1]);
    else if (argv.length == 3) {
        if (!User_AmIRoot())
            printf("Only root is allowed to do this!\n");
        else
            perform_chsh(argv[1], argv[2]);
    } else
        printf("Usage: %s <username> shell\n", (argv.length != 0) ? argv[0] : "chsh");
    exit(0);
}
