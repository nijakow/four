#include "/lib/app.c"

private object player;

private void describe()
{
    object location = this.player->get_parent();
    if (location != nil)
    {
        printf("%s\n", location->get_short());
        printf("%s\n", location->get_desc());
        // TODO: List everything that's here
    }
}

private void receive(string line)
{
    if (line == "exit") {
        exit(0);
        return;
    } else if (line == "") {
        restart();
    } else if (line == "look") {
        describe();
        restart();
    } else {
        if (!this.player->obey(line))
            printf("???\n");
        restart();
    }
}

private void restart()
{
    prompt(this::receive, "> ");
}

void main(string* argv)
{
    this.player = new("/std/thing.c");  // TODO: Find player
    restart();
}
