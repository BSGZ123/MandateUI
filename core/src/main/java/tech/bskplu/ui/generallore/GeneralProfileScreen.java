package tech.bskplu.ui.generallore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.List;

public class GeneralProfileScreen extends ApplicationAdapter {

    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private BitmapFont font;
    private TextureAtlas atlas;

    // Textures (应通过AssetManager管理，此处为演示直接加载)
    private Texture backgroundTexture;
    private Texture portraitTexture;

    // 自定义雷达图 Actor
    private RadarChartActor radarChart;
    float[] fiveStats = new float[] { 0.75f, 0.65f, 0.85f, 0.55f, 0.90f };
    String[] labels = { "武", "统", "政", "德", "知" };

    // 类成员区，定义四个装备名称
    private List<String> equipmentNames = Arrays.asList("破军剑", "黯曜铠甲", "赤兔马", "兵书");

    // 武将技能组
    List<String> skills;

    // 定义最小/目标世界尺寸，ExtendViewport将基于此进行扩展
    private static final float WORLD_WIDTH = 2400;
    private static final float WORLD_HEIGHT = 1080;


    public GeneralProfileScreen() {
        // 构造函数中可以初始化一些东西，但主要加载和UI构建在 show() 中
        skills = Arrays.asList("后勤", "无双", "内连", "反击", "奇谋", "铁壁", "迅捷", "固守");
    }

