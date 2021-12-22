inherit "/std/thing.c";

void create()
{
    "/std/thing.c"::create();
    set_short("A sword");
    set_long("A curved sword lies here, shining with a bright blue glow.");
    add_names("sword", "blade");
}
