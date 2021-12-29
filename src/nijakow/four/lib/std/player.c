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
    if (query_light_level_here() == 0) {
        connection->write("Is is pitch black here.\n");
    } else {
	    connection->write(get_location()->get_short(), "\n");
	    connection->write(get_location()->get_long(), "\n");
	    for (object obj = get_location()->get_children();
	         obj != nil;
	         obj = obj->get_sibling())
	    {
	        if (obj != this)
	            connection->write(capitalize(obj->get_short()), ".\n");
	    }
    }
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
    } else if (cmd == "take" || cmd == "get") {
        cmd_take(args);
    } else if (cmd == "inv") {
        cmd_inv(args);
    } else if (cmd == "examine") {
        cmd_examine(args);
    } else if (cmd == "drop") {
        cmd_drop(args);
    } else if (cmd == "exit") {
        exit();
        return;
    } else if (cmd == "edit") {
        cmd_edit_file(args);
    } else if (cmd == "recompile") {
        cmd_recompile_file(args);
    } else if (cmd == "new") {
        cmd_instantiate(args);
    } else {
        connection->write("I didn't quite get that, sorry...\n");
    }
    resume();
}

use $filetext;
use $filetext_set;

string editPath;

void cmd_write_file(string id, string text)
{
    if (text != nil) {
        connection->mode_italic();
        if(!$filetext_set(editPath, text)) {
            connection->mode_red();
            connection->write("Could not write \"", editPath, "\"!\n");
        } else {
            connection->mode_green();
            connection->write("\"", editPath, "\" written.\n");
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
    editPath = text;
    string content = $filetext(text);
    if (content == nil) {
        if ($touch(text))
            content = "";
    }
    if (content != nil)
        connection->edit(this::cmd_write_file, text, content);
    else {
        connection->mode_red();
        connection->mode_italic();
        connection->write("Could not read \"", text, "\"!\n");
        connection->mode_normal();
    }
}

use $recompile;

void cmd_recompile_file(string text)
{
    if (!$recompile(trim(text))) {
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
        connection->write("You summon ", obj->get_short(), ".");
        me_act("summons ", obj->get_short(), ".");
    } else {
        connection->write("Could not create an instance!\n");
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
    resume();
}

bool inhibit_create_on_init() { return 1; }

void activate_as(string name)
{
    set_name(name);
    add_names(name);
    act_goto(the("/world/void.c"));
}

void create()
{
    "/std/thing.c"::create();
    set_properly_named();
    connection = nil;
    say_room = true;
    log("Initializing the player ", this, "\n");
}
