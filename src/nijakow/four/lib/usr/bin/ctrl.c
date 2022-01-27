inherits "/std/cli.c";

void docmd(string cmd, string args)
{
    if (cmd == "") {
        resume();
    } else if (cmd == "shell!") {   // Emergency shell!
        exec(this::resume, "/usr/bin/shell.c");
    } else if (cmd == "quit" || cmd == "logout") {
        connection()->write("Goodbye! :)\n");
        connection()->close();
    } else {
        if (!exec(this::resume, "/usr/cmds/" + cmd + ".c", args)) {
            connection()->write("I didn't quite get that, sorry...\n");
            resume();
        }
    }
}

void receive(string cmd)
{
    string args;

    int i = indexof(cmd, ' ');

    if (i != -1) {
        args = substr(cmd, i + 1, strlen(cmd));
        cmd = substr(cmd, 0, i);
    } else {
        args = "";
    }

    docmd(cmd, args);
}

void resume()
{
    connection()->prompt(this::receive, "> ");
}

void start()
{
    connection()->set_fallback(this::resume);
    me()->submit_lines_to(connection()->write);
    resume();
}
