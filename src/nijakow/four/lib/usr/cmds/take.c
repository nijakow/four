inherits "/std/cmd.c";

void cmd_take_act(object obj)
{
    if (obj == nil)
        connection()->write("There is no such thing here!\n");
    else if (!me()->act_take(obj))
        connection()->write("You can't take that!\n");
    else
        connection()->write("Taken.\n");
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_take_act);
}
