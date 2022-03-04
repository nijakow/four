inherits "/secure/object.c";

use $login;
mapping players;

bool login(string name, string password)
{
    return $login(name, password);
}

object get_player(string name)
{
    object player = nil;
    if (name != "guest")
        player = players[name];
    if (player == nil) {
        player = new("/std/player.c");
        players[name] = player;
    }
    return player;
}

void _init()
{
    "/secure/object.c"::_init();
    this.players = [];
}
