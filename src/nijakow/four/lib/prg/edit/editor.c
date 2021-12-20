inherit "/secure/object.c";

object io;
any regain;
int line;
any lines;

void cmd(string text)
{
    if (text == "exit") {
        call(regain);
    } else {
        io->write("???: ", text, "\n");
        newcmd();
    }
}

void newcmd()
{
    io->prompt(this::cmd, "edit> ");
}

void start() { newcmd(); }

void create(object the_io, any the_regain, string text)
{
    "/secure/object.c"::create();
    io = the_io;
    regain = the_regain;
    lines = splitson(text, this::isnewline);
}
