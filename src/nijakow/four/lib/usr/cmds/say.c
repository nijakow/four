inherits "/std/cmd.c";

void start()
{
    me()->act_say(arg());
    exit();
}
