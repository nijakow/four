inherit "/std/cli.c";

mapping mapped_pathnames;


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
            file_not_found_error();
            break;
        }
        string the_text = cat(the_path);
        if (the_text == nil) {
            file_not_found_error();
            break;
        }
        connection()->write(the_text);
    }
    resume();
}

void cmd_edit_file__write(any id, string text)
{
    string path = mapped_pathnames[id];
    mapped_pathnames[id] = nil;
    if (path != nil && text != nil) {
        connection()->mode_italic();
        if(!echo_into(path, text)) {
            connection()->mode_red();
            connection()->write("Could not write \"", path, "\"!\n");
        } else {
            connection()->mode_green();
            connection()->write("\"", path, "\" written.\n");
        }
        connection()->mode_normal();
    }
    resume();
}

void cmd_touch_file(list argv)
{
    if (length(argv) != 2)
        connection()->write("Could not create file!\n");
    else {
        if (!touch(argv[1]))
            connection()->write("Could not create file!\n");
        else
            connection()->write(argv[1], " created.\n");
    }
    resume();
}

void cmd_edit_file(list argv)
{
    /*
     * TODO: Check if the requested file can be edited by the current user.
     *       Maybe also disable editing of special security files.
     * - mhahnFr
     */
    if (length(argv) != 2)
        arg_error();
    else {
        string path = resolve(pwd(), argv[1]);
        string content = cat(path);
        if (content == nil) {
            if (touch(path))
                content = "";
        }
        if (content != nil) {
            any id = connection()->edit(this::cmd_edit_file__write, path, content);
            mapped_pathnames[id] = path;
        } else {
            file_not_found_error();
        }
    }
    resume();
}

void cmd_recompile_file(list argv)
{
    if (length(argv) <= 1)
        arg_error();
    else {
        for (int i = 1; i < length(argv); i++) {
            string file = resolve(pwd(), argv[i]);
            if (recompile(file)) {
                the(file);  // This automatically reinitializes the file
                connection()->mode_green();
                connection()->write(file, ": ok.\n");
                connection()->mode_normal();
            } else {
                connection()->mode_red();
                connection()->write(file, ": error.\n");
                connection()->mode_normal();
            }
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
    else if (argv[0] == "edit")
        cmd_edit_file(argv);
    else if (argv[0] == "cc")
        cmd_recompile_file(argv);
    else if (argv[0] == "touch")
        cmd_touch_file(argv);
    else {
        object cmd = new("/bin/" + argv[0] + ".c", connection(), this::resume);
        if (cmd != nil) {
            cmd->start(argv);
        } else {
            connection()->write(argv[0], ": not a command!\n");
            resume();
        }
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
    mapped_pathnames = [];
}
