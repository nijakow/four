inherit "/secure/object.c";
inherit "/std/thing/movement.c";
inherit "/std/thing/descriptions.c";
inherit "/std/thing/actions.c";
inherit "/std/thing/light.c";


/*
 *    M i s c e l l a n e o u s
 */

void write(...) {}


void create()
{
    "/secure/object.c"::create();
    "/std/thing/movement.c"::create();
    "/std/thing/descriptions.c"::create();
    "/std/thing/actions.c"::create();
    "/std/thing/light.c"::create();
}

void _init()
{
    "/secure/object.c"::_init();
    create();
}
