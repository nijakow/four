inherits "/lib/app.c";

void receive(string line)
{
    printf("'%s'\n", line);
    restart();
}

void restart()
{
    prompt(receive, "$ ");
}

void main(string* argv)
{
    restart();
}
