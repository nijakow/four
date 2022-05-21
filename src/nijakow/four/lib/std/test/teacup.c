#include "/std/thing.c"

private void _tick()
{
    this.act("418 I'm a teacup.\n");
}

void reset()
{
    "/std/thing.c"::reset();
    set_short("teacup");
    set_long("A teacup lies here.");
    add_id("teacup");
    add_id("cup");
}

void _init()
{
    "/std/thing.c"::_init();
}
