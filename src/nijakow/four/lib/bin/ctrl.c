inherits "/std/app.c";

private int bad_cmds;

void docmd(string cmd, string args)
{
    if (cmd == "") {
        resume();
    } else if (cmd == "quit" || cmd == "logout") {
        exit();
    } else {
        log("Player '", me()->get_short(), "' issued the command '", cmd, "'.\n");
        if (exec(this::resume, "/usr/cmds/" + cmd + ".c", args)) {
            bad_cmds = 0;
        } else {
            bad_cmds++;
            if (bad_cmds >= 2) {
                bad_cmds = 0;
                printf("Looking for something? Try 'help' ;-)\n");
            } else {
                printf("I didn't quite get that, sorry...\n");
            }
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
    connection()->set_fallback(this::resume);
    prompt(this::receive, "> ");
}

void start()
{
    bad_cmds = 0;
    if (me() == nil) {
        printf("Game access is only allowed for player accounts.\n");
        exit();
    } else {
        printf("Please type 'look' to open your eyes.\n");
        resume();
    }
}
