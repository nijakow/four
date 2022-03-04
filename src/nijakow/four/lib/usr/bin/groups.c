inherits "/std/app.c";

void start()
{
    foreach (string id : getgroups())
        printf("%s\n", gname(id));
    exit();
}
