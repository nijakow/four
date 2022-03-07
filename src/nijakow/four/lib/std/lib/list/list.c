use $listinsert;
use $listremove;

void insert(list lst, int index, any value)
{
    $listinsert(lst, index, value);
}

void append(list lst, any value)
{
    insert(lst, length(lst), value);
}

any remove(list lst, int index)
{
    return $listremove(lst, index);
}
