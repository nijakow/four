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
    connection->write("Hello ", this.name, "!\n");
}

void create(object the_connection)
{
    connection = the_connection;
    banner();
    connection->prompt(this::setname, "What's your intra name? ");
}
