package ds.guang.majing.client.game;

import ds.guang.majing.common.game.card.MaJiang;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author guangyong.deng
 * @date 2022-02-22 10:21
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ClientMaJiang extends MaJiang {


    private Image src;


    private ImageView view;
}
