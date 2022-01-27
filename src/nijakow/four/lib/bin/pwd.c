inherits "/std/app.c";

void start()
{
    connection()->write(pwd(), "\n");
    exit();
}
