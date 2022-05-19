#include "/std/thing.c"

private void tick()
{
    this.act("418 I'm a teacup.\n");
}

void reset()
{
    "/std/thing.c"::reset();
    enable_living(this::tick);
    set_short("teacup");
    set_long("A teacup lies here.");
    add_id("teacup");
    add_id("cup");
}

void _init()
{
    "/std/thing.c"::_init();
}
