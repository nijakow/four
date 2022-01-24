inherits "/secure/object.c";
inherits "/std/thing/movement.c";
inherits "/std/thing/descriptions.c";
inherits "/std/thing/actions.c";
inherits "/std/thing/light.c";


/*
 *    M i s c e l l a n e o u s
 */

void write(...) {}

bool query_is_container() { return false; }

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
