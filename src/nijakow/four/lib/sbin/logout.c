inherits "/std/app.c";

void start()
{
    me()->move_to(nil); // TODO: "Freeze" the player as a statue?
    connection()->write("Goodbye! :)\n");
    connection()->close();
}
