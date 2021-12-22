inherit "/std/thing.c";

object connection;
bool say_room;


void write(...)
{
    if (connection != nil)
        connection->write(...);
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
    
    if (loc != nil) {
    	go_to(loc);
    	say_room = true;
    } else
    	connection->write("There is no exit in this direction!\n");
}

void cmd_say(string text)
{
    act(get_short(), " says: ", text, "\n");
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
    } else if (cmd == "say") {
        cmd_say(args);
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
        say_room = false;
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

void start(object connection)
{
    this.connection = connection;
    resume();
}

bool inhibit_create_on_init() { return 1; }

void activate_as(string name)
{
    set_short(name);
    go_to(the("/world/void.c"));
}

void create()
{
    "/std/thing.c"::create();
    connection = nil;
    say_room = true;
}
