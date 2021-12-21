inherit "/std/thing.c";

object connection;
bool say_room;


object get_location() { return get_parent(); }


void lookaround()
{
    connection->write(get_location()->get_short(), "\n");
    connection->write(get_location()->get_long(), "\n");
}


void docmd(string cmd)
{
    string args;
    
    int i = indexof(cmd, ' ');
    
    if (i != -1) {
        args = substr(cmd, i + 1, strlen(cmd));
        cmd = substr(cmd, 0, i);
    } else {
        args = "";
    }
    
    if (cmd == "look") {
        lookaround();
    } else if (cmd == "exit") {
        exit();
        return;
    } else {
        connection->write("I didn't quite get that, sorry...\n");
    }
    resume();
}

void resume()
{
    if (say_room) {
        lookaround();
        say_room = 0;
    }
    connection->prompt(this::docmd, "> ");
}

void exit()
{
    log("Player requested logout.\n");
    connection->write("Goodbye!\n");
    connection->close();
}

void bind(object connection)
{
    this.connection = connection;
}

void create()
{
    "/std/thing.c"::create();
    move_to(the("/world/void.c"));
    say_room = 1;
}
