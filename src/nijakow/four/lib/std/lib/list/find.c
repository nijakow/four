
int find(any lst, any e)
{
    for (int x = 0; x < length(lst); x++)
        if (lst[x] == e)
            return x;
    return -1;
}

int rfind(any lst, any e)
{
    for (int x = length(lst) - 1; x >= 0; x--)
        if (lst[x] == e)
            return x;
    return -1;
}

bool member(any lst, any e)
{
    return find(lst, e) != -1;
}
