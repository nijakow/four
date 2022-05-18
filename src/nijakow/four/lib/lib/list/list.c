use $listinsert;
use $listremove;
use $length;

int List_Length(any* lst)
{
    return $length(lst);
}

void List_Insert(any* lst, int index, any value)
{
    $listinsert(lst, index, value);
}

void List_Append(any* lst, any value)
{
    List_Insert(lst, List_Length(lst), value);
}

void List_Remove(any* lst, int index)
{
    return $listremove(lst, index);
}
