inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/string/split.c";

private void execute_command_in_path(string* argv, string path)
{
    string result;
    string* paths;

    paths = String_SplitOnChar(path, ':');
    // TODO: Resolve path from PWD
    if (exec(restart_from_binary, argv[0], argv))
        return;
    foreach (string pth : paths)
    {
        if (exec(restart_from_binary, pth + "/" + argv[0], argv))
            return;
    }
    printf("%s: not found!\n", argv[0]);
    restart();
}

private void execute_command(string* argv)
{
    execute_command_in_path(argv, "/bin:/sbin:/usr/bin/");
}

private void receive(string line)
{
    string* raw_tokens = String_SplitOnChar(line, ' ');
    string* cooked_tokens = {};
    foreach (string t : raw_tokens)
    {
        if (t != "")
            List_Append(cooked_tokens, t);
    }
    if (List_Length(cooked_tokens) > 0)
        execute_command(cooked_tokens);
    else
        restart();
}

private void restart()
{
    prompt(receive, "$ ");
}

private void restart_from_binary(...)
{
    restart();
}

void main(string* argv)
{
    restart();
}
