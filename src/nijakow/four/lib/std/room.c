inherits "/std/thing.c";
inherits "/std/container.c";

mapping exits;


object get_exit(object player, string dir)
{
    string result = exits[dir];
    string tp     = type(result);

    while (tp == "function") {
        result = call(result, player, dir);
        tp     = type(result);
    }

    if (tp == "string")
        return the(result);
    else if (tp == "object")
        return result;
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
    "/std/container.c"::create();
    set_properly_named();
    exits = [];
}
