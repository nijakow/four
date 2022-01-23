inherits "/std/thing.c";

void create()
{
    "/std/thing.c"=>create();
    set_name("sword");
    set_desc("A curved sword. Good for slaying monsters.");
    add_IDs("sword", "blade");
}
