inherits "/std/cmd.c";

void go_west()
{
    printf("(%s)\n", select_random({
        "life is peaceful there",
        "in the open air",
        "where the skies are blue",
        "this is what we're gonna do",
        "sun in wintertime"
    }));
}

void start()
{
    if (arg() == "west") go_west();

    object current_loc = get_location();
    object loc         = current_loc->get_exit(me(), arg());

    if (loc == current_loc) {
    } else if (loc == nil) {
        printf("There is no exit in this direction!\n");
    } else {
        me()->act_goto(loc);
        lookaround();
    }
    exit();
}
