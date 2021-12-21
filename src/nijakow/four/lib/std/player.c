inherit "/std/thing.c";

object connection;
bool say_room;


void write(...)
{
    if (connection != nil)
        connection->write(...);
}


object get_location() { return get_parent(); }

void go_to(object location)
{
    object current = get_location();
    
    if (location != nil)
        location->evt_entering(this);
    move_to(location);
    if (current != nil)
        current->evt_leaving(this);
    say_room = 1;
}


void lookaround()
{
    connection->write("\n");
    connection->write(get_location()->get_short(), "\n");
    connection->write(get_location()->get_long(), "\n");
    for (object obj = get_location()->get_children();
         obj != nil;
         obj = obj->get_sibling())
    {
        if (obj != this)
            connection->write(obj->get_short(), ".\n");
    }
}

void cmd_go(string dir)
{
    object loc = get_location()->get_exit(dir);
    
    if (loc != nil)
    	go_to(loc);
    else
    	connection->write("There is no exit in this direction!\n");
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
    
    if (cmd == "") {
    } else if (cmd == "look") {
        lookaround();
    } else if (cmd == "go") {
        cmd_go(args);
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
    go_to(nil);
    log("Player requested logout.\n");
    connection->write("Goodbye!\n");
    connection->close();
}

void bind(object connection)
{
    this.connection = connection;
}

bool inhibit_create_on_init() { return 1; }

void create()
{
    "/std/thing.c"::create();
    connection = nil;
    say_room = 1;
    go_to(the("/world/void.c"));
}
