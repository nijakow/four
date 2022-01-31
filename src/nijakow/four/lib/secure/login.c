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

void newuser(string name)
{
    this.name = name;
    connection()->password(this::newpass, "Please choose a password: ");
}

void newpass(string pass)
{
    if (!($adduser(name, pass) && trylogin(name, pass))) {
        printf("\n");
        connection()->mode_red();
        connection()->mode_underscore();
        printf("Unable to create user.\nConnection terminated.\n");
        connection()->mode_normal();
        connection()->close();
    } else {
        startup();
    }
}

void setname(string name)
{
    if (name == "new") {
        connection()->prompt(this::newuser, "New username: ");
    } else {
        this.name = name;
        connection()->password(this::setpass, "Password: ");
    }
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
        connection()->write("\n");
        connection()->mode_red();
        connection()->mode_underscore();
        connection()->write("Username or password not recognized.\nConnection terminated.\n");
        connection()->mode_normal();
        connection()->close();
        return false;
    } else {
        return true;
    }
}

void setuname(string uname)
{
    object player = the("/secure/logman.c")->get_player(name);
    player->activate_as(uname);
    object sword = new("/world/lantern.c");
    sword->set_name(uname + "'s lantern");
    sword->set_properly_named();
    sword->add_IDs(uname + "'s");
    sword->move_to(player);
    player->act_goto(the("/world/42/hn/reception.c"));
    set_me(player);
    execappfromcli(this->logout, "/usr/bin/ctrl.c");
}

void startup()
{
    connection()->prompt(this::setuname, "By what name will you be known? ");
}

void logout()
{
    execappfromcli(nil, "/sbin/logout.c");
}

void start()
{
    banner();
    connection()->prompt(this::setname, "What's your intra name? ");
}
