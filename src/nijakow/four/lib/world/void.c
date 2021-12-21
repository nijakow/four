inherit "/std/room.c";


object get_exit(string dir)
{
    if (dir == "up") {
        return the("/realms/nijakow/workroom.c");
    } else {
        return "/std/room.c"::get_exit(dir);
    }
}

void create()
{
    "/std/room.c"::create();
    set_short("The Void");
    set_long("You are floating in a dark, empty space.");
}
