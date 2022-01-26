inherits "/std/room.c";

object go_to(object player, string dir)
{
    player->write("Ouch! You bumped into the ceiling! That hurt!\n");
    return this;
}

void create()
{
    "/std/room.c"::create();
    set_name("The Void");
    set_desc("You are floating in a dark, empty space.");
    add_exit("up", go_to);
    add_exit("north", "/world/cafeteria.c");
}
