inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Dining Room @ 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/f/42-heilbronn-Campus_Spaceship_pflanzen_wand-f61d51dc.jpg", "400x");
    set_desc("Text follows.");
    add_exit("north", "/world/42/hn/reception.c");
}
