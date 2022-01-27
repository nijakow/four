inherits "/std/app.c";

mapping mapped_pathnames;


void arg_error()
{
    connection()->write("Argument error!\n");
}

void file_not_found_error()
{
    connection()->write("File not found!\n");
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

void cmd_edit_file__write(any id, string text)
{
    string path = mapped_pathnames[id];
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

void cmd_edit_file(list argv)
{
    /*
     * TODO: Check if the requested file can be edited by the current user.
     *       Maybe also disable editing of special security files.
     * - mhahnFr
     */
    if (length(argv) <= 1)
        arg_error();
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            string content = cat(path);
            if (content == nil) {
                file_not_found_error();
            } else {
                any id = connection()->edit(this->cmd_edit_file__write, path, content);
                mapped_pathnames[id] = path;
            }
        }
    }
    resume();
}

object lookup_cmd_instance(list argv)
{
    object cmd;
    cmd = new("/bin/" + argv[0] + ".c", connection(), this::resume, me(), pwd(), argv);
    if (cmd == nil)
        cmd = new(resolve(pwd(), argv[0]), connection(), this::resume, me(), pwd(), argv);
    return cmd;
}

bool launch_app(list argv)
{
    return execapp(this->resume, "/bin/" + argv[0] + ".c", argv)
        || execapp(this->resume, "/sbin/" + argv[0] + ".c", argv)
        || execapp(this->resume, "/usr/bin/" + argv[0] + ".c", argv)
        || execapp(this->resume, resolve(pwd(), argv[0]), argv);
}

void receive(string line)
{
    list argv = split(line);

    if (length(argv) == 0) resume();
    else if (argv[0] == "exit")
        exit();
    else if (argv[0] == "cd")
        cmd_cd(argv);
    else if (argv[0] == "edit")
        cmd_edit_file(argv);
    else {
        if (!launch_app(argv))
        {
            if (argv[0] == "ls") {
                cmd_ls(argv);
                resume();
            } else {
                connection()->write(argv[0], ": not a command!\n");
                resume();
            }
        }
    }
}

void resume()
{
    connection()->set_fallback(this::resume);
    string ps1 = pwd();
    if (ps1 != nil) ps1 = ps1 + " $ ";
    else ps1 = "$ ";
    connection()->prompt(this::receive, ps1);
}

void start()
{
    mapped_pathnames = [];
    resume();
}
