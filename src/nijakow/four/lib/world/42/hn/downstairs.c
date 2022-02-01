inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Downstairs @ 42 Heilbronn");
    set_desc("Text follows.");
    add_exit("west", "/world/42/hn/cluster1.c");
    add_exit("south", "/world/42/hn/reception.c");
    add_exit("up", "/world/42/hn/upstairs.c");
    add_exit("upstairs", "/world/42/hn/upstairs.c");
}
