#include "/std/room.c"

use $log;

void reset()
{
    "/std/room.c"::reset();
    set_short("The Workroom");
    set_desc("This is the workroom.");
}

void _init()
{
    "/std/room.c"::_init();
}
