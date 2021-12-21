inherit "/std/room.c";

object get_exit(string dir)
{
    if (dir == "down") {
        return the("/world/void.c");
    } else {
        return "/std/room.c"::get_exit(dir);
    }
}

void create()
{
    "/std/room.c"::create();
    set_short("Nijakow's Workroom");
    set_long("This is nijakow's workroom. A large table sits in one corner, dozens of handwritten notes and sheets of paper covering it. You can go down to the void.");
}
