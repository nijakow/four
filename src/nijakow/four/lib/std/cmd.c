inherits "/std/cli.c";

object player;
string the_arg;

string arg() { return the_arg; }

object me() { return player; }
object get_location() { return me->get_location(); }


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


void create(object connection, func finish_cb, object player, string arg)
{
    "/std/cli.c"::create(connection, finish_cb);
    this.the_arg = arg;
    this.player = player;
}
