inherits "/std/cli.c";

public string* argv;
private string working_dir;

string pwd() { return working_dir; }
bool chdir(string path) { working_dir = path; return true; }

string* args() { return argv; }

bool execapp(func our_cb, string name, list args)
{
    if (name == nil)
        return false;
    return exec(our_cb, name, pwd(), args);
}

void create(object connection, func finish_cb, object me, string working_dir, list the_argv)
{
    "/std/cli.c"::create(connection, finish_cb, me);
    this.working_dir = working_dir;
    this.argv = the_argv;
}
