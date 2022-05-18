private bool is_container;

bool query_is_container()
{
    return this.is_container;
}

void enable_container()
{
    this.is_container = true;
}

void disable_container()
{
    this.is_container = false;
}

void reset()
{
    this.is_container = false;
}

void _init()
{
}
