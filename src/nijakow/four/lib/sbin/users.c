inherits "/std/app.c";

void list_users(string group)
{
    if (!findgroup(group)) {
        printf("! error: group %s not found!\n", group);
    } else {
        printf("%s: ", group);
        foreach (string id : getmembers(group))
            printf("%s ", uname(id));
        printf("\n");
    }
}

void start()
{
    if (length(argv) == 1)
        list_users("users");
    else {
        for (int i = 1; i < length(argv); i++)
            list_users(argv[i]);
    }
    exit();
}
