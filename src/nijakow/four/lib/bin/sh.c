inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/string/split.c";
inherits "/lib/sys/fs/wd.c";
inherits "/lib/sys/fs/resolve.c";

private void execute_command_in_path(string* argv, string path)
{
    string result;
    string* paths;

    paths = String_SplitOnChar(path, ':');
    if (exec(restart_from_binary, FileSystem_Resolve(FileSystem_GetWorkingDirectory(), argv[0]), argv))
        return;
    foreach (string pth : paths)
    {
        if (exec(restart_from_binary, pth + "/" + argv[0] + ".c", argv))
            return;
    }
    printf("%s: not found!\n", argv[0]);
    restart();
}

private void execute_command(string* argv)
{
    execute_command_in_path(argv, "/bin:/sbin:/usr/bin");
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
    prompt(receive, "%s $ ", FileSystem_GetWorkingDirectory());
}

private void restart_from_binary(...)
{
    restart();
}

void main(string* argv)
{
    restart();
}
