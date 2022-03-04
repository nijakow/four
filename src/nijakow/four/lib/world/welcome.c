inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("The Introductory Room");
    set_desc("Welcome to the MUD!\n"
           + "This room is the place where everbody starts out. There is not much here, "
           + "since the developers didn't have a chance to implement items, weapons, NPCs and "
           + "furniture yet. You may still have a look at the MUD, though.\n"
           + "To do so, please type 'go north'. Also, don't forget to use the 'pic' command ;-)");
    set_img("https://www.dieter-schwarz-stiftung.de/files/Bilder/Aktuelles/2020/2020_03/Banner_42-Heilbronn_Innovationsfabrik_%28c%29-Bernhard-Lattner.jpg", "?x");
    add_exit("north", "/world/42/hn/upstairs.c");
}
