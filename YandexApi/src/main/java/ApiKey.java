class ApiKey
{
    private String keyName;
    private String guid;

    ApiKey(String keyName, String guid)
    {
        this.keyName = keyName;
        this.guid = guid;
    }

    String getKeyName()
    {
        return keyName;
    }

    String getGuid()
    {
        return guid;
    }
}
