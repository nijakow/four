inherits "/std/cli.c";

use $adduser;

string name;

void banner()
{
    printf("\{^64x64,nijakow,https://avatars.githubusercontent.com/u/79372954?v=4\}\{^64x64,mhahnFr,https://avatars.githubusercontent.com/u/83553794?v=4\}\n");
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

void setname(string name)
{
    this.name = name;
    password(this::setpass, "password: ");
}

void setpass(string pass)
{
    if (dologin(this.name, pass))
        startup();
}

bool trylogin(string username, string password)
{
    return the("/secure/logman.c")->login(name, password);
}

bool dologin(string username, string password)
{
    if (!trylogin(username, password)) {
        no_login();
        return false;
    } else {
        return true;
    }
}

void prepare(object player, string uname)
{
    player->activate_as(uname);
    object sword = new("/world/lantern.c");
    sword->set_name(uname + "'s lantern");
    sword->set_properly_named();
    sword->add_IDs(uname + "'s");
    sword->move_to(player);
    player->act_goto(the("/world/42/hn/reception.c"));
}

void launch_shell()
{
    string shell = getshell(getuid());
    if (shell == nil) {
        if (isroot()) {
            shell = "/bin/sh.c";
        } else {
            shell = "/usr/bin/ctrl.c";
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

void setuname(string uname)
{
    prepare(me(), uname);
    kickoff();
}

void kickoff()
{
    me()->submit_lines_to(connection()->write);
    connection()->add_close_cb(me()->freeze);
    me()->thaw();
    launch_shell();
}

void startup()
{
    if (isroot()) {
        launch_shell();
    } else {
        object player = the("/secure/logman.c")->get_player(name);
        set_me(player);
        if (!me()->query_is_activated())
            prompt(this::setuname, "By what name will you be known? ");
        else
            kickoff();
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
