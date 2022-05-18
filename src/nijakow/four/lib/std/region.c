#include "/std/thing.c"

void reset()
{
    "/std/thing.c"::reset();
    enable_opaque();
    enable_container();
}

void _init()
{
    "/std/thing.c"::_init();
}
