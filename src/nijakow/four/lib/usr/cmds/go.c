inherits "/std/cmd.c";

void start()
{
    object current_loc = get_location();
    object loc         = current_loc->get_exit(me(), arg());

    if (loc == current_loc) {
    } else if (loc == nil) {
        connection()->write("There is no exit in this direction!\n");
    } else {
        me()->act_goto(loc);
        lookaround();
    }
    exit();
}
