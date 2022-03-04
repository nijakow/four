inherits "/std/cmd.c";

void cmd_drop_act(object obj)
{
    if (obj == nil)
        printf("There is no such thing here!\n");
    else if (!me()->act_drop(obj))
        printf("You can't drop that!\n");
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_drop_act);
}
