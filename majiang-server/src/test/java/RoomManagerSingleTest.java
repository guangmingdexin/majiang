import ds.guang.majing.common.game.room.RoomManager;

public class RoomManagerSingleTest {


    public static void test() {

        RoomManager ins1 = RoomManager.getInstance();

        RoomManager ins2 = RoomManager.getInstance();

        System.out.println(ins1);
        System.out.println(ins2);

        System.out.println(ins1 == ins2);

    }


    public static void main(String[] args) {
        test();
    }
}
