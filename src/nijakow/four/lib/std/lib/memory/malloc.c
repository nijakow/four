
char* malloc(int size)
{
    char*  data = [];
    for (int x = 0; x < size; x++)
        append(data, 0);
    return data;
}
