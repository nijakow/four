inherit "/std/thing.c";

mapping exits;

/* Deprecated, marked for deletion
void broadcast(...)
{
    for (object obj = get_children(); obj != nil; obj = obj->get_sibling())
    {
        obj->write(...);
    }
}
*/

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
