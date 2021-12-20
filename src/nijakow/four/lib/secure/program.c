inherit "/secure/object.c";

object connection;
object chatroom;

string name;

void write(...) { connection->write(...); }

void banner()
{
    write("\n");
    write("                /####/  #################\n");
    write("              /####/    #####/     ######\n");
    write("            /####/      ###/       ######\n");
    write("          /####/        #/         /####/\n");
    write("        /####/          /        /####/\n");
    write("      /####/                   /####/\n");
    write("    /####/                   /####/\n");
    write("  /####/                   /####/\n");
    write("/####/                   /####/         /\n");
    write("#####################   ######         /#\n");
    write("#####################   ######       /###\n");
    write("#####################   ######     /#####\n");
    write("               ######   #################\n");
    write("               ######\n");
    write("               ######\n");
    write("               ######   #\n");
    write("   _                    #  #  #  #==  #  #    #==\\   #==,  *==*  #\\\\  #  #\\\\  #\n");
    write("  |c| nijakow, mhahnFr  #  #==#  #=   #  #    #==<   #=*   #  #  # \\\\ #  # \\\\ #\n");
    write("  '-'                   #  #  #  #==  #  #==  #==/   # \\\\  *==*  #  \\\\#  #  \\\\#\n");
    write("                        #\n");
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
    banner();
    chatroom = the_chatroom;
    object editor = new("/prg/edit/editor.c", connection, this::resume, "");
    editor->start();
}
