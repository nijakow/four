#include "/lib/app.c"

private object player;

private void describe()
{
    string* here;

    object location = this.player->get_parent();
    if (location == nil)
        printf("You are floating in the void.\n");
    else {
        printf("%s\n", location->get_short());
        printf("%s\n", location->get_desc());
        here = {};
        this.player->collect_here(here, false);
        for (object o : here)
        {
            if (o != this.player)
                printf("%s.\n", o->get_long());
        }
    }
}

private void cmd_look(string* args)
{
    describe();
}

private void event_callback(string event)
{
    printf("%s", event);
}

private void main_loop()
{
    while (true)
    {
        string line = this.prompt("> ");
        if (line == "exit") {
            return;
        } else if (line == "") {
        } else {
            if (!this.player->obey(line))
                printf("???\n");
        }
    }
}

void main(string* argv)
{
    this.player = new("/std/thing.c");  // TODO: Find player
    this.player.add_command("look", this::cmd_look);
    this.player.add_command("examine", this::cmd_look);
    this.player.set_event_callback(this::event_callback);
    main_loop();
}
