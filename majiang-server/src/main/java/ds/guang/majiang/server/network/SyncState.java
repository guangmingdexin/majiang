package ds.guang.majiang.server.network;

/**
 *
 * 同步客户端-服务器端的状态
 * 每个状态绑定一个 SyncState，每次请求更新 session
 *
 * @author guangmingdexin
 */
public interface SyncState {


    /**
     *
     * 同步状态
     *
     * @param userId 用户 id
     * @return 同步是否成功
     */
    boolean syncState(String userId);


    /**
     *
     * 判断是否需要同步状态
     *
     * @param
     * @return
     */
    boolean isNeedSyncState();
}
