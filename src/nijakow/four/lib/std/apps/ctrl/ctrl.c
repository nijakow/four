inherits "/std/cli.c";

mapping cmds;
object me;

void lookaround()
{
    connection->write("\n");
    if (query_light_level_here() == 0) {
        connection->write("Is is pitch black here.\n");
    } else {
	    connection->write(get_location()->get_short(), "\n");
	    connection->write(get_location()->get_desc(), "\n");
	    for (object obj = get_location()->get_children();
	         obj != nil;
	         obj = obj->get_sibling())
	    {
	        if (obj != this)
	            connection->write(capitalize(obj->get_short()), ".\n");
	    }
    }
}

void cmd_look(string arg)
{
    lookaround();
    resume();
}

void docmd(string cmd, string args)
{
    func f = cmds[cmd];

    if (f != nil) {
        call(f, args);
    } else if (cmd == "") {
        resume();
    } else if (cmd == "exit") {
        exit();
    } else {
        connection->write("I didn't quite get that, sorry...\n");
        resume();
    }
}

void receive(string line)
{
    string args;

    int i = indexof(cmd, ' ');

    if (i != -1) {
        args = substr(cmd, i + 1, strlen(cmd));
        cmd = substr(cmd, 0, i);
    } else {
        args = "";
    }

    docmd(cmd, args);
}

void resume()
{
    connection()->prompt(this::receive, "> ");
}

void add_cmd(string name, func cb)
{
    cmds[name] = cb;
}

void create(object connection, func finish_cb, object me)
{
    "/std/cli.c"::create(connection, finish_cb);
    this.cmds = [];
    this.me = me;
    add_cmd("look", this::cmd_look);
}
