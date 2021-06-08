package gameTest;

import com.guang.majiangclient.client.entity.User;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.InfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

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

        int r = mapper.register(new User("123", "111"));
       // User user = mapper.getUserInfoByTel("123");
        System.out.println("r: " + r);
    }
}
