inherits "/std/app.c";

void start()
{
    if (me() != nil)
        me()->freeze();
    printf("Goodbye! :)\n");
    connection()->close();
}
