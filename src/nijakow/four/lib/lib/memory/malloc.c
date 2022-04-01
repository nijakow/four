// TODO: Import List_Append

char* Memory_Alloc(int size)
{
    char*  data = {};
    for (int x = 0; x < size; x++)
        List_Append(data, 0);
    return data;
}
