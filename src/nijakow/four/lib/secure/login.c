inherits "/std/cli.c";

use $adduser;


/*
 *    B a n n e r
 */

void banner()
{
    connection()->image("https://avatars.githubusercontent.com/u/79372954?v=4", "64x64", "nijakow");
    connection()->image("https://avatars.githubusercontent.com/u/83553794?v=4", "64x64", "mhahnFr");
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
    connection()->mode_italic();
    printf("  Welcome to the 42 MUD!\n");
    printf("\n");
    connection()->mode_normal();
    printf("\n");
}

/*
 *    E r r o r   H a n d l i n g
 */

void no_login()
{
    printf("\n");
    connection()->mode_red();
    connection()->mode_underscore();
    printf("Identification not recognized.\nConnection terminated.\n");
    connection()->mode_normal();
    connection()->close();
}

void no_shell()
{
    printf("\n");
    connection()->mode_red();
    connection()->mode_underscore();
    printf("Startup shell not present.\nPlease contact the administrator.\n");
    connection()->mode_normal();
    connection()->close();
}

/*
 *    L o g i n   P r o m p t s
 */

void setname(string name)
{
    password(this::(name)setpass, "password: ");
}

void setpass(string pass, string name)
{
    if (dologin(name, pass))
        player_activation_and_go(name);
}

void setuname(string uname)
{
    setup_new_player(me(), uname);
    thaw_and_go();
}

/*
 *    U s e r   A c c o u n t   H a n d l i n g
 */

bool dologin(string username, string password)
{
    if (!the("/secure/logman.c")->login(username, password)) {
        no_login();
        return false;
    } else {
        return true;
    }
}

/*
 *    P l a y e r   S e t u p
 */

void setup_new_player(object player, string uname)
{
    player->activate_as(uname);
    player->act_goto(the("/world/welcome.c"));
}

void thaw_and_go()
{
    me()->submit_lines_to(connection()->write);
    connection()->add_close_cb(me()->freeze);
    me()->thaw();
    launch_shell();
}

/*
 *    S h e l l   F i n d i n g   a n d   P l a y e r   S e t u p
 */

void launch_shell()
{
    string shell = getshell(getuid());
    if (shell == nil) {
        if (isroot()) {
            shell = "/bin/sh.c";
        } else {
            shell = "/bin/ctrl.c";
        }
    }
    if (!execappfromcli(this->logout, shell)) {
        if (isroot()) {
            execappfromcli(this->logout, "/bin/sh.c");
        } else {
            no_shell();
        }
    }
}

void player_activation_and_go(string name)
{
    if (isroot()) {
        launch_shell();
    } else {
        object player = the("/secure/logman.c")->get_player(name);
        set_me(player);
        if (!me()->query_is_activated())
            prompt(this::setuname, "By what name will you be known? ");
        else
            thaw_and_go();
    }
}


void logout()
{
    execappfromcli(nil, "/sbin/logout.c");
}

void start()
{
    banner();
    prompt(this::setname, "login: ");
}
