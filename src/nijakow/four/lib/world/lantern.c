inherits "/std/thing.c";

void create()
{
    "/std/thing.c"=>create();
    set_name("lantern");
    set_desc("A brass lantern. It looks like it's at least a century old.");
    add_IDs("lantern");
    set_brightness(10);
}
