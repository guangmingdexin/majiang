package ds.guang.majiang.server.network;

/**
 *
 * 同步客户端-服务器端的状态
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
}
