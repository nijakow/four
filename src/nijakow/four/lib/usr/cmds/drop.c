inherits "/std/cmd.c";

void cmd_drop_act(object obj)
{
    if (obj == nil)
        connection()->write("There is no such thing here!\n");
    else if (!me()->act_drop(obj))
        connection()->write("You can't drop that!\n");
    else
        connection()->write("Dropped.\n");
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_drop_act);
}
