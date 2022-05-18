#include "/lib/app.c"
#include "/lib/list/list.c"
#include "/lib/string/split.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/stat.c"
#include "/lib/sys/identity/user.c"

private void execute_command_in_path(string* argv, string path)
{
    string result;
    string* paths;

    if (argv[0] == "exit") {
        printf("Exiting!\n");
        exit(0);
    } else if (argv[0] == "cd") {
        if (List_Length(argv) != 2)
            printf("cd: argument error!\n");
        else {
            string path = FileSystem_ResolveHere(argv[1]);
            if (!FileSystem_IsDirectory(path))
                printf("%s: not a directory!\n", argv[1]);
            else
                FileSystem_SetCurrentDirectory(path);
        }
    } else {
        paths = String_SplitOnChar(path, ':');
        if (exec(this::restart_from_binary, FileSystem_ResolveHere(argv[0]), argv))
            return;
        foreach (string pth : paths)
        {
            if (exec(this::restart_from_binary, pth + "/" + argv[0] + ".c", argv))
                return;
        }
        printf("%s: not found!\n", argv[0]);
    }
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
}

private string shell_prompt()
{
    if (User_AmIRoot()) {
        return prompt("%s@four:%s# ", User_Whoami(), FileSystem_GetWorkingDirectory());
    } else {
        return prompt("%s@four:%s$ ", User_Whoami(), FileSystem_GetWorkingDirectory());
    }
}

private void restart_from_binary(...)
{
}

void main(string* argv)
{
    while (true)
    {
        receive(shell_prompt());
    }
}
