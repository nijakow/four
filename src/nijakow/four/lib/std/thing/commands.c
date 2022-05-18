/*
 *     C o m m a n d   S e c t i o n
 */

private object* commands;

void add_command(string pattern, func callback)
{
    List_Append(this.commands, new("/std/thing/command.c", pattern, callback));
}

bool obey(string command)
{
    for (object o : this.commands)
    {
        string* lst = o->match(command);
        if (lst != nil) {
            o->execute(lst);
            return true;
        }
    }
    return false;
}

void reset()
{
    this.commands = {};
}

void _init()
{
}
