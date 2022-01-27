inherits "/std/app.c";

void start()
{
    connection()->write("Goodbye! :)\n");
    connection()->close();
}
