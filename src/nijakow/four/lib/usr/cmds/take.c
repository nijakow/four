inherits "/std/cmd.c";

void cmd_take_act(object obj)
{
    if (obj == nil)
        printf("There is no such thing here!\n");
    else if (!me()->act_take(obj))
        printf("You can't take that!\n");
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_take_act);
}
