inherit "/std/thing.c";

object connection;
mapping cmds;
bool say_room;


void write(...)
{
    if (connection != nil)
        connection->write(...);
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
}

void cmd_go(string dir)
{
    object loc = get_location()->get_exit(dir);
    
    if (loc != nil) {
    	act_goto(loc);
    	say_room = true;
    } else
    	connection->write("There is no exit in this direction!\n");
}

void cmd_say(string text)
{
    act(get_short(), " says: ", text, "\n");
}

void cmd_take(string text)
{
    list objects = find_thing_here(text);
    if (length(objects) == 0) {
        write("There is no such thing here!\n");
    } else if (!act_take(objects[0])) {
        write("You can't take that!\n");
    } else {
        write("Taken.\n");
    }
}

void cmd_drop(string text)
{
    list objects = find_thing(text);
    if (length(objects) == 0) {
        write("There is no such thing here!\n");
    } else if (!act_drop(objects[0])) {
        write("You can't drop that!\n");
    } else {
        write("Dropped.\n");
    }
}

void cmd_examine(string text)
{
    list objects = find_thing_here(text);
    if (length(objects) == 0) {
        write("There is no such thing here!\n");
    } else {
        write(objects[0]->get_long(), "\n");
    }
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
}

use $filetext;
use $filetext_set;

mapping mapped_pathnames;

void cmd_write_file(any id, string text)
{
    string path = mapped_pathnames[id];
    mapped_pathnames[id] = nil;
    if (path != nil && text != nil) {
        connection->mode_italic();
        if(!$filetext_set(path, text)) {
            connection->mode_red();
            connection->write("Could not write \"", path, "\"!\n");
        } else {
            connection->mode_green();
            connection->write("\"", path, "\" written.\n");
        }
        connection->mode_normal();
    }
}

use $touch;

void cmd_edit_file(string text)
{
    /*
     * TODO: Check if the requested file can be edited by the current user.
     *       Maybe also disable editing of special security files.
     * - mhahnFr
     */
    // TODO: write callback function in stdlib
    string content = $filetext(text);
    if (content == nil) {
        if ($touch(text))
            content = "";
    }
    if (content != nil) {
        any id = connection->edit(this::cmd_write_file, text, content);
        mapped_pathnames[id] = text;
    } else {
        connection->mode_red();
        connection->mode_italic();
        connection->write("Could not read \"", text, "\"!\n");
        connection->mode_normal();
    }
}

use $recompile;

void cmd_recompile_file(string text)
{
    string file = trim(text);
    if ($recompile(file)) {
        the(file);  // This automatically reinitializes the file
    } else {
        connection->mode_red();
        connection->mode_italic();
        connection->write("Could not recompile \"", text, "\"!\n");
        connection->mode_normal();
    }
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
    log("Initializing the player ", this, "\n");
    "/std/thing.c"::create();

    set_properly_named();
    connection = nil;
    cmds = [];
    say_room = true;
    mapped_pathnames = [];

    add_cmd("look", this::cmd_look);
    add_cmd("examine", this::cmd_examine);
    add_cmd("go", this::cmd_go);
    add_cmd("say", this::cmd_say);
    add_cmd("take", this::cmd_take);
    add_cmd("get", this::cmd_take);
    add_cmd("drop", this::cmd_drop);
    add_cmd("inv", this::cmd_inv);
    add_cmd("edit", this::cmd_edit_file);
    add_cmd("recompile", this::cmd_recompile_file);
    add_cmd("new", this::cmd_instantiate);
}
