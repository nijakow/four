inherits "/std/cli.c";

string name;
string pass;

void banner()
{
    connection()->write("\n");
    connection()->write("                                  /####/  #################\n");
    connection()->write("                                /####/    #####/     ######\n");
    connection()->write("                              /####/      ###/       ######\n");
    connection()->write("                            /####/        #/         /####/\n");
    connection()->write("                          /####/          /        /####/\n");
    connection()->write("                        /####/                   /####/\n");
    connection()->write("                      /####/                   /####/\n");
    connection()->write("                    /####/                   /####/\n");
    connection()->write("                  /####/                   /####/         /\n");
    connection()->write("                  #####################   ######         /#\n");
    connection()->write("                  #####################   ######       /###\n");
    connection()->write("                  #####################   ######     /#####\n");
    connection()->write("                                 ######   #################\n");
    connection()->write("                                 ######\n");
    connection()->write("                                 ######\n");
    connection()->write("                                 ######\n");
    connection()->write("\n");
    connection()->mode_italic();
    connection()->write("  Welcome to the 42 MUD!\n");
    connection()->mode_normal();
    connection()->write("\n");
}

void setname(string name)
{
    this.name = name;
    connection()->password(this::setpass, "Password: ");
}

void setpass(string pass)
{
    if (!the("/secure/logman.c")->check_login(name, pass)) {
        connection()->write("\n");
        connection()->mode_red();
        connection()->mode_underscore();
        connection()->write("Username or password not recognized.\nConnection terminated.\n");
        connection()->mode_normal();
        connection()->close();
        return;
    }
    connection()->write("\nWelcome to the 42 MUD, ", this.name, "!\n\n");
    connection()->prompt(this::setuname, "By what name will your character be known? ");
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
    player->act_goto(the("/world/void.c"));
    set_me(player);
    execappfromcli(this->logout, "/usr/bin/ctrl.c", "/", {"/usr/bin/ctrl.c"});
}

void logout()
{
    execappfromcli(nil, "/sbin/logout.c", "/", {"/sbin/logout.c"});
}

void start()
{
    banner();
    connection()->prompt(this::setname, "What's your intra name? ");
}
