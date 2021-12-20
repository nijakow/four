inherit "/secure/object.c";

any subscribers;

void add_subscriber(any client)
{
    append(subscribers, client);
}

void remove_subscriber(any client)
{
    for (int x = 0; x < length(subscribers);) {
        if (subscribers[x] == client) {
            remove(subscribers, x);
        } else {
            x = x + 1;
        }
    }
}

void broadcast(...)
{
    for (int x = 0; x < length(subscribers);) {
        subscribers[x]->write(...);
        x = x + 1;
    }
}

void create()
{
    "/secure/object.c"::create();
    subscribers = {};
}
