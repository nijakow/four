inherits "/std/app.c";

void start()
{
    if (me() != nil) {
        me()->move_to(nil); // TODO: "Freeze" the player as a statue?
    }
    printf("Goodbye! :)\n");
    connection()->close();
}
