inherits "/std/cli.c";

string the_arg;

string arg() { return the_arg; }

object get_location() { return me()->get_location(); }


list select_object_list;
func select_cb;

void _select_choose(string line)
{
    int choice = atoi(line);

    if ((choice == nil) || (choice < 1) || (choice > length(select_object_list)))
        _select_resume();
    else
        call(select_cb, select_object_list[choice - 1]);
}

void _select_resume()
{
    connection()->write("\nYour selection is ambiguous:\n");
    for (int i = 0; i < length(select_object_list); i++)
    {
        connection()->write("  ", (i + 1), ": ", select_object_list[i].get_long(), "\n");
    }
    connection()->prompt(this::_select_choose, "Please choose: ");
}

void select_and_call(string text, func cb)
{
    list objects = me()->find_thing_here(text);
    if (length(objects) == 0) {
        call(cb, nil);
    } else if (length(objects) == 1) {
        call(cb, objects[0]);
    } else {
        select_object_list = objects;
        select_cb          = cb;
        _select_resume();
    }
}


void lookaround()
{
    connection()->write("\n");
    if (me()->query_light_level_here() == 0) {
        connection()->write("Is is pitch black here.\n");
    } else {
        string img = get_location()->get_img();
        if (img != nil) {
            connection()->write("\{^" + img + "\}\n");
        }
	    connection()->write(get_location()->get_short(), "\n");
	    connection()->write(get_location()->get_desc(), "\n");
	    for (object obj = get_location()->get_children();
	         obj != nil;
	         obj = obj->get_sibling())
	    {
	        if (obj != me())
	            connection()->write(capitalize(obj->get_short()), ".\n");
	    }
    }
}


void create(object connection, func finish_cb, object the_me, string arg)
{
    "/std/cli.c"::create(connection, finish_cb, the_me);
    this.the_arg = arg;
}
