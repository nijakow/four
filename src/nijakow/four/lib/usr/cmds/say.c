inherits "/std/cmd.c";

void start()
{
    me()->act(me()->get_short(), " says: ", arg(), "\n");
    exit();
}
