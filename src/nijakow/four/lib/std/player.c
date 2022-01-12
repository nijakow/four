inherit "/std/thing.c";

object connection;
mapping cmds;
bool say_room;


void write(...)
{
    if (connection != nil)
        connection->write(...);
}


list select_object_list;
func select_cb;

void _select_choose(string line)
{
    int choice = atoi(line);

    if ((choice == nil) || (choice < 1) || (choice > length(select_object_list)))
        _select_resume();
    else
        call(select_cb, select_object_list[choice - 1]);
}

void _select_resume()
{
    connection->write("\nYour selection is ambiguous:\n");
    for (int i = 0; i < length(select_object_list); i++)
    {
        connection->write("  ", (i + 1), ": ", select_object_list[i].get_long(), "\n");
    }
    connection->prompt(this::_select_choose, "Please choose: ");
}

void select_and_call(string text, func cb)
{
    list objects = find_thing_here(text);
    if (length(objects) == 0) {
        call(cb, nil);
    } else if (length(objects) == 1) {
        call(cb, objects[0]);
    } else {
        select_object_list = objects;
        select_cb          = cb;
        _select_resume();
    }
}


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

void cmd_look(string text)
{
    lookaround();
    resume();
}

void cmd_go(string dir)
{
    object loc = get_location()->get_exit(dir);

    if (loc != nil) {
    	act_goto(loc);
    	say_room = true;
    } else {
    	connection->write("There is no exit in this direction!\n");
    }
    resume();
}

void cmd_say(string text)
{
    act(get_short(), " says: ", text, "\n");
    resume();
}

void cmd_take_act(object obj)
{
    if (obj == nil)
        connection->write("There is no such thing here!\n");
    else if (!act_take(obj))
        connection->write("You can't take that!\n");
    else
        connection->write("Taken.\n");
    resume();
}

void cmd_take(string text)
{
    select_and_call(text, this::cmd_take_act);
}

void cmd_drop(string text)
{
    list objects = find_thing(text);
    if (length(objects) == 0) {
        connection->write("There is no such thing here!\n");
    } else if (!act_drop(objects[0])) {
        connection->write("You can't drop that!\n");
    } else {
        connection->write("Dropped.\n");
    }
    resume();
}

void cmd_examine(string text)
{
    list objects = find_thing_here(text);
    if (length(objects) == 0) {
        connection->write("There is no such thing here!\n");
    } else {
        connection->write(objects[0]->get_desc(), "\n");
    }
    resume();
}

void cmd_inv(string text)
{
    for (object obj = get_children();
         obj != nil;
         obj = obj->get_sibling())
    {
        if (obj != this)
            connection->write(capitalize(obj->get_short()), ".\n");
    }
    resume();
}

void cmd_instantiate(string text)
{
    object obj = new(text);
    if (obj != nil) {
        obj->move_to(get_location());
        connection->write("You summon ", obj->get_short(), ".\n");
        me_act("summons ", obj->get_short(), ".\n");
    } else {
        connection->write("Could not create an instance!\n");
    }
    resume();
}

void cmd_shell(string text)
{
    new("/std/apps/shell/shell.c", connection, this::resume)->start();
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

    func f = cmds[cmd];

    if (f != nil) {
        call(f, args);
    } else if (cmd == "") {
        resume();
    } else if (cmd == "exit") {
        exit();
        return;
    } else {
        connection->write("I didn't quite get that, sorry...\n");
        resume();
    }
}

void resume()
{
    if (say_room) {
        lookaround();
        say_room = false;
    }
    connection->prompt(this::docmd, "> ");
}

void add_cmd(string name, func cb)
{
    cmds[name] = cb;
}

void exit()
{
    act_goto(nil);
    log("Player requested logout.\n");
    connection->write("Goodbye!\n");
    connection->close();
}

void start(object connection)
{
    this.connection = connection;
    connection->set_fallback(this::resume);
    resume();
}

void activate_as(string name)
{
    set_name(name);
    add_IDs(name);
    act_goto(the("/world/void.c"));
}

void create()
{
    "/std/thing.c"::create();

    set_properly_named();
    connection = nil;
    cmds = [];
    say_room = true;

    add_cmd("look", this::cmd_look);
    add_cmd("examine", this::cmd_examine);
    add_cmd("go", this::cmd_go);
    add_cmd("say", this::cmd_say);
    add_cmd("take", this::cmd_take);
    add_cmd("get", this::cmd_take);
    add_cmd("drop", this::cmd_drop);
    add_cmd("inv", this::cmd_inv);
    add_cmd("new", this::cmd_instantiate);
    add_cmd("shell", this::cmd_shell);
}
