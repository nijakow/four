inherits "/std/app.c";

any subject;

void receive(string line)
{
    if (line == "")
        exit();
    else {
        connection()->write(eval(subject, line), "\n");
        resume();
    }
}

void resume()
{
    connection()->prompt(this::receive, "& ");
}

void start()
{
    if (length(argv) != 2) {
        subject = me();
    } else {
        string path = resolve(pwd(), argv[1]);
        subject     = the(path);
    }
    resume();
}
