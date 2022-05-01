inherits "/lib/app.c";
inherits "/lib/sys/identity/user.c";

use $login;

private string username;

private void banner()
{
    printf("\n");
    printf("nijakow and mhahnFr present...\n");
    printf("\n");
    printf("\n");
    printf("                                  /####/  #################\n");
    printf("                                /####/    #####/     ######\n");
    printf("                              /####/      ###/       ######\n");
    printf("                            /####/        #/         /####/\n");
    printf("                          /####/          /        /####/\n");
    printf("                        /####/                   /####/\n");
    printf("                      /####/                   /####/\n");
    printf("                    /####/                   /####/\n");
    printf("                  /####/                   /####/         /\n");
    printf("                  #####################   ######         /#\n");
    printf("                  #####################   ######       /###\n");
    printf("                  #####################   ######     /#####\n");
    printf("                                 ######   #################\n");
    printf("                                 ######\n");
    printf("                                 ######\n");
    printf("                                 ######\n");
    printf("\n");
    printf("  Welcome to the 42 MUD!\n");
    printf("\n");
    printf("\n");
}

private void identification_failure()
{
    printf("\n");
    printf("Credentials not recognized.\n");
    printf("\n");
    ask_username();
}

private void launch_shell_failed()
{
    if (User_IsRoot(this.username)) {
        if (exec(exit, "/bin/sh.c", {"/bin/sh.c"}))
            return;
    }
    printf("No login shell was found.\nPlease contact the system administrator.\n");
    exit(0);
}

private void launch_shell()
{
    string shell = User_GetShell(this.username);
    if (shell == nil || !exec(exit, shell, {shell}))
        launch_shell_failed();
}

private void receive_password(string pass)
{
    if (!$login(this.username, pass)) {
        identification_failure();
    } else {
        launch_shell();
    }
}

private void ask_password()
{
    password(receive_password, "password: ");
}

private void receive_username(string username)
{
    this.username = username;
    ask_password();
}

private void ask_username()
{
    prompt(receive_username, "login: ");
}

void main()
{
    banner();
    ask_username();
}
