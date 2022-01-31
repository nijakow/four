inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Cluster 1 @ 42 Heilbronn");
    set_desc("Text follows.");
    add_exit("east", "/world/42/hn/downstairs.c");
    add_exit("exit", "/world/42/hn/downstairs.c");
}
