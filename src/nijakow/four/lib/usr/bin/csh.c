inherits "/std/app.c";

any subject;

void receive(string line)
{
    if (line == "")
        exit();
    else {
        connection()->write(eval(subject, "this->" + line), "\n");
        resume();
    }
}

void resume()
{
    connection()->prompt(this::receive, "this->");
}

void start()
{
    if (length(argv) != 2) {
        connection->write("Argument error!\n");
        exit();
    } else {
        subject = the(argv[1]);
        resume();
    }
}
