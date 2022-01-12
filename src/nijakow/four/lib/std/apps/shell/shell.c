inherit "/std/cli.c";

void arg_error()
{
    connection()->write("Argument error!\n");
}

void cmd_pwd(list argv)
{
    if (length(argv) == 1)
        connection()->write(pwd(), "\n");
    else
        arg_error();
    resume();
}

void cmd_cd(list argv)
{
    if (length(argv) == 2)
        chdir(argv[1]);
    else
        arg_error();
    resume();
}

void receive(string line)
{
    list argv = split(line);

    if (length(argv) == 0) resume();
    else if (argv[0] == "exit")
        exit();
    else if (argv[0] == "pwd")
        cmd_pwd(argv);
    else if (argv[0] == "cd")
        cmd_cd(argv);
    else {
        connection()->write(argv[0], ": not a command!\n");
        resume();
    }
}

void resume()
{
    connection()->prompt(this::receive, "$ ");
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
