inherits "/std/app.c";

void start()
{
    me()->move_to(nil); // TODO: "Freeze" the player as a statue?
    printf("Goodbye! :)\n");
    connection()->close();
}