    @Override
    public void create() {
        // 修改Viewport 类型为 ExtendViewport
        // ExtendViewport 会保持世界宽高比，并在需要时扩展世界区域以填充屏幕，而不是留黑边。
        // 它使用最小世界宽度和高度。如果屏幕更大，世界也会在那个维度上更大。
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, new OrthographicCamera());
        stage    = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);


        // 1. 加载资源
        //loadAssets(); // 加载纹理等
        Assets.inst().queue();
        Assets.inst().finish();

        atlas = Assets.inst().atlas();
        backgroundTexture = Assets.inst().bg();
        portraitTexture = Assets.inst().portrait();


        // 2. 创建 Skin
        createSkin();

        // 3. 创建主背景图片
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);// 背景将填充整个舞台区域，包括ExtendViewport扩展出的部分
        stage.addActor(backgroundImage);// 背景置于最底层

        // 4. 创建根 Table
        Table rootTable = new Table();
        rootTable.setFillParent(true);// 根Table也将填充整个舞台
        //rootTable.setDebug(true);// 开启调试线，完成后关闭
        stage.addActor(rootTable);

        // 5. 创建三列
        Table leftColumn = new Table(skin);
        Table middleColumn = new Table(skin);
        Table rightColumn = new Table(skin);

         //leftColumn.setDebug(true);
         //middleColumn.setDebug(true);
         //rightColumn.setDebug(true);

        // 6. 填充每一列
        populateLeftColumn(leftColumn);
        populateMiddleColumn(middleColumn);
        populateRightColumn(rightColumn);

        // 7. 将列添加到根 Table
        // 使用百分比宽度，这对于ExtendViewport是合适的，列会根据根Table的实际宽度（可能已扩展）分配空间
        rootTable.add(leftColumn).prefWidth(Value.percentWidth(0.20f, rootTable)).expandY().fillY().padLeft(20).padTop(20).padBottom(20);
        rootTable.add(middleColumn).prefWidth(Value.percentWidth(0.50f, rootTable)).expandY().fillY().padTop(20).padBottom(20).padLeft(10).padRight(10);
        rootTable.add(rightColumn).prefWidth(Value.percentWidth(0.30f, rootTable)).expandY().fillY().padRight(20).padTop(20).padBottom(20);
    }


    private void createSkin() {

        skin = new Skin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Alibaba-PuHuiTi-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;
        param.magFilter = Texture.TextureFilter.Linear;
        param.minFilter = Texture.TextureFilter.Linear;

        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS +
            "/破兵书兔黯曜铠甲男野心亲密称号装列剑传九爷洛月个固迅捷奇谋反铁勤双内连经验无官职柳岩妃子武将资料等级称号技能主公势力城市俸禄体知德统政忠相性部队兵种专精机动白胜利失败单挑计策战役击杀俘虏死亡外交成功生涯人物关系上一页下一页返回出仕超级人五行" +
            "这里是的平事迹可以有很多行文字关羽约年本长后改云河东郡解县今山西运汉末名早期跟随刘备辗转各地曾被曹操擒于马坡斩袁绍大颜良张飞同为万人敌赤壁之助吴周瑜攻打南仁别遣绝北道阻挡援军退走任命襄阳太守入益州留荆建安二十四围樊派禁前来增获庞威震华夏想迁都避其锐徐晃吕蒙又偷袭腹背受";

        // 字体初始化，默认Libgdx默认字体，不支持中文
        font = generator.generateFont(param);
        skin.add("default-font", font, BitmapFont.class);
        generator.dispose();


        /* ---------- 纯文字样式 ---------- */
        skin.add("default", new Label.LabelStyle(font, Color.WHITE), Label.LabelStyle.class);
        skin.add("golden",  new Label.LabelStyle(font, Color.GOLD),  Label.LabelStyle.class);

        /* ---------- 带 NinePatch 背景样式 ---------- */
        addLabelWithPatch("titleBoxStyle", "relationship_slot", Color.BLACK);
        addLabelWithPatch("superStyle", "small_green_frame", Color.WHITE);
        addLabelWithPatch("lvlTitleStyle", "special_bluegreen_frame", Color.WHITE);
        addLabelWithPatch("headerTitle", "generalFrame", Color.WHITE);
        addLabelWithPatch("identityStyle", "two_dashes", Color.WHITE);
        addLabelWithPatch("tabInfoStyle", "tab_information", Color.WHITE);
        addLabelWithPatch("relationTitleStyle", "shortHorizontal", Color.RED);

        /* ---------- 按钮样式 ---------- */
        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = font; btn.fontColor = Color.WHITE;
        btn.up   = new TextureRegionDrawable(atlas.findRegion("button_up"));
        btn.down = new TextureRegionDrawable(atlas.findRegion("button_down"));
        btn.over = btn.down;
        skin.add("default", btn, TextButton.TextButtonStyle.class);

        TextButton.TextButtonStyle back = new TextButton.TextButtonStyle();
        back.font = font; back.fontColor = Color.WHITE;
        TextureRegionDrawable backDraw = new TextureRegionDrawable(atlas.findRegion("return_button"));
        back.up = back.down = backDraw;
        skin.add("backStyle", back, TextButton.TextButtonStyle.class);

        /* ---------- ScrollPaneStyle 占位 ---------- */
        skin.add("default", new ScrollPane.ScrollPaneStyle(), ScrollPane.ScrollPaneStyle.class);
    }

    /** 辅助：为 LabelStyle 设置 NinePatch 背景 */
    private void addLabelWithPatch(String styleName, String regionName, Color fontColor) {
        // 取出TextureRegion
        TextureRegion region = atlas.findRegion(regionName);
        // 手动构造NinePatch left、right、top、bottom
        NinePatch patch = new NinePatch(region, 8, 8, 8, 8);
        NinePatchDrawable bg   = new NinePatchDrawable(patch);

        Label.LabelStyle style = new Label.LabelStyle(skin.getFont("default-font"), fontColor);
        style.background = bg;
        skin.add(styleName, style, Label.LabelStyle.class);
    }


    private void populateLeftColumn(Table leftColumn) {
        leftColumn.top().pad(10);

        // 创建 子Table
        Table headerRow = new Table(skin);
        headerRow.defaults().row();

        Label pageTitle = new Label("武将资料", skin, "headerTitle");
        pageTitle.setFontScale(1.8f);
        pageTitle.setAlignment(Align.center);

        Label identityLabel = new Label("妃子", skin, "identityStyle");
        identityLabel.setFontScale(1.2f);
        identityLabel.setAlignment(Align.center);

        headerRow.add(pageTitle).left().pad(5,0,10,10).height(65).width(205);
        headerRow.add(identityLabel).right().pad(5,10,10,0);

        leftColumn.add(headerRow)
            .expandX()
            .fillX()
            .row();

        Group portraitGroup = new Group();
        {
            // -—— 常量：背景框和头像的尺寸 ——-
            final float FRAME_SIZE     = 245f;// 头像框整体 245×245
            final float IMAGE_SIZE     = 224f;// 头像 224×224
            final float OFFSET_X       = 20f; // 头像框相对于 Group 左侧的偏移
            final float INNER_OFFSET   = (FRAME_SIZE - IMAGE_SIZE) / 2f;

            Image portraitBgImg = new Image(atlas.findRegion("portrait_frame"));
            portraitBgImg.setSize(FRAME_SIZE, FRAME_SIZE);
            portraitBgImg.setPosition(OFFSET_X, 0);

            Image portraitImg = new Image(portraitTexture);
            portraitImg.setSize(IMAGE_SIZE, IMAGE_SIZE);
            portraitImg.setPosition(OFFSET_X + INNER_OFFSET, INNER_OFFSET);


            // 把头像背景框和头像都加入 portraitGroup
            portraitGroup.addActor(portraitBgImg);
            portraitGroup.addActor(portraitImg);
        }

        // 性别圆框
        Stack genderStack = new Stack();
        Image genderIcon = new Image(atlas.findRegion("icon_male"));

        genderStack.add(genderIcon);
        Label genderText = new Label("男", new Label.LabelStyle(font, Color.WHITE));
        genderText.setFontScale(1.4f);
        genderText.setAlignment(Align.center);

        Container<Label> genderTextC = new Container<>(genderText);
        genderTextC.fill().center();
        genderStack.add(genderTextC);


        // 出仕竖条
        Label.LabelStyle greenBarStyle = new Label.LabelStyle(font, Color.WHITE);
        greenBarStyle.background = new NinePatchDrawable(
            new NinePatch(atlas.findRegion("green_vertical"), 4, 4, 4, 4)
        );
        skin.add("greenBar", greenBarStyle);
        Label statusBar = new Label("出仕", skin, "greenBar");
        statusBar.setAlignment(Align.center);
        Container<Label> statusC = new Container<>(statusBar);

        // 不需要对statusC单独pad，由表格自己定位
        // 如果需要微调可在下面的表格cel里加 padLeft/padTop
        // infoTable是两列布局，左列内两个子行，右列是portraitGroup
        Table infoTable = new Table(skin);

        Table leftSideTable = new Table(skin);
        leftSideTable.defaults().uniformX().pad(5);

        // 第一行 性别圆框，水平居中
        leftSideTable.add(genderStack)
            .width(68).height(76)
            .center()
            .row();

        // 第二行 出仕竖条，同样居中
        leftSideTable.add(statusC)
            .center()
            .row();

        // 把左侧 子Table放进infoTable的左列
        infoTable.add(leftSideTable)
            // 给左列指定一个固定宽度或最小宽度，使其与右侧头像区域比例合适
            .width(78)    // 大于genderIcon的68px，且留一点边距
            .padRight(10) // 左右列间隔 10px
            .fillY();     // 在父Table可用高度里填满纵向

        // 右侧放 portraitGroup
        infoTable.add(portraitGroup)
            .width(245).height(245)
            .center()
            .fillY();// 垂直方向上尽量填满同infoTable高度

        // .row()结束这一行
        infoTable.row();

        // 把构造好的infoTable放入leftColumn
        leftColumn.add(infoTable)
            .fillX()
            .padBottom(15)
            .row();

        // ----- 武将姓名及官位（已优化）----
        // 武将姓名 直接用白色大字号Label，无背景
        Label.LabelStyle nameStyle = new Label.LabelStyle(font, Color.WHITE);
        Label nameLabel = new Label("柳岩", nameStyle);
        nameLabel.setFontScale(1.6f);
        leftColumn.add(nameLabel)
            .expandX()// 水平占满，方便居中
            .center()
            .padTop(5)
            .padBottom(5)
            .row();

        // 官位
        Label rankLabel = new Label("无官职", skin, "headerTitle");
        rankLabel.setFontScale(1.5f);
        rankLabel.setAlignment(Align.center);
        leftColumn.add(rankLabel)
            .width(200)
            .height(55)
            .padTop(5)
            .padBottom(20)
            .row();

        // 能力雷达图（五维）
        // 原来固定宽高 改为扩展填充+相对高度
        BitmapFont radarFont = font;
        radarFont.setColor(Color.YELLOW);
        radarChart = new RadarChartActor(fiveStats, radarFont, labels);
        leftColumn.add(radarChart)
            .expandX()
            .fillX()
            .height(Value.percentHeight(0.28f, leftColumn))// 垂直占左列25%高度
            .padBottom(20)
            .row();

        // 等级经验及称号？
        Table levelExpTable = new Table(skin);
        levelExpTable.defaults().pad(5).expandX().fillX();

        // 超级
        Label superLabel1 = new Label("超级", skin, "superStyle");
        superLabel1.setFontScale(1.5f);
        superLabel1.setAlignment(Align.center);
        levelExpTable.add(superLabel1)
            .minWidth(88).minHeight(59)
            .left();

        // 空白占位
        levelExpTable.add()
            .width(80);

        // 等级
        Label lvlTitleLabel = new Label("等级", skin, "lvlTitleStyle");
        lvlTitleLabel.setFontScale(1.5f);
        lvlTitleLabel.setAlignment(Align.center);
        levelExpTable.add(lvlTitleLabel)
            .minWidth(85).minHeight(50)
            .center();

        // 等级数值
        Label lvlValueLabel = new Label("16", new Label.LabelStyle(font, Color.WHITE));
        lvlValueLabel.setFontScale(1.4f);
        levelExpTable.add(lvlValueLabel)
            .minWidth(60).minHeight(40)
            .center()
            .row();

        // 人将
        Label superLabel2 = new Label("人将", skin, "superStyle");
        superLabel2.setFontScale(1.5f);
        superLabel2.setAlignment(Align.center);
        levelExpTable.add(superLabel2)
            .minWidth(88).minHeight(59)
            .left();

        // 空白占位
        levelExpTable.add()
            .width(80);

        // 经验
        Label expTitleLabel = new Label("经验", skin, "lvlTitleStyle");
        expTitleLabel.setFontScale(1.5f);
        expTitleLabel.setAlignment(Align.center);
        levelExpTable.add(expTitleLabel)
            .minWidth(85).minHeight(50)
            .center();

        // 经验数值
        Label expValueLabel = new Label("5000", new Label.LabelStyle(font, Color.WHITE));
        expValueLabel.setFontScale(1.4f);
        levelExpTable.add(expValueLabel)
            .minWidth(60).minHeight(40)
            .center()
            .row();

        leftColumn.add(levelExpTable)
            .padBottom(20)
            .fillX()
            .row();

        // 武将技能组
        Table skillsTable = new Table(skin);
        skillsTable.defaults()
            .pad(5)
            .expandX()
            .fillX();

        int colsPerRow = 4;
        for (int i = 0; i < skills.size(); i++) {
            String skillName = skills.get(i);
            Label skillLabel = new Label(skillName, skin, "superStyle");
            skillLabel.setFontScale(1.4f);
            skillLabel.setAlignment(Align.center);

            skillsTable.add(skillLabel)
                .minWidth(80).minHeight(50)
                .center();

            if ((i + 1) % colsPerRow == 0) {
                skillsTable.row();
            }
        }

        if (skills.size() % colsPerRow != 0) {
            skillsTable.row();
        }

        leftColumn.add(skillsTable)
            .fillX()
            .padBottom(10)
            .row();

    }

    private void populateMiddleColumn(Table middleColumn) {
        middleColumn.top().pad(10);

        // ===== 最外层infoContainer 两列布局 =====
        // 左侧放 leftNestedTable（包含三行：个人 / 能力 / 部队）
        // 右侧放 loreStack（人物列传标题）
        Table infoContainer = new Table(skin);
        infoContainer.defaults().align(Align.center);

        // 创建左侧的leftNestedTable
        Table leftNestedTable = new Table(skin);
        leftNestedTable.defaults().pad(5).align(Align.center);

        // ——— 个人行 ———
        Table personalRow = new Table(skin);
        personalRow.defaults().pad(10).align(Align.center);
        //personalRow.setDebug(true);

        // （a）第一列：个人带背景框
        Label personalLabel = new Label("个人", skin, "tabInfoStyle");
        personalLabel.setFontScale(1.5f);
        personalLabel.setAlignment(Align.center);
        personalRow.add(personalLabel).minWidth(120).minHeight(55).padRight(30);

        // （b）势力+九爷
        personalRow.add(new Label("势力", skin, "golden")).padRight(15);
        Label lordValue = new Label("九爷", skin, "identityStyle");
        lordValue.setAlignment(Align.center);
        personalRow.add(lordValue).minWidth(80).minHeight(40).padRight(15);

        // （c）城市+洛阳
        personalRow.add(new Label("城市", skin, "golden")).padRight(15);
        Label cityValue = new Label("洛阳", skin, "identityStyle");
        cityValue.setAlignment(Align.center);
        personalRow.add(cityValue).minWidth(80).minHeight(40).padRight(15);

        // （d）俸禄+30/月
        personalRow.add(new Label("俸禄", skin, "golden")).padRight(15);
        Label salaryValue = new Label("30/月", skin, "identityStyle");
        salaryValue.setAlignment(Align.center);
        personalRow.add(salaryValue).minWidth(80).minHeight(40);

        // 将这行加入leftNestedTable
        leftNestedTable.add(personalRow).left().row();

        // ——— 能力行 ———
        Table abilityRow = new Table(skin);
        abilityRow.defaults().pad(10).align(Align.center);

        // （a）第一列 能力
        Label abilityLabel = new Label("能力", skin, "tabInfoStyle");
        abilityLabel.setFontScale(1.5f);
        abilityLabel.setAlignment(Align.center);
        abilityRow.add(abilityLabel).minWidth(120).minHeight(55).padRight(30);

        // （b）体 99
        abilityRow.add(new Label("体", skin, "golden")).padRight(5);
        Label valBody = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valBody).minWidth(50).minHeight(40).padRight(15);

        // （c）武 99
        abilityRow.add(new Label("武", skin, "golden")).padRight(5);
        Label valMartial = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valMartial).minWidth(50).minHeight(40).padRight(15);

        // （d）知 99
        abilityRow.add(new Label("知", skin, "golden")).padRight(5);
        Label valIntellect = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valIntellect).minWidth(50).minHeight(40).padRight(15);

        // （e）德 99
        abilityRow.add(new Label("德", skin, "golden")).padRight(5);
        Label valVirtue = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valVirtue).minWidth(50).minHeight(40).padRight(15);

        // （f）统 99
        abilityRow.add(new Label("统", skin, "golden")).padRight(5);
        Label valLeadership = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valLeadership).minWidth(50).minHeight(40).padRight(15);

        // （g）政 99
        abilityRow.add(new Label("政", skin, "golden")).padRight(5);
        Label valPolitics = new Label("99", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valPolitics).minWidth(50).minHeight(40).padRight(15);

        // （h）忠 90
        abilityRow.add(new Label("忠", skin, "golden")).padRight(5);
        Label valLoyalty = new Label("90", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valLoyalty).minWidth(50).minHeight(40).padRight(15);

        // （i）相性 100
        abilityRow.add(new Label("相性", skin, "golden")).padRight(5);
        Label valAffinity = new Label("100", new Label.LabelStyle(font, Color.WHITE));
        abilityRow.add(valAffinity).minWidth(50).minHeight(40);

        // 将能力行加入leftNestedTable
        leftNestedTable.add(abilityRow).left().row();

        // ——— 部队行 ———
        Table troopsRow = new Table(skin);
        troopsRow.defaults().pad(10).align(Align.center);

        // （a）第一列 部队
        Label troopsLabel = new Label("部队", skin, "tabInfoStyle");
        troopsLabel.setFontScale(1.5f);
        troopsLabel.setAlignment(Align.center);
        troopsRow.add(troopsLabel).minWidth(120).minHeight(55).padRight(30);

        // 定义图标统一尺寸
        float iconSize = 64f;

        // （b）兵种 山军
        troopsRow.add(new Image(atlas.findRegion("tf_xiandeng")))
            .size(iconSize, iconSize)
            .padRight(5);
        troopsRow.add(new Label("兵种", skin, "golden"))
            .padRight(3);
        troopsRow.add(new Label("山军", new Label.LabelStyle(font, Color.WHITE)))
            .minWidth(80).minHeight(40)
            .padRight(15);

        // （c）专精 剑
        troopsRow.add(new Image(atlas.findRegion("tf_xianfa")))
            .size(iconSize, iconSize)
            .padRight(5);
        troopsRow.add(new Label("专精", skin, "golden"))
            .padRight(3);
        troopsRow.add(new Label("剑", new Label.LabelStyle(font, Color.WHITE)))
            .minWidth(80).minHeight(40)
            .padRight(15);

        // （d）兵力 3000
        troopsRow.add(new Image(atlas.findRegion("tf_xushi")))
            .size(iconSize, iconSize)
            .padRight(5);
        troopsRow.add(new Label("兵力", skin, "golden"))
            .padRight(3);
        troopsRow.add(new Label("3000", new Label.LabelStyle(font, Color.WHITE)))
            .minWidth(80).minHeight(40)
            .padRight(15);

        // （e）机动 20
        troopsRow.add(new Image(atlas.findRegion("tf_jizou")))
            .size(iconSize, iconSize)
            .padRight(5);
        troopsRow.add(new Label("机动", skin, "golden"))
            .padRight(3);
        troopsRow.add(new Label("20", new Label.LabelStyle(font, Color.WHITE)))
            .minWidth(80).minHeight(40);

        leftNestedTable.add(troopsRow).left().row();

        // ===== 右侧的人物列传标题栏，只放一个 Label，下面留空 =====
        Table rightTitleTable = new Table(skin);
        rightTitleTable.top().padTop(3);// 标题靠上

        // 人物列传 Label
        Label loreTitle = new Label("人物列传", skin, "superStyle");
        loreTitle.setFontScale(1.1f);
        loreTitle.setAlignment(Align.center);
        rightTitleTable.add(loreTitle).minWidth(140).minHeight(50).row();
        //rightTitleTable.setDebug(true);

        // 再加一个空行，占据剩余高度
        rightTitleTable.add().row();

        infoContainer.add(leftNestedTable)
            .expandY().fillY()
            .padRight(10);

        infoContainer.add(rightTitleTable)
            .expandX()
            .right()
            .top()
            .padRight(5)
            .row();

        middleColumn.add(infoContainer)
            .expandX().fillX()
            .row();

        // 大分割栏
        middleColumn.add(new Image(atlas.findRegion("divider_h")))
            .fillX()
            .padTop(10)
            .padBottom(10)
            .row();

        // ----- 装备槽 -----
        Table equipmentSection = new Table(skin);

        Label equipTab = new Label("装备", skin, "tabInfoStyle");
        equipTab.setFontScale(1.4f);
        equipTab.setAlignment(Align.center);
        equipmentSection.add(equipTab)
            .left()
            .minWidth(120)
            .minHeight(55)
            .pad(15)
            .expandX()
            .row();

        Table equipGrid = new Table(skin);
        equipGrid.defaults().pad(15);

        TextureAtlas.AtlasRegion[] slotBgTextures = new TextureAtlas.AtlasRegion[]{
            atlas.findRegion("equip_slot"),
            atlas.findRegion("armor_slot"),
            atlas.findRegion("mount_slot"),
            atlas.findRegion("accessory_slot")
        };
        TextureAtlas.AtlasRegion[] itemTextures = new TextureAtlas.AtlasRegion[]{
            atlas.findRegion("armyBreakingSword"),
            atlas.findRegion("obsidianArmor"),
            atlas.findRegion("redHare"),
            atlas.findRegion("military_strategy")
        };

        for (int i = 0; i < slotBgTextures.length; i++) {
            TextureAtlas.AtlasRegion slotBg = slotBgTextures[i];
            TextureAtlas.AtlasRegion itemTex = itemTextures[i];

            Stack slot = new Stack();

            // 底图
            Image bgImg = new Image(slotBg);
            bgImg.setSize(260, 260);
            bgImg.setScaling(Scaling.stretch);
            slot.add(bgImg);

            // 装备图 + 名称
            if (itemTex != null) {
                // 装备图
                Image itemImg = new Image(itemTex);
                itemImg.setSize(240, 240);
                itemImg.setScaling(Scaling.fit);
                Container<Image> itemContainer = new Container<>(itemImg);
                itemContainer.center();
                slot.add(itemContainer);

                // 装备名称
                String name = equipmentNames.get(i);
                Label.LabelStyle nameStyle = new Label.LabelStyle(font, Color.WHITE);
                Label nameLabel = new Label(name, nameStyle);
                nameLabel.setAlignment(Align.center);

                Container<Label> labelContainer = new Container<>(nameLabel);
                labelContainer.bottom().fillX().padBottom(8);
                slot.add(labelContainer);
            }

            equipGrid.add(slot)
                .size(260, 260)
                .pad(10);
        }

        equipmentSection.add(equipGrid)
            .expandX()
            .fillX()
            .pad(15)
            .padBottom(20)
            .row();

        middleColumn.add(equipmentSection)
            .fillX()
            .row();

        // 大分割栏
        middleColumn.add(new Image(atlas.findRegion("divider_h")))
            .fillX()
            .padTop(10)
            .padBottom(10)
            .row();


        // ----- 生涯战绩 -----
        Table careerSection = new Table(skin);

        // 生涯 选项卡
        Label careerTab = new Label("生涯", skin, "tabInfoStyle");
        careerTab.setFontScale(1.4f);
        careerTab.setAlignment(Align.center);
        careerSection.add(careerTab)
            .left()
            .minWidth(120)
            .minHeight(55)
            .padLeft(15)
            .padTop(10)
            .padBottom(20)
            .expandX()
            .row();

        // 三列 带竖向分隔栏
        Table row2 = new Table(skin);
        row2.defaults().align(Align.top);

        Table statsTable = new Table(skin);
        statsTable.defaults().pad(5).align(Align.center);

        String[] careerStatNamesTop = {
            "白兵胜利", "单挑胜利", "计策成功", "战役胜利",
            "武将击杀", "武将俘虏", "内政成功", "白兵击杀"
        };

        String[] careerStatNamesBottom = {
            "白兵失败", "单挑失败", "计策失败", "战役失败",
            "武将死亡", "武将被俘", "外交成功", "计策击杀"
        };

        for (int i = 0; i < careerStatNamesTop.length; i++) {

            TextureAtlas.AtlasRegion bgTex = ((i == careerStatNamesTop.length - 1)
                            ? atlas.findRegion("bigCareerBox")
                            : atlas.findRegion("stat_win"));

            Image bgImg = new Image(bgTex);
            bgImg.setScaling(Scaling.stretch);

            // 垂直排列
            Label nameLabel = new Label(careerStatNamesTop[i], skin);
            nameLabel.setFontScale(0.8f);
            VerticalGroup vg = new VerticalGroup();
            vg.space(2);// 行间距
            vg.center();
            vg.addActor(nameLabel);
            vg.addActor(new Label("0", new Label.LabelStyle(font, Color.GREEN)));

            Stack statBox = new Stack(bgImg,vg);

                statsTable
                    .add(statBox)
                    .size(bgImg.getWidth(), bgImg.getHeight())
                    .padRight(5);
        }
        statsTable.row().padTop(5);

        for (int i = 0; i < careerStatNamesBottom.length; i++) {

            TextureAtlas.AtlasRegion bgTex = ((i == careerStatNamesBottom.length - 1)
                            ? atlas.findRegion("bigCareerBox")
                            : atlas.findRegion("stat_win"));

            Image bgImg = new Image(bgTex);
            bgImg.setScaling(Scaling.stretch);

            Label nameLabel = new Label(careerStatNamesBottom[i], skin);
            nameLabel.setFontScale(0.8f);
            VerticalGroup vg = new VerticalGroup();
            vg.space(2);
            vg.center();
            vg.addActor(nameLabel);
            vg.addActor(new Label("0", new Label.LabelStyle(font, Color.GREEN)));

            Stack statBox = new Stack(bgImg,vg);

                statsTable
                    .add(statBox)
                    .size(bgImg.getWidth(), bgImg.getHeight())
                    .padRight(5);
        }
        statsTable.row();

        // statsTable加入到row2的第一列，垂直方向填满高度
        row2.add(statsTable)
            .fillY()
            .expandY();

        // 竖直分隔线
        Image vertSep1 = new Image(atlas.findRegion("verticalSeparator"));
        vertSep1.setScaling(Scaling.fillY);// 纵向填充
        row2.add(vertSep1)
            .fillY()
            .padLeft(10).padRight(10)
            .expandY();

        // ----- 3×3属性标签网格 -----
        Table attrTable = new Table(skin);

        // 默认每个格子内边距5px 统一大小 内容居中
        attrTable.defaults()
            .pad(5)
            .minWidth(90).minHeight(40)
            .align(Align.center);

        String[] attrTitles = {
            "五行","亲密","称号",
            "战队","野心","",
            "","",""
        };

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                String title = attrTitles[idx];
                Label lbl = new Label(title, skin, "titleBoxStyle");
                lbl.setFontScale(1.1f);
                lbl.setAlignment(Align.center);
                attrTable.add(lbl);
            }
            attrTable.row();
        }

        row2.add(attrTable)
            .fillY()
            .expandY();

        // 竖直分隔线
        Image vertSep2 = new Image(atlas.findRegion("verticalSeparator"));
        vertSep2.setScaling(Scaling.fillY);
        row2.add(vertSep2)
            .fillY()
            .padLeft(10).padRight(10)
            .expandY();

        // 关系栏
        Table relationColumn = new Table(skin);
        relationColumn.top();
        relationColumn.defaults().pad(5).align(Align.center);

        Label.LabelStyle relLabelStyle = new Label.LabelStyle(font, Color.RED);
        relLabelStyle.background = new TextureRegionDrawable(
            new TextureRegion(atlas.findRegion("shortHorizontal"))
        );

        Label relationLabel = new Label("关系", relLabelStyle);
        relationLabel.setAlignment(Align.center);
        relationColumn.add(relationLabel)
            .minWidth(67)
            .minHeight(47)
            .row();

        // 单独显示图标
        Image relationIcon = new Image(atlas.findRegion("icon"));
        relationIcon.setScaling(Scaling.fit);
        relationColumn.add(relationIcon)
            .minWidth(65)
            .minHeight(59)
            .row();

        // 把relationColumn加到row2的第三列
        row2.add(relationColumn)
            .fillY()
            .expandY();

        // 把row2加入careerSection
        careerSection.add(row2)
            .padLeft(15)
            .fillX()
            .expandY()
            .row();

        // careerSection加入到middleColumn
        middleColumn.add(careerSection)
            .fillX()
            .expandY()
            .top()
            .row();

    }

    private void populateRightColumn(Table rightColumn) {
        rightColumn.top().pad(10);
        //rightColumn.setDebug(true);

        // ----- 列传显示区 -----
        Image bioBgImage = new Image(atlas.findRegion("scroll_bg"));

        String biographyExample = "这里是武将的生平事迹...\n" +
            "关羽（约160－220年），本字长生，后改字云长，河东郡解县（今山西运城）人。\n" +
            "东汉末年名将，早期跟随刘备辗转各地，曾被曹操生擒，于白马坡斩杀袁绍大将颜良，与张飞一同被称为万人敌。\n" +
            "赤壁之战后，刘备助东吴周瑜攻打南郡曹仁，别遣关羽绝北道，阻挡曹操援军，曹仁退走后，关羽被刘备任命为襄阳太守。\n" +
            "刘备入益州，关羽留守荆州。\n建安二十四年，关羽围襄樊，曹操派于禁前来增援，关羽擒获于禁，斩杀庞德，威震华夏，曹操曾想迁都以避其锐。\n" +
            "后曹操派徐晃前来增援，东吴吕蒙又偷袭荆州，关羽腹背受敌，兵败被杀。";
        Label biographyLabel = new Label(biographyExample, skin);
        biographyLabel.setWrap(true);
        biographyLabel.setAlignment(Align.topLeft);

        Stack bioStack = new Stack();
        bioStack.add(bioBgImage);
        // 把Label放在bioStack的内边距容器里，以留出背景四周的边框
        Container<Label> bioTextContainer = new Container<>(biographyLabel);
        bioTextContainer.pad(20);
        bioTextContainer.fill();
        bioStack.add(bioTextContainer);

        // 添加到右列，并让它占据中部主要空间
        rightColumn.add(bioStack)
            .expandX()
            .fillX()
            .height(Value.percentHeight(0.8f, rightColumn))
            .row();

        // ----- 翻页与返回按钮组 -----
        Table navButtons = new Table(skin);

        TextButton prevButton = new TextButton("上一页", skin, "default");
        TextButton nextButton = new TextButton("下一页", skin, "default");
        TextButton backButton = new TextButton("", skin, "backStyle");

        prevButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                biographyLabel.setText(getPreviousBiography());
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                biographyLabel.setText(getNextBiography());
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // 返回上个界面
                //backToPreviousScreen();
            }
        });

        // 平分三份宽度
        navButtons.add(prevButton)
            .prefWidth(Value.percentWidth(0.3f, navButtons))
            .height(50)
            .pad(5)
            .expandX();
        navButtons.add(nextButton)
            .prefWidth(Value.percentWidth(0.3f, navButtons))
            .height(50)
            .pad(5)
            .expandX();
        navButtons.add(backButton)
            .prefWidth(Value.percentWidth(0.3f, navButtons))
            .height(50)
            .pad(5)
            .expandX();

        rightColumn.add(navButtons)
            .bottom()
            .expandY()
            .fillX()
            .padTop(10)
            .padBottom(15)
            .row();
    }

    private String getPreviousBiography() {
        // TODO: 实现翻页逻辑，返回对应页的文本
        return "这是上一页的列传内容……";
    }

    private String getNextBiography() {
        // TODO: 实现翻页逻辑，返回对应页的文本
        return "这是下一页的列传内容……";
    }

    private void backToPreviousScreen() {
        // game.setScreen(previousScreen);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 更新视口的世界尺寸和相机（如果窗口大小改变）
        // viewport.apply(); // 通常在 resize 中调用 update，然后 stage.draw 会用 viewport 的相机
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            // 如果为true，则将相机居中。
            // 对于ExtendViewport，这会根据新的屏幕大小更新其世界大小，
            // 以及如果设置了最小/最大尺寸，则居中相机。
            viewport.update(width, height, true);
        }
        // 如果有其他需要根据屏幕尺寸动态调整的UI元素（比如字体大小），可以在这里处理
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }


    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (radarChart != null) radarChart.dispose();

        Assets.inst().dispose();
    }

    // --- 自定义雷达图 Actor ---
    // (RadarChartActor)
    public static class RadarChartActor extends Actor {
        private ShapeRenderer shapeRenderer;
        private float[] stats;
        private int dimensions;
        private BitmapFont font;
        private String[] axisLabels;

        // 配色略...
        private Color axisColor          = new Color(0.7f, 0.7f, 0.7f, 1f);
        private Color polygonColor       = new Color(0.8f, 0.6f, 0.2f, 0.5f);
        private Color polygonBorderColor = new Color(0.8f, 0.6f, 0.2f, 1f);

        /**
         * @param initialStats 初始数值数组，长度 = 维度个数
         * @param font         用来画标签的 BitmapFont
         * @param axisLabels   每个维度的标签，比如 {"武","统","政","德","知"}
         */
        public RadarChartActor(float[] initialStats, BitmapFont font, String[] axisLabels) {
            this.stats       = initialStats;
            this.dimensions  = initialStats.length;
            this.font        = font;
            this.axisLabels  = axisLabels;
            this.shapeRenderer = new ShapeRenderer();
        }

        public void setStats(float[] newStats) {
            this.stats      = newStats;
            this.dimensions = newStats.length;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            //中断batch，切到shapeRenderer
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            // 应用Actor的变换
            shapeRenderer.translate(getX()+getOriginX(), getY()+getOriginY(), 0);
            shapeRenderer.rotate(0,0,1,getRotation());
            shapeRenderer.scale(getScaleX(),getScaleY(),1);
            shapeRenderer.translate(-getOriginX(), -getOriginY(), 0);

            float w = getWidth(), h = getHeight();
            float centerX = w/2, centerY = h/2;
            float radius = Math.min(w,h)/2 * 0.9f;
            float angleStep = (float)(2*Math.PI/dimensions);

            // 画轴线
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(axisColor);
            for(int i=0;i<dimensions;i++){
                float a = (float)(Math.PI/2 - i*angleStep);
                shapeRenderer.line(centerX, centerY,
                    centerX + radius*(float)Math.cos(a),
                    centerY + radius*(float)Math.sin(a));
            }
            shapeRenderer.end();

            // 画同心多边形网格
            int levels=4;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for(int lvl=1;lvl<=levels;lvl++){
                float r = radius * (lvl/(float)levels);
                for(int i=0;i<dimensions;i++){
                    float a1 = (float)(Math.PI/2 - i*angleStep);
                    float a2 = (float)(Math.PI/2 - ((i+1)%dimensions)*angleStep);
                    float x1 = centerX + r*(float)Math.cos(a1), y1 = centerY + r*(float)Math.sin(a1);
                    float x2 = centerX + r*(float)Math.cos(a2), y2 = centerY + r*(float)Math.sin(a2);
                    shapeRenderer.line(x1,y1,x2,y2);
                }
            }
            shapeRenderer.end();

            // 画数据多边形
            if(stats!=null && stats.length==dimensions){
                // 填充
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(polygonColor);
                for(int i=0;i<dimensions;i++){
                    int ni = (i+1)%dimensions;
                    float v1 = MathUtils.clamp(stats[i],0f,1f)*radius;
                    float v2 = MathUtils.clamp(stats[ni],0f,1f)*radius;
                    float a1 = (float)(Math.PI/2 - i*angleStep);
                    float a2 = (float)(Math.PI/2 - ni*angleStep);
                    shapeRenderer.triangle(centerX,centerY,
                        centerX+v1*(float)Math.cos(a1), centerY+v1*(float)Math.sin(a1),
                        centerX+v2*(float)Math.cos(a2), centerY+v2*(float)Math.sin(a2));
                }
                shapeRenderer.end();
                // 边框
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(polygonBorderColor);
                for(int i=0;i<dimensions;i++){
                    int ni=(i+1)%dimensions;
                    float v1 = MathUtils.clamp(stats[i],0f,1f)*radius;
                    float v2 = MathUtils.clamp(stats[ni],0f,1f)*radius;
                    float a1 = (float)(Math.PI/2 - i*angleStep);
                    float a2 = (float)(Math.PI/2 - ni*angleStep);
                    shapeRenderer.line(centerX+v1*(float)Math.cos(a1), centerY+v1*(float)Math.sin(a1),
                        centerX+v2*(float)Math.cos(a2), centerY+v2*(float)Math.sin(a2));
                }
                shapeRenderer.end();
            }

            // 重启batch，画文字
            batch.begin();
            float labelOffset = 18;// 文本离顶点再往外扩 18px
            for(int i=0;i<dimensions;i++){
                float a = (float)(Math.PI/2 - i*angleStep);
                // 世界坐标 Actor 左下角 + (centerX,centerY) + (radius+offset)*cos/sin
                float x = getX() + centerX + (radius+labelOffset)*(float)Math.cos(a);
                float y = getY() + centerY + (radius+labelOffset)*(float)Math.sin(a);
                String text = axisLabels[i];
                GlyphLayout layout = new GlyphLayout(font, text);
                // 居中：x - width/2, y + height/2
                font.draw(batch,
                    text,
                    x - layout.width/2,
                    y + layout.height/2);
            }
            // 这里不用自己end()，Stage.draw() 最后会统一end
        }

        public void dispose() {
            shapeRenderer.dispose();
        }
    }

}
