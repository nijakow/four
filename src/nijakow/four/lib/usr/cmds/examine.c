inherits "/std/cmd.c";

void cmd_examine_act(object obj)
{
    if (obj == nil)
        connection()->write("You can't examine that!\n");
    else
        connection()->write(obj->get_desc(), "\n");
    exit();
}

void start()
{
    select_and_call(arg(), this::cmd_examine_act);
}
