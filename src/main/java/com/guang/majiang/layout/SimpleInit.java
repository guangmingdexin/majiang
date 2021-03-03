package com.guang.majiang.layout;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.CardType;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.ImageRoot;
import com.guang.majiang.image.BackgroundMyImage;
import com.guang.majiang.image.CardFaceImage;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName SimpleInit
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 17:09
 * @Version 1.0
 **/
@Getter
@Setter
public final class SimpleInit implements Init {

    private BackgroundMyImage bg;

    private CardImage cardImage;

    private List<CardImage> cards;

    private static PlayerCard[] playerCards;

    private static Map<Character, Integer> points = new HashMap<>(3);

    static {
        // 万
        points.put('c', 10);
        // 条
        points.put('b', 100);
        // 筒
        points.put('d', 1000);
    }

    public SimpleInit() {
        this.bg = addBackground();
        this.cards = addCards();
        this.cardImage = cards.get(0);
    }


    @Override
    public BackgroundMyImage addBackground() {

        // 1.加载背景图片
        Load load = new LoadImpl();

        ImageView bg = load.load(ImageRoot.BACKGROUNG_IMAGE_ROOT.getPath());

        String fileName = bg.getId();

        String id = "bg";

        String filePath = ImageRoot.BACKGROUNG_IMAGE_ROOT.getPath() + File.separator + fileName;

        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();

        bg.setFitWidth(width);
        bg.setFitHeight(height);

        BackgroundMyImage background = new BackgroundMyImage(id, filePath, "bg_image",
                bg, bg.getImage().getHeight(), bg.getImage().getWidth());
        return background;

    }

    @Override
    public List<CardImage> addCards() {

        // 1.加载麻将图片
        Load load = new LoadImpl();

        List<ImageView> cards = load.loads(ImageRoot.MAJIANG_IMAGE_ROOT.getPath());

        List<CardImage> images = new ArrayList<>();

        for (ImageView card : cards) {

            String name = card.getId();

            // 通过 name 确定类型
            if(Pattern.matches("^[b-d]{1}[a-z]+\\d{1}\\.(png|jpg){1}$", name)) {

                CardType cardType = findType(name.charAt(0));

                int value = findNum(name);

                if(value == -1) {
                    System.out.println("图片格式不正确！");
                    throw new IllegalArgumentException("图片格式不正确!");
                }

                String filePath = ImageRoot.BACKGROUNG_IMAGE_ROOT.getPath() + File.separator + name;

                card.setFitHeight(card.getImage().getHeight());
                card.setFitWidth(card.getImage().getWidth());

                // 首先加载麻将背面图片
                CardFaceImage cardFaceImage = addFaceDown();

                CardImage c = new CardImage("card", filePath, name, card,
                        card.getImage().getHeight(), card.getImage().getWidth(),
                        cardType, value, CardStatus.STORAGE, cardFaceImage);

                images.add(c);

            }

        }

        return images;
    }

    @Override
    public PlayerCard[] addPlayerCard() {

        System.out.println("棋牌的数量：" + cards.size());
        // 游戏初始化 四位玩家
        playerCards = new PlayerCard[4];

        playerCards[0] = new PlayerCard(new Player("1", "张三", Direction.UNDER, false, 0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerCards);
        playerCards[1] = new PlayerCard(new Player("2", "李四", Direction.LEFT, false, 0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerCards);
        playerCards[2] = new PlayerCard(new Player("3", "王五", Direction.ABOVE, false, 0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerCards);
        playerCards[3] = new PlayerCard(new Player("4", "王麻子", Direction.RIGHT, false, 0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerCards);
        // 洗牌
        Collections.shuffle(cards);

        LinkedList<CardImage> handCards = new LinkedList<>(cards);

        // 确定庄家
        int r = new Random().nextInt(4);

        System.out.println("庄家为： " + r);

        playerCards[r].getPlayer().setBookmaker(true);
        playerCards[r].getPlayer().setIsRound(1);
        // 模拟发牌
        for (int i = 0; i < 4; i++) {

            // 初始手牌数
            int countCardHand = i == r ? 14 : 13;

            for (int j = 0; j < countCardHand; j++) {
                CardImage c = handCards.poll();
                playerCards[i].getCards().add(c);
                // 设置手牌状态
                assert c != null;
                c.setCardStatus(CardStatus.HOLD);
            }

        }

        for (PlayerCard playerCard : playerCards) {
            playerCard.start();
        }

        return playerCards;
    }


    private int findNum(String str) {

        char[] chars = str.toCharArray();

        for (char c : chars) {
            if(c >= '1' && c <= '9') {
                return (c - '0') + points.get(chars[0]);
            }
        }

        return -1;
    }

    private CardType findType(char ch) {

        if(ch == 'b') {
            // 条子
            return CardType.BAMBOO;
        }else if(ch == 'c') {
            // 万子
            return CardType.CHARACTER;
        }else if(ch == 'd') {
            // 筒子
            return CardType.DOT;
        }

        throw new IllegalArgumentException("图片名称格式不对！");
    }

    public CardFaceImage addFaceDown() {

        Load load = new LoadImpl();

        // 首先加载麻将背面图片
        String path = ImageRoot.MAJIANG_IMAGE_ROOT.getPath() + File.separator + "face-down.png";
        ImageView faceDown = load.load(path);
        CardFaceImage cardFaceImage = new CardFaceImage("face-down", path
                ,"麻将背面", faceDown, faceDown.getFitHeight(), faceDown.getFitWidth());

        String pathLeft = ImageRoot.MAJIANG_IMAGE_ROOT.getPath() + File.separator + "face-down-left.png";
        ImageView faceDownLeft = load.load(pathLeft);

        cardFaceImage.setFaceDownLeft(faceDownLeft);

        String pathRight = ImageRoot.MAJIANG_IMAGE_ROOT.getPath() + File.separator + "face-down-right.png";
        ImageView faceDownRight = load.load(pathRight);
        cardFaceImage.setFaceDownRight(faceDownRight);

        return cardFaceImage;

    }


}
