inherit "/std/thing.c";

void create()
{
    "/std/thing.c"::create();
    set_name("sword");
    set_long("A curved sword. Good for slaying monsters.");
    add_names("sword", "blade");
}
