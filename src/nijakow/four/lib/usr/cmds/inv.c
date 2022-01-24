inherits "/std/cmd.c";

void start()
{
    for (object obj = me()->get_children();
         obj != nil;
         obj = obj->get_sibling())
    {
        if (obj != me())
            connection()->write(capitalize(obj->get_short()), ".\n");
    }
    exit();
}
