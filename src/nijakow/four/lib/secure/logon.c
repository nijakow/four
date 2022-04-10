inherits "/lib/app.c";

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

private void launch_shell()
{
    exec(exit, "/bin/sh.c", []);
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
