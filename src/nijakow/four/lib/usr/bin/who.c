inherits "/std/app.c";

void start()
{
    foreach (string id : getmembers("users"))
    {
        if (isactive(id))
            printf("%s\n", id);
    }
    exit();
}
