inherit "/secure/object.c";

bool check_login(string name, string pass)
{
    any users = {
        "enijakow",
        "mhahn"
    };
    
    return member(users, name);
}

void create()
{
}
