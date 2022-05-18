#include "/lib/app.c"
#include "/lib/sys/identity/user.c"

use $login;

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
}

private void launch_shell_failed(string username)
{
    if (User_IsRoot(username)) {
        if (exec(this::exit, "/bin/sh.c", {"/bin/sh.c"}))
            return;
    }
    printf("No login shell was found.\nPlease contact the system administrator.\n");
    exit(0);
}

private void launch_shell(string username)
{
    string shell = User_GetShell(username);
    if (shell == nil || !exec(this::exit, shell, {shell}))
        launch_shell_failed(username);
}

void main()
{
    banner();
    string username = this.prompt("login: ");
    string password = this.password("password: ");
    if ($login(username, password))
        launch_shell(username);
    else
        identification_failure();
}
