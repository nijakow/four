inherit "/std/cli.c";

void arg_error()
{
    connection()->write("Argument error!\n");
}

void file_not_found_error()
{
    connection()->write("File not found!\n");
}

void cmd_pwd(list argv)
{
    if (length(argv) != 1)
        arg_error();
    else
        connection()->write(pwd(), "\n");
    resume();
}

void cmd_cd(list argv)
{
    if (length(argv) != 2)
        arg_error();
    else {
        string newpwd = resolve(pwd(), argv[1]);
        if (newpwd != nil && is_dir(newpwd)) {
            chdir(newpwd);
        } else {
            file_not_found_error();
        }
    }
    resume();
}

void cmd_ls(list argv)
{
    list files;

    if (length(argv) == 1) {
        files = ls(pwd());
        if (files != nil)
            foreach (string name : files)
                connection()->write(name, "\n");
    } else if (length(argv) == 2) {
        files = ls(resolve(pwd(), argv[1]));
        if (files != nil)
            foreach (string name : files)
                connection()->write(name, "\n");
    } else {
        arg_error();
    }
    resume();
}

void cmd_cat(list argv)
{
    for (int i = 1; i < length(argv); i++)
    {
        string the_path = resolve(pwd(), argv[i]);
        if (the_path == nil) {
            arg_error();
            break;
        } else {
            connection()->write(cat(the_path));
        }
    }
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
    else if (argv[0] == "ls")
        cmd_ls(argv);
    else if (argv[0] == "cat")
        cmd_cat(argv);
    else {
        connection()->write(argv[0], ": not a command!\n");
        resume();
    }
}

void resume()
{
    string ps1 = pwd();
    if (ps1 != nil) ps1 = ps1 + " $ ";
    else ps1 = "$ ";
    connection()->prompt(this::receive, ps1);
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
