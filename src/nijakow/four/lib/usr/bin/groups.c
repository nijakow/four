inherits "/std/app.c";

string listgroups(string user)
{
    printf("%s: ", user);
    foreach (string id : getgroups())
    {
        foreach (string uid : getmembers(id))
        {
            if (uid == user) {
                printf("%s ", id);
                continue;
            }
        }
    }
    printf("\n");
}

void start()
{
    if (length(argv) == 1)
        listgroups(getuid());
    else {
        for (int x = 1; x < length(argv); x++)
            listgroups(argv[x]);
    }
    exit();
}
