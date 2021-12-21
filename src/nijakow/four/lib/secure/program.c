inherit "/secure/object.c";

object connection;
object chatroom;

string name;

void write(...) { connection->write(...); }

void banner()
{
    write("\n");
    write("                                  /####/  #################\n");
    write("                                /####/    #####/     ######\n");
    write("                              /####/      ###/       ######\n");
    write("                            /####/        #/         /####/\n");
    write("                          /####/          /        /####/\n");
    write("                        /####/                   /####/\n");
    write("                      /####/                   /####/\n");
    write("                    /####/                   /####/\n");
    write("                  /####/                   /####/         /\n");
    write("                  #####################   ######         /#\n");
    write("                  #####################   ######       /###\n");
    write("                  #####################   ######     /#####\n");
    write("                                 ######   #################\n");
    write("                                 ######\n");
    write("                                 ######\n");
    write("                                 ######\n");
    write("\n");
    connection->mode_italic();
    write("  Welcome to the 42 MUD!\n");
    connection->mode_normal();
    write("\n");
}

void msg(string text)
{
	chatroom->broadcast("[", name, "]: ", text, "\n");
	connection->prompt(this::msg, "> ");
}

void setname(string _name)
{
    name = _name;
    chatroom->add_subscriber(this);
    connection->prompt(this::msg, "> ");
}

void resume()
{
    connection->prompt(this::setname, "What's your name? ");
}

void create(any the_connection, any the_chatroom)
{
    connection = the_connection;
    chatroom = the_chatroom;
    banner();
    resume();
}
