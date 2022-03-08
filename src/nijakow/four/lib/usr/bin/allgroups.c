inherits "/std/app.c";

void start()
{
    foreach (string id : getgroups())
        printf("%s ", gname(id));
    printf("\n");
    exit();
}
