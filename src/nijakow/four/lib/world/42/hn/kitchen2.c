inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Kitchen 2 @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/8/42-heilbronn-Campus_Spaceship_Kueche-64e16c82.jpg", "400x");
    set_desc("Text follows.");
    add_exit("west", "/world/42/hn/upstairs.c");
    add_exit("exit", "/world/42/hn/upstairs.c");
}
