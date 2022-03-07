use $random;

int rand()
{
    return $random();
}

any select_random(list lst)
{
    if (length(lst) == 0)
        return nil;
    else
        return lst[rand() % length(lst)];
}
