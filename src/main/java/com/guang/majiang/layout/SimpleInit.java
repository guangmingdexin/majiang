package com.guang.majiang.layout;

import com.guang.majiang.common.*;
import com.guang.majiang.image.BackgroundMyImage;
import com.guang.majiang.image.CardFaceImage;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import com.guang.majiang.player.PlayerNode;
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

    private LinkedList<CardImage> cards;

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
    public LinkedList<CardImage> addCards() {

        // 1.加载麻将图片
        Load load = new LoadImpl();

        List<ImageView> cards = load.loads(ImageRoot.MAJIANG_IMAGE_ROOT.getPath());

        LinkedList<CardImage> images = new LinkedList<>();

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
                images.offer(c);
            }
        }

        // 洗牌
        Collections.shuffle(images);

        return images;
    }

    @Override
    public PlayerNode<Player> addPlayerCard() {

        System.out.println("棋牌的数量：" + cards.size());
        // 游戏初始化 四位玩家

        PlayerNode<Player> under = new PlayerNode<>(
                new Player("1", "张三", Direction.UNDER, false, 0,
                        new PlayerCard(new ArrayList<>(), new ArrayList<>(), new ArrayList<>())));

        PlayerNode<Player> left = new PlayerNode<>(new Player("2", "李四", Direction.LEFT, false, 0,
                new PlayerCard(new ArrayList<>(), new ArrayList<>(), new ArrayList<>())));

        PlayerNode<Player> above = new PlayerNode<>(new Player("3", "王五", Direction.ABOVE, false, 0,
                new PlayerCard(new ArrayList<>(), new ArrayList<>(), new ArrayList<>())));

        PlayerNode<Player> right = new PlayerNode<>(new Player("4", "王麻子", Direction.RIGHT, false, 0,
                new PlayerCard(new ArrayList<>(), new ArrayList<>(), new ArrayList<>())));

        under.next = right;
        right.prev = under;

        right.next = above;
        above.prev = right;

        above.next = left;
        left.prev = above;

        left.next = under;
        under.prev = left;

        // 确定庄家
        int r = new Random().nextInt(4);

        PlayerNode<Player> bookmaker = under;
        while (r > 0) {
            bookmaker = bookmaker.next;
            r --;
        }
        Player p = bookmaker.item;
        System.out.println("庄家为： " + p.getDirection());

        p.setBookmaker(true);
        p.setIsRound(1);
        return under;
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

    public List<ImageView> loadSpecialImage(String... names) {
        List<ImageView> eventImages = new ArrayList<>();
        Load load = new LoadImpl();
        for (String name : names) {
            String path = ImageRoot.EVENT_IMAGE_ROOT.getPath() + File.separator +
                                name;
            ImageView img = load.load(path);
            img.setFitWidth(img.getFitWidth() / 2);
            img.setFitHeight(img.getFitHeight() / 2);
            if(eventImages.size() == 0) {
                img.setX(GlobalConstant.BG_WEITH - 6 * img.getFitWidth());
            }else {
                img.setX(eventImages.get(eventImages.size() - 1).getX() + img.getFitWidth());
            }
            img.setY(GlobalConstant.BG_HEIGHT - 3 * GlobalConstant.CARD_HEIGHT);
            img.setVisible(false);
            img.setId(name);
            eventImages.add(img);
        }
        ImageView ignore = eventImages.get(eventImages.size() - 1);
        ignore.setX(ignore.getX() + 30);
        return eventImages;
    }

    public ImageView loadArrow(String name) {
        Load load = new LoadImpl();
        String path = ImageRoot.ARROW_IMAGE_ROOT.getPath() + File.separator +
                name;
        ImageView arrow = load.load(path);
        arrow.setFitHeight(arrow.getFitHeight() / 10);
        arrow.setFitWidth(arrow.getFitWidth() / 10);
        arrow.setId("arrow");
        arrow.setVisible(false);
        return arrow;
    }

}
