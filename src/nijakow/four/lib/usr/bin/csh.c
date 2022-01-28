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
    if (length(argv) == 1) {
        subject = me();
        resume();
    } else if (length(argv) == 2) {
        string path = resolve(pwd(), argv[1]);
        subject     = the(path);
        resume();
    } else {
        connection()->write("Argument error!\n");
        exit();
    }
}
