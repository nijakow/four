inherit "/std/thing.c";


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
    return nil;
}

void create()
{
    "/std/thing.c"::create();
}
