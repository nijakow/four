use $listinsert;
use $listremove;
use $length;

int List_Length(list lst)
{
    return $length(lst);
}

void List_Insert(list lst, int index, any value)
{
    $listinsert(lst, index, value);
}

void List_Append(list lst, any value)
{
    List_Insert(lst, List_Length(lst), value);
}

void List_Remove(list lst, int index)
{
    return $listremove(lst, index);
}
