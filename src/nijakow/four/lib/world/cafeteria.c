inherit "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_short("The Cafeteria");
    set_long("Rows of tables are standing here side by side, and the loud chattering of the students sounds through the hall.");
    add_exit("south", "/world/void.c");
}
