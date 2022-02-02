inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Cluster 3 @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/e/42-heilbronn-Campus_Spaceship_cluster-345f2b4b.jpg", "?x");
    set_desc("Text follows.");
    add_exit("north", "/world/42/hn/upstairs.c");
    add_exit("exit", "/world/42/hn/upstairs.c");
}
