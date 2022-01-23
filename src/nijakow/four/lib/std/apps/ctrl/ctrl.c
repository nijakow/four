inherits "/std/cli.c";

mapping cmds;
object me;

object get_location()
{
    return me->get_location();
}

void lookaround()
{
    connection()->write("\n");
    if (me->query_light_level_here() == 0) {
        connection()->write("Is is pitch black here.\n");
    } else {
	    connection()->write(get_location()->get_short(), "\n");
	    connection()->write(get_location()->get_desc(), "\n");
	    for (object obj = get_location()->get_children();
	         obj != nil;
	         obj = obj->get_sibling())
	    {
	        if (obj != me)
	            connection()->write(capitalize(obj->get_short()), ".\n");
	    }
    }
}

void cmd_look(string arg)
{
    lookaround();
    resume();
}

void cmd_go(string dir)
{
    object loc = get_location()->get_exit(dir);

    if (loc != nil) {
    	me->act_goto(loc);
    } else {
    	connection()->write("There is no exit in this direction!\n");
    }
    resume();
}

void cmd_say(string text)
{
    me->act(me->get_short(), " says: ", text, "\n");
    resume();
}

void cmd_take_act(object obj)
{
    if (obj == nil)
        connection()->write("There is no such thing here!\n");
    else if (!me->act_take(obj))
        connection()->write("You can't take that!\n");
    else
        connection()->write("Taken.\n");
    resume();
}

void cmd_take(string text)
{
    select_and_call(text, this::cmd_take_act);
}

void cmd_drop_act(object obj)
{
    if (obj == nil)
        connection()->write("There is no such thing here!\n");
    else if (!me->act_drop(obj))
        connection()->write("You can't drop that!\n");
    else
        connection()->write("Dropped.\n");
    resume();
}

void cmd_drop(string text)
{
    select_and_call(text, this::cmd_drop_act);
}

void cmd_examine_act(object obj)
{
    if (obj == nil)
        connection()->write("There is no such thing here!\n");
    else
        connection()->write(obj->get_desc(), "\n");
    resume();
}

void cmd_examine(string text)
{
    select_and_call(text, this::cmd_examine_act);
}

void cmd_inv(string text)
{
    for (object obj = me->get_children();
         obj != nil;
         obj = obj->get_sibling())
    {
        if (obj != me)
            connection()->write(capitalize(obj->get_short()), ".\n");
    }
    resume();
}

void resume_from_shell()
{
    resume();
}

void cmd_shell(string text)
{
    exec("/std/apps/shell/shell.c", connection(), this::resume_from_shell);
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
        connection()->write("I didn't quite get that, sorry...\n");
        resume();
    }
}

void receive(string cmd)
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
    connection->set_fallback(this::resume);
    this.cmds = [];
    this.me = me;
    add_cmd("look", this::cmd_look);
    add_cmd("examine", this::cmd_examine);
    add_cmd("go", this::cmd_go);
    add_cmd("say", this::cmd_say);
    add_cmd("take", this::cmd_take);
    add_cmd("get", this::cmd_take);
    add_cmd("drop", this::cmd_drop);
    add_cmd("inv", this::cmd_inv);
    // add_cmd("new", this::cmd_instantiate);
    // add_cmd("devhint", this::cmd_devhint);
    add_cmd("shell", this::cmd_shell);
}
