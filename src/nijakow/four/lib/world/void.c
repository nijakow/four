inherit "/std/room.c";


void create()
{
    "/std/room.c"::create();
    set_short("The Void");
    set_long("You are floating in a dark, empty space.");
    add_exit("up", "/realms/nijakow/workroom.c");
}
