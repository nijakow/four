inherit "/std/thing.c";

void create()
{
    "/std/thing.c"::create();
    set_name("lantern");
    set_long("A brass lantern. It looks like it's at least a century old.");
    add_names("lantern");
    set_brightness(10);
}
