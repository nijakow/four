inherits "/std/cmd.c";

void cmd_examine_act(object obj)
{
    if (obj == nil)
        printf("You can't examine that!\n");
    else
        printf("%s\n", obj->get_desc());
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_examine_act);
}
