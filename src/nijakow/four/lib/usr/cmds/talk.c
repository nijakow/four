inherits "/std/cmd.c";

void receive(string text)
{
    if (text == "")
        exit();
    else {
        me()->me_act("says: ", text, "\n");
        resume();
    }
}

void resume()
{
    prompt(this::receive, "You say: ");
}

void start()
{
    resume();
}
