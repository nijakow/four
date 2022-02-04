inherits "/std/app.c";

void start()
{
    foreach (string id : getusers())
        printf("%s\n", uname(id));
    exit();
}
