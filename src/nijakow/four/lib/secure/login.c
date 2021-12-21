inherit "/secure/object.c";

object connection;

string name;

void banner()
{
    connection->write("\n");
    connection->write("                                  /####/  #################\n");
    connection->write("                                /####/    #####/     ######\n");
    connection->write("                              /####/      ###/       ######\n");
    connection->write("                            /####/        #/         /####/\n");
    connection->write("                          /####/          /        /####/\n");
    connection->write("                        /####/                   /####/\n");
    connection->write("                      /####/                   /####/\n");
    connection->write("                    /####/                   /####/\n");
    connection->write("                  /####/                   /####/         /\n");
    connection->write("                  #####################   ######         /#\n");
    connection->write("                  #####################   ######       /###\n");
    connection->write("                  #####################   ######     /#####\n");
    connection->write("                                 ######   #################\n");
    connection->write("                                 ######\n");
    connection->write("                                 ######\n");
    connection->write("                                 ######\n");
    connection->write("\n");
    connection->mode_italic();
    connection->write("  Welcome to the 42 MUD!\n");
    connection->mode_normal();
    connection->write("\n");
}

void setname(string name)
{
    this.name = name;
    connection->password(this::setpass, "Password: ");
}

void setpass(string pass)
{
    if (the("/secure/logman.c")->check_login(name, pass)) {
        connection->write("Hello ", this.name, "!\n\n");
        object player = the("/secure/logman.c")->get_player(name);
        player->bind(connection);
        player->resume();
    } else {
        connection->write("\n");
        connection->mode_red();
        connection->mode_underscore();
        connection->write("Username or password not recognized.\nConnection terminated.\n");
        connection->mode_normal();
        connection->close();
        return;
    }
}

void create(object the_connection)
{
    connection = the_connection;
    banner();
    connection->prompt(this::setname, "What's your intra name? ");
}
