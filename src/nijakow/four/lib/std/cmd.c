inherits "/std/cli.c";

string the_arg;

string arg() { return the_arg; }

object get_location() { return me()->get_location(); }


void _select_choose(string line, object* objects, func cb)
{
    int choice = atoi(line);

    if ((choice == nil) || (choice < 1) || (choice > length(objects)))
        _select_resume(objects, cb);
    else
        call(cb, objects[choice - 1]);
}

void _select_resume(object* objects, func cb)
{
    printf("\nYour selection is ambiguous:\n");
    for (int i = 0; i < length(objects); i++)
    {
        printf("  %d: %s\n", (i + 1), objects[i].get_long());
    }
    prompt(this::(objects, cb)_select_choose, "Please choose: ");
}

void select_and_call(string text, func cb)
{
    object* objects = me()->find_thing_here(text);
    if (length(objects) == 0) {
        call(cb, nil);
    } else if (length(objects) == 1) {
        call(cb, objects[0]);
    } else {
        _select_resume(objects, cb);
    }
}


void lookaround()
{
    printf("\n");
    if (me()->query_light_level_here() == 0) {
        printf("Is is pitch black here.\n");
    } else {
	    printf("%s\n", get_location()->get_short());
	    printf("%s\n", get_location()->get_desc());
	    for (object obj = get_location()->get_children();
	         obj != nil;
	         obj = obj->get_sibling())
	    {
	        if (obj != me())
	            printf("%s.\n", capitalize(obj->get_short()));
	    }
    }
}


void create(object connection, func finish_cb, object the_me, string arg)
{
    "/std/cli.c"::create(connection, finish_cb, the_me);
    this.the_arg = arg;
}
