inherit "/secure/object.c";

object connection;


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

void create(object the_connection)
{
    connection = the_connection;
    banner();
    connection->close();
}
