package tech.bskplu.ui.generallore;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @ClassName: Assets
 * @Description: 全局资源服务：负责入队、加载、获取、释放。
 * @Author BsKPLu
 * @Date 2025/6/14
 * @Version 1.1
 */
public final class Assets {
    // ----- 单例 -----
    private static final Assets INSTANCE =new Assets();
    public static Assets inst() { return INSTANCE; }
    private Assets() {}

    // ----- 内部字段 -----
    private final AssetManager am = new AssetManager();
    private TextureAtlas uiAtlas;
    private Skin uiSkin;

    // ----- 预加载 -----
    public void queue(){
        // --- atlas(固定的UI元素 在这里加载) ---
        am.load("ui/general_profile/ui.txt",TextureAtlas.class);

        // --- 独立大图或可能频繁更换的纹理 ---
        am.load("bg_warlord_panel.png", Texture.class);
        am.load("portrait.png", Texture.class);

        am.load("armyBreakingSword.png", Texture.class);
        am.load("obsidianArmor.png", Texture.class);
        am.load("redHare.png", Texture.class);
        am.load("military_strategy.png", Texture.class);

    }

    // ----- 异步更新 在LoadingScreen渲染循环中调用 -----
    public float update() {
        return am.update() ? 1f : am.getProgress();
    }

    // ----- 同步结束加载(若不用LoadingScreen，可直接调用) -----
    public void finish() {
        am.finishLoading();
        uiAtlas = am.get("ui/general_profile/ui.txt", TextureAtlas.class);
    }

    // ----- getter -----
    public TextureAtlas atlas() {
        return uiAtlas;
    }

    public Skin skin() {
        return uiSkin;
    }

    public Texture getTexture(String internalPath) {
        return am.get(internalPath, Texture.class);
    }

    public Texture bg() {
        return getTexture("bg_warlord_panel.png");
    }

    public Texture portrait() {
        return getTexture("portrait.png");
    }

    public Texture equipSword() {
        return am.get("armyBreakingSword.png");
    }

    public Texture equipArmor() {
        return am.get("obsidianArmor.png");
    }

    public Texture equipMount() {
        return am.get("redHare.png");
    }

    public Texture equipBook() {
        return am.get("military_strategy.png");
    }

    // 统一释放
    public void dispose() { am.dispose(); }

}
