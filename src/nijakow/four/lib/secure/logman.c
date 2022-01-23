inherits "/secure/object.c";

bool check_login(string name, string pass)
{
    list users = {
        "enijakow",
        "mhahn"
    };

    return member(users, name);
}

object get_player(string name)
{
    return new("/std/player.c");
}

void create()
{
    "/secure/object.c"::create();
}
