inherit "/std/thing.c";

mapping exits;


void evt_entering(object obj)
{
    broadcast(obj->get_short(), " enters.\n");
}

void evt_leaving(object obj)
{
    broadcast(obj->get_short(), " leaves.\n");
}

void broadcast(...)
{
    for (object obj = get_children(); obj != nil; obj = obj->get_sibling())
    {
        obj->write(...);
    }
}


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
    exits = [];
}
