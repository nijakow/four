inherits "/std/thing.c";

mapping exits;


object get_exit(string dir)
{
    string result = exits[dir];
    if (result != nil)
        return the(result);
    else
        return nil;
}

void add_exit(string dir, string room)
{
    exits[dir] = room;
}

void create()
{
    "/std/thing.c"::create();
    set_properly_named();
    exits = [];
}
