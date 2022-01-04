inherit "/std/room.c";


void create()
{
    "/std/room.c"::create();
    set_name("The Void");
    set_desc("You are floating in a dark, empty space.");
    add_exit("up", "/realms/nijakow/workroom.c");
    add_exit("north", "/world/cafeteria.c");
}
