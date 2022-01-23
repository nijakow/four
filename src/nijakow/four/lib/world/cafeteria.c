inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("The Cafeteria");
    set_desc("Rows of tables are standing here side by side, and the loud chattering of the students sounds through the hall.");
    add_exit("south", "/world/void.c");
}
