inherits "/std/cmd.c";

void start()
{
    object loc = get_location()->get_exit(me(), arg());

    if (loc != nil) {
        me()->act_goto(loc);
        lookaround();
    } else {
        connection()->write("There is no exit in this direction!\n");
    }
    exit();
}
