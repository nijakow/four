inherit "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_short("Nijakow's Workroom");
    set_long("This is nijakow's workroom. A large table sits in one corner, dozens of handwritten notes and sheets of paper covering it. You can go down to the void.");
    add_exit("down", "/world/void.c");
}
