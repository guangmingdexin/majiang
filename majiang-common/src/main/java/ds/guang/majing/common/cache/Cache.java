package ds.guang.majing.common.cache;

/**
 * @author asus
 */
public interface Cache {

    /** 常量，表示一个key永不过期 (在一个key被标注为永远不过期时返回此值) */
    long NEVER_EXPIRE = -1;

    /** 常量，表示系统中不存在这个缓存 (在对不存在的key获取剩余存活时间时返回此值) */
     long NOT_VALUE_EXPIRE = -2;

     Cache INSTANCE = new CacheDefaultImpl();

    /**
     *
     * 获取默认实现方式
     *
     * @return
     */
    static Cache getInstance() {
        return INSTANCE;
    }

    // --------------------- 字符串读写 ---------------------

    /**
     * 获取Value，如无返空
     * @param key 键名称
     * @return value
     */
    public String get(String key);

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     * @param key 键名称
     * @param value 值
     * @param timeout 过期时间(值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储)
     */
    public void set(String key, String value, long timeout);

    /**
     * 更新Value (过期时间不变)
     * @param key 键名称
     * @param value 值
     */
    public void update(String key, String value);

    /**
     * 删除Value
     * @param key 键名称
     */
    public void delete(String key);

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     * @param key 指定key
     * @return 这个key的剩余存活时间
     */
    public long getTimeout(String key);

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     * @param key 指定key
     * @param timeout 过期时间
     */
    public void updateTimeout(String key, long timeout);


    // --------------------- 对象读写 ---------------------

    /**
     * 获取Object，如无返空
     * @param key 键名称
     * @return object
     */
    public Object getObject(String key);

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     * @param key 键名称
     * @param object 值
     * @param timeout 存活时间 (值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储)
     */
    public void setObject(String key, Object object, long timeout);

    /**
     * 更新Object (过期时间不变)
     * @param key 键名称
     * @param object 值
     */
    public void updateObject(String key, Object object);

    /**
     * 删除Object
     * @param key 键名称
     */
    public void deleteObject(String key);

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     * @param key 指定key
     * @return 这个key的剩余存活时间
     */
    public long getObjectTimeout(String key);

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     * @param key 指定key
     * @param timeout 过期时间
     */
    public void updateObjectTimeout(String key, long timeout);
}
