inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Reception @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/4/42-heilbronn-Campus_Spaceship_Flur-b8fc69fd.jpg", "400x");
    set_desc("Text follows.");
    add_exit("down", "/world/42/hn/downstairs.c");
    add_exit("downstairs", "/world/42/hn/downstairs.c");
    add_exit("east", "/world/42/hn/kitchen2.c");
    add_exit("west", "/world/42/hn/cluster2.c");
    add_exit("south", "/world/42/hn/cluster3.c");
}
