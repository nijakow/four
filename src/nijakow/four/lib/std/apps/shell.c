inherit "/secure/object.c";

object connection;
func   termination_cb;

void receive(string line)
{
    list argv = split(line);

    if (length(argv) == 0) resume();
    else if (argv[0] == "exit") {
        call(termination_cb);
    } else {
        connection->write(argv[0], ": not a command!\n");
        resume();
    }
}

void resume()
{
    connection->prompt(this::receive, "$ ");
}

void start(object connection, func cb)
{
    this.connection     = connection;
    this.termination_cb = cb;
    resume();
}

void create()
{
}
