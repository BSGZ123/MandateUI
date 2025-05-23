package tech.bskplu.ui.generallore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * @ClassName: WarlordScreen
 * @Description: 武将资料界面
 * @Author BsKPLu
 * @Date 2025/5/21
 * @Version 1.1
 */
public class WarlordScreen extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private float experience = 500f;

    private Texture bgTexture, portraitTex, slotTex, expBgTex, expKnobTex;
    private Texture swordTex, armorTex, horseTex, bookTex;
    private BitmapFont font;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // ---- 加载资源 ----
        //font = new BitmapFont(Gdx.files.internal("ui/font.fnt"));
        font = new BitmapFont();

        bgTexture = new Texture("bg_warlord_panel.png");
        portraitTex = new Texture("portrait.png");
        slotTex = new Texture("equip_slot.png");

        expBgTex = new Texture("exp_bg.png");
        expKnobTex = new Texture("exp_knob.png");

        swordTex = new Texture("Army-Breaking Sword.png");
        armorTex = new Texture("obsidian armor.png");
        horseTex = new Texture("Red Hare Horse.png");
        bookTex = new Texture("military strategy.png");

        // ==== 皮肤与样式手动构建 ====
        skin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        skin.add("default", labelStyle);
        skin.add("default", btnStyle);

        ProgressBar.ProgressBarStyle xpStyle = new ProgressBar.ProgressBarStyle();
        xpStyle.background = new TextureRegionDrawable(new TextureRegion(expBgTex));
        xpStyle.knob = new TextureRegionDrawable(new TextureRegion(expKnobTex));
        xpStyle.knobBefore = xpStyle.knob;

        // ==== 背景图 ====
        Image bgImage = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        bgImage.setFillParent(true);
        bgImage.setTouchable(Touchable.disabled);

        // ==== 左侧面板 ====
        Table left = new Table();
        left.defaults().pad(5);
        left.add(new Image(new TextureRegionDrawable(new TextureRegion(portraitTex)))).size(180).row();
        left.add(new Label("柳岩", labelStyle)).row();
        left.add(new Label("无官职", labelStyle)).row();

        ProgressBar expBar = new ProgressBar(0, 1000, 1, false, xpStyle);
        expBar.setValue(experience);
        left.add(expBar).width(200).row();

        final Label expLabel = new Label("经验: " + (int) experience + " / 1000", labelStyle);
        left.add(expLabel).row();

        TextButton gainBt = new TextButton("训练 +100经验", btnStyle);
        gainBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                experience = Math.min(1000, experience + 100);
                expBar.setValue(experience);
                expLabel.setText("经验: " + (int) experience + " / 1000");
            }
        });
        left.add(gainBt).row();

        RadarWidget radar = new RadarWidget(new float[]{99, 99, 99, 99, 99});
        left.add(radar).size(150).row();

        // ==== 中部面板 ====
        Table center = new Table();
        center.defaults().pad(5);
        center.add(new Label("武: 99", labelStyle));
        center.add(new Label("知: 99", labelStyle)).row();
        center.add(new Label("统: 99", labelStyle));
        center.add(new Label("政: 99", labelStyle)).row();
        center.add(new Label("德: 99", labelStyle));
        center.add(new Label("忠: 90", labelStyle)).row();

        // ==== 装备栏 ====
        Table equip = new Table();
        TooltipManager.getInstance().initialTime = 0.2f;
        equip.add(createEquipSlot(slotTex, swordTex, "破军剑\n攻击 +20\n暴击 +10%", labelStyle)).pad(5);
        equip.add(createEquipSlot(slotTex, armorTex, "黑曜甲\n防御 +25\n韧性 +5%", labelStyle)).pad(5);
        equip.add(createEquipSlot(slotTex, horseTex, "赤兔马\n机动 +30", labelStyle)).pad(5);
        equip.add(createEquipSlot(slotTex, bookTex, "兵法精要\n智力 +15\n策略成功率 +5%", labelStyle)).pad(5);
        center.add(equip).colspan(2).padTop(20).row();

        // ==== 战绩统计 ====
        Table stats = new Table();
        stats.defaults().pad(4);
        stats.add(new Label("白兵胜利: 0", labelStyle));
        stats.add(new Label("单挑胜利: 0", labelStyle)).row();
        stats.add(new Label("战役胜利: 0", labelStyle));
        stats.add(new Label("武将击杀: 0", labelStyle)).row();
        stats.add(new Label("武将死亡: 0", labelStyle));
        stats.add(new Label("外交成功: 0", labelStyle)).row();
        center.add(stats).colspan(2).padTop(20).row();

        // ==== 右侧列传 ====
        Table right = new Table();
        Label bio = new Label("魏延，字文长，义阳人，初为牙门将军……", labelStyle);
        bio.setWrap(true);
        ScrollPane scroll = new ScrollPane(bio);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        right.add(scroll).width(300).height(450);

        // ==== 整体布局 ====
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.pad(10);
        uiTable.add(left).width(260).top().padRight(20);
        uiTable.add(center).expand().top().padRight(20);
        uiTable.add(right).width(320).top();

        Stack root = new Stack();
        root.setFillParent(true);
        root.add(bgImage);
        root.add(uiTable);

        stage.addActor(root);
    }

    private Stack createEquipSlot(Texture bg, Texture iconTex, String tooltipText, Label.LabelStyle labelStyle) {
        Image bgImg = new Image(new TextureRegionDrawable(new TextureRegion(bg)));
        Image icon = new Image(new TextureRegionDrawable(new TextureRegion(iconTex)));

        Stack slot = new Stack();
        slot.add(bgImg);
        slot.add(icon);

        Tooltip<Label> tip = new Tooltip<>(new Label(tooltipText, labelStyle));
        slot.addListener(tip);
        return slot;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        bgTexture.dispose();
        portraitTex.dispose();
        slotTex.dispose();
        swordTex.dispose();
        armorTex.dispose();
        horseTex.dispose();
        bookTex.dispose();
        expBgTex.dispose();
        expKnobTex.dispose();

    }

    // 雷达图绘制
    private static class RadarWidget extends Widget {
        private final float[] stats;
        private final ShapeRenderer sr = new ShapeRenderer();

        public RadarWidget(float[] stats) {
            this.stats = stats;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();
            sr.setProjectionMatrix(batch.getProjectionMatrix());
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.CYAN);

            int points = stats.length;
            float angleStep = MathUtils.degreesToRadians * 360f / points;
            float cx = getX() + getWidth() / 2f;
            float cy = getY() + getHeight() / 2f;
            float radius = Math.min(getWidth(), getHeight()) / 2f;

            for (int i = 0; i < points; i++) {
                int ni = (i + 1) % points;
                float va = stats[i] / 100f;
                float vb = stats[ni] / 100f;

                float x1 = cx + MathUtils.cos(angleStep * i) * radius * va;
                float y1 = cy + MathUtils.sin(angleStep * i) * radius * va;
                float x2 = cx + MathUtils.cos(angleStep * ni) * radius * vb;
                float y2 = cy + MathUtils.sin(angleStep * ni) * radius * vb;

                sr.line(x1, y1, x2, y2);
            }

            sr.end();
            batch.begin();
        }



    }
}
