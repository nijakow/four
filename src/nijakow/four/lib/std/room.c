#include "/std/thing.c"

private mapping room_exits;

object query_room_exit(object player, string direction)
{
    return this.room_exits[direction];
}

void add_exit(string direction, object exit)
{
    this.room_exits[direction] = exit;
}

void reset()
{
    "/std/thing.c"::reset();
    enable_container();
    this.room_exits = [];
}

void _init()
{
    "/std/thing.c"::_init();
}
