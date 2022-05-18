#include "/lib/object.c"
#include "/lib/list/list.c"

#include "/std/thing/commands.c"
#include "/std/thing/tree.c"
#include "/std/thing/name.c"
#include "/std/thing/events.c"
#include "/std/thing/container.c"

/*
 *     C o n s t r u c t o r   S e c t i o n
 */
void reset()
{
    "/std/thing/tree.c"::reset();
    "/std/thing/name.c"::reset();
    "/std/thing/commands.c"::reset();
    "/std/thing/events.c"::reset();
    "/std/thing/container.c"::reset();
}

void _init()
{
    "/lib/object.c"::_init();
    "/std/thing/tree.c"::_init();
    "/std/thing/name.c"::_init();
    "/std/thing/commands.c"::_init();
    "/std/thing/events.c"::_init();
    "/std/thing/container.c"::_init();
    reset();
}
