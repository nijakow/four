inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Cluster 2 @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/2/42-heilbronn-Campus_Spaceship_cluster_3-afc09b24.jpg", "400x");
    set_desc("Text follows.");
    add_exit("east", "/world/42/hn/upstairs.c");
    add_exit("exit", "/world/42/hn/upstairs.c");
}
