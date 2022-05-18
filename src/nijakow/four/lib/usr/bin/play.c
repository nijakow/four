#include "/lib/app.c"

private object player;

private void describe()
{
    object location = this.player->get_parent();
    if (location == nil)
        printf("You are floating in the void.\n");
    else {
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

private void cmd_look(string* args)
{
    describe();
}

private void event_callback(string event)
{
    printf("%s", event);
}

void main(string* argv)
{
    this.player = new("/std/thing.c");  // TODO: Find player
    this.player.add_command("look", this::cmd_look);
    this.player.add_command("examine", this::cmd_look);
    this.player.set_event_callback(this::event_callback);
    restart();
}
