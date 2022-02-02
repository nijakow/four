inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Reception @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/d/42-heilbronn-Campus_paceship_reception-29a146bc.jpg", "?x");
    set_desc("Text follows.");
    add_exit("north", "/world/42/hn/downstairs.c");
    add_exit("south", "/world/42/hn/diningroom.c");
}
