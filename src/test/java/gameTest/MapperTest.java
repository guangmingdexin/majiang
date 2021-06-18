package gameTest;

import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.GameInfoMapper;
import com.guang.majiangserver.mapper.InfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.List;

/**
 * @ClassName MapperTest
 * @Author guangmingdexin
 * @Date 2021/4/24 10:54
 * @Version 1.0
 **/
public class MapperTest {

    public static void main(String[] args) {


        SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
       // GameInfoMapper gameInfoMapper = sqlSession.getMapper(GameInfoMapper.class);
       // int r = mapper.register(new User("123", "111"));
       // User user = mapper.getUserInfoByTel("123");
       // System.out.println("r: " + r);
        List<User> allUsers = mapper.findAllUsers();
//
        allUsers.forEach(user -> {
            // System.out.println("user: " + user);
           // System.out.println(JedisUtil.del("score:" + user.getUserId()));
            System.out.println(JedisUtil.zadd("score:" + user.getUserId(), 0, String.valueOf(user.getUserId())));
        });

//        ThreadGroup currentGroup =
//                Thread.currentThread().getThreadGroup();
//        int noThreads = currentGroup.activeCount();
//        Thread[] lstThreads = new Thread[noThreads];
//        currentGroup.enumerate(lstThreads);
//        for (int i = 0; i < noThreads; i++)
//            System.out.println("线程号：" + i + " = " + lstThreads[i].getName());
//
//        System.out.println("完成！");
//        sqlSession.close();


    }
}
