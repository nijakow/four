inherits "/std/app.c";

void start()
{
    foreach (string s : getmsgs())
        printf("%s\n", s);
    exit();
}
