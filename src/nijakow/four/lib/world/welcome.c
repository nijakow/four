inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("The Introductory Room");
    set_desc("Welcome!");
    add_exit("south", "/world/42/hn/upstairs.c");
}
