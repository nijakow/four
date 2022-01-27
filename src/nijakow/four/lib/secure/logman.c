inherits "/secure/object.c";

use $login;


bool login(string name, string password)
{
    return $login(name, password);
}

object get_player(string name)
{
    return new("/std/player.c");
}

void create()
{
    "/secure/object.c"::create();
}
