package tech.bskplu.ui.generallore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WujiangScreen extends ApplicationAdapter {

    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private BitmapFont font;

    // Textures (应通过 AssetManager 管理，此处为演示直接加载)
    private Texture backgroundTexture;
    private Texture portraitFrame;
    private Texture portraitTexture;
    private Texture genderMaleTexture;// 性别男图标
    // private Texture genderFemaleTexture;
    private Texture greenBgTexture;// 技能绿色背景条
    private Texture titleBgTexture;// 称号背景框 (如 "超级")
    private Texture separatorTexture;// 大分割栏
    private Texture textBoxBackgroundTexture;//势力、城市等内容的背景框
    private Texture twoDashesTexture;// 二分栏
    private Texture weaponSlotBgTexture, armorSlotBgTexture, mountSlotBgTexture, bookSlotBgTexture;// 装备槽背景
    private Texture weaponItemTexture, armorItemTexture, mountItemTexture, bookItemTexture;// 装备物品图片
    private Texture careerBoxBgTexture;// 战绩格子背景
    private Texture buttonUpTexture, buttonDownTexture;// 按钮背景
    private Texture generalFrameTexture;// “武将资料” 头框
    private Texture greenVerticalTexture;// 绿色竖条
    private Texture biographyBgTexture;// 列传背景

    // 自定义雷达图 Actor
    private RadarChartActor radarChart;
    private float[] currentStats = {0.42f, 0.59f, 0.79f, 0.89f, 0.89f, 0.89f};

    // UI 元素引用 (如果需要动态修改)
    private Label levelLabel, expLabel;
    private Label forceValueLabel, cityValueLabel, salaryValueLabel;

    // 体 武 知 德 统 政 忠 相性
    private Label bodyLabel, martialLabel, intellectLabel, virtueLabel, leadershipLabel, politicsLabel, loyaltyLabel, affinityLabel;
    private Label troopTypeLabel, specialtyLabel, troopCountLabel, mobilityLabel;

    // 定义最小/目标世界尺寸，ExtendViewport将基于此进行扩展
    private static final float WORLD_WIDTH = 2400;
    private static final float WORLD_HEIGHT = 1080;


    public WujiangScreen() {
        // 构造函数中可以初始化一些东西，但主要加载和UI构建在 show() 中
    }

    @Override
    public void create() {
        // 修改 Viewport 类型为 ExtendViewport
        // ExtendViewport 会保持世界宽高比，并在需要时扩展世界区域以填充屏幕，而不是留黑边。
        // 它使用最小世界宽度和高度。如果屏幕更大，世界也会在那个维度上更大。
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, new OrthographicCamera());
        stage    = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);


        // 1. 加载资源
        loadAssets(); // 加载纹理等

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

         leftColumn.setDebug(true);
         //middleColumn.setDebug(true);
         //rightColumn.setDebug(true);

        // 6. 填充每一列
        populateLeftColumn(leftColumn);
        populateMiddleColumn(middleColumn);
        populateRightColumn(rightColumn);

        // 7. 将列添加到根 Table
        // 使用百分比宽度，这对于ExtendViewport是合适的，列会根据根Table的实际宽度（可能已扩展）分配空间
        rootTable.add(leftColumn).prefWidth(Value.percentWidth(0.20f, rootTable)).expandY().fillY().padLeft(20).padTop(20).padBottom(20);
        rootTable.add(middleColumn).prefWidth(Value.percentWidth(0.48f, rootTable)).expandY().fillY().padTop(20).padBottom(20).padLeft(10).padRight(10);
        rootTable.add(rightColumn).prefWidth(Value.percentWidth(0.32f, rootTable)).expandY().fillY().padRight(20).padTop(20).padBottom(20);
    }

    private void loadAssets() {
        // 素材纹理加载，建议使用AssetManager
        try {
            backgroundTexture = new Texture(Gdx.files.internal("bg_warlord_panel.png"));
            portraitFrame = new Texture(Gdx.files.internal("portrait_frame.png"));
            portraitTexture = new Texture(Gdx.files.internal("portrait.png"));
            genderMaleTexture = new Texture(Gdx.files.internal("icon_male.png"));
            separatorTexture = new Texture(Gdx.files.internal("divider_h.png"));
            twoDashesTexture = new Texture(Gdx.files.internal("two_dashes.png"));

            greenBgTexture = new Texture(Gdx.files.internal("relationship_slot.png"));
            titleBgTexture = new Texture(Gdx.files.internal("relationship_slot.png"));
            textBoxBackgroundTexture = new Texture(Gdx.files.internal("relationship_slot.png"));

            weaponSlotBgTexture = new Texture(Gdx.files.internal("equip_slot.png"));
            armorSlotBgTexture = new Texture(Gdx.files.internal("armor_slot.png"));
            mountSlotBgTexture = new Texture(Gdx.files.internal("mount_slot.png"));
            bookSlotBgTexture = new Texture(Gdx.files.internal("accessory_slot.png"));

            weaponItemTexture = new Texture(Gdx.files.internal("ArmyBreakingSword.png"));
            armorItemTexture = new Texture(Gdx.files.internal("ObsidianArmor.png"));
            mountItemTexture = new Texture(Gdx.files.internal("RedHare.png"));
            // bookItemTexture = new Texture(Gdx.files.internal("Army-Breaking Sword.png"));

            careerBoxBgTexture = new Texture(Gdx.files.internal("stat_win.png"));

            buttonUpTexture = new Texture(Gdx.files.internal("button_down.png"));
            buttonDownTexture = new Texture(Gdx.files.internal("button_up.png"));

            generalFrameTexture = new Texture(Gdx.files.internal("GeneralFrame.png"));
            greenVerticalTexture= new Texture(Gdx.files.internal("green_vertical.png"));
            biographyBgTexture  = new Texture(Gdx.files.internal("scroll_bg.png"));

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Alibaba-PuHuiTi-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 16;
            param.magFilter = Texture.TextureFilter.Linear;
            param.minFilter = Texture.TextureFilter.Linear;

            param.characters = FreeTypeFontGenerator.DEFAULT_CHARS +
                "/妃子武将资料等级称号技能主公势力城市俸禄体知德统政忠相性部队兵种专精机动白胜利失败单挑计策战役击杀俘虏死亡外交成功生涯人物关系上一页下一页返回出仕超级人五行" +
                "这里是的平事迹可以有很多行文字关羽约年本长后改云河东郡解县今山西运汉末名早期跟随刘备辗转各地曾被曹操擒于马坡斩袁绍大颜良张飞同为万人敌赤壁之助吴周瑜攻打南仁别遣绝北道阻挡援军退走任命襄阳太守入益州留荆建安二十四围樊派禁前来增获庞威震华夏想迁都避其锐徐晃吕蒙又偷袭腹背受";



            // 字体初始化，默认Libgdx默认字体，不支持中文
            font = generator.generateFont(param);
            generator.dispose();

        } catch (Exception e) {
            Gdx.app.error("AssetLoad", "Error loading textures or font", e);
        }
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default-font", font, BitmapFont.class);

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", defaultLabelStyle);

        Label.LabelStyle goldenLabelStyle = new Label.LabelStyle(font, Color.GOLD);
        skin.add("golden", goldenLabelStyle);

        NinePatchDrawable skillBgDrawable = new NinePatchDrawable(new NinePatch(greenBgTexture, 6, 6, 2, 2));
        Label.LabelStyle skillLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        skillLabelStyle.background = skillBgDrawable;
        skin.add("skillStyle", skillLabelStyle);

        NinePatchDrawable titleBgDrawable = new NinePatchDrawable(new NinePatch(titleBgTexture, 6, 6, 2, 2));
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(font, Color.BLACK);
        titleLabelStyle.background = titleBgDrawable;
        skin.add("titleBoxStyle", titleLabelStyle);

        TextureRegionDrawable textBoxDrawable = new TextureRegionDrawable(new TextureRegion(textBoxBackgroundTexture));
        Label.LabelStyle contentBoxStyle = new Label.LabelStyle(font, Color.WHITE);
        contentBoxStyle.background = textBoxDrawable;
        skin.add("contentBoxStyle", contentBoxStyle);

        // 武将当前身份 素材（妃子？）
        NinePatchDrawable identityBgDrawable = new NinePatchDrawable(new NinePatch(twoDashesTexture, 8, 8, 8, 8));
        Label.LabelStyle identityStyle = new Label.LabelStyle(font, Color.WHITE);
        identityStyle.background = identityBgDrawable;
        skin.add("identityStyle", identityStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        skin.add("default", textButtonStyle);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        skin.add("default", scrollPaneStyle);
    }

    private void populateLeftColumn(Table leftColumn) {
        leftColumn.top().pad(10);

        // 创建 子Table
        Table headerRow = new Table(skin);
        headerRow.defaults().row();

        // 顶部一级标题“武将资料”
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        // 添加了名为 "GeneralFrame" 的背景素材(测试，后续统一管理)
        titleStyle.background = new NinePatchDrawable(new NinePatch(generalFrameTexture, 8, 8, 8, 8));
        skin.add("headerTitle", titleStyle);
        Label pageTitle = new Label("武将资料", skin, "headerTitle");
        pageTitle.setFontScale(1.5f);// 稍微放大

        Label identityLabel = new Label("妃子", skin, "identityStyle");
        identityLabel.setFontScale(1.0f);

        headerRow.add(pageTitle).left().pad(5,0,10,10).height(65).width(205);
        headerRow.add(identityLabel).right().pad(5,10,10,0);

        leftColumn.add(headerRow)
            .expandX()
            .fillX()
            .row();

        // 人物基本资料（头像性别出仕状态）Group
        Group portraitGroup = new Group();

        // 头像
        Image portraitImg = new Image(portraitTexture);
        Image portraitBgImg = new Image(portraitFrame);

        // 偏移计算 头像略小于背景框 推到相对中心位置
        float offsetX =20;
        float centering = (245 - 224) / 2f;
        portraitBgImg.setSize(245, 245);
        portraitBgImg.setPosition(offsetX, 0);
        portraitImg.setSize(224, 224);
        portraitImg.setPosition(offsetX + centering, centering);

        portraitGroup.addActor(portraitBgImg);
        portraitGroup.addActor(portraitImg);

        // 性别圆框
        Image genderIcon = new Image(genderMaleTexture);
        genderIcon.setSize(50, 55);
        // “挂”在头像左上角外面一点，x = -iconW/2, y = portraitH - iconH/2
        genderIcon.setPosition(-50, 180 - 50);
        portraitGroup.addActor(genderIcon);

        // 自由布局组放回布局表格
        leftColumn.add(portraitGroup)
            .size(245, 245)
            .center()
            .padBottom(15)
            .row();

        //出仕竖条：放在左侧中部
        Label.LabelStyle greenBarStyle = new Label.LabelStyle(font, Color.GREEN);
        greenBarStyle.background = new NinePatchDrawable(new NinePatch(
            new NinePatch(greenVerticalTexture, 4, 4, 4, 4)));
        skin.add("greenBar", greenBarStyle);
        Label statusBar = new Label("出仕", skin, "greenBar");
        Container<Label> statusC = new Container<>(statusBar);
        statusC.left().padLeft(-65).padTop(-110);
        portraitGroup.addActor(statusC);


        // ----- 武将姓名及官位（后加）---------------
        leftColumn.row();
        leftColumn.add(new Label("九爷", skin, "titleBoxStyle")).padTop(5).row();
        leftColumn.add(new Label("无官职", skin, "contentBoxStyle")).padTop(5).row();

        radarChart = new RadarChartActor(currentStats);
        leftColumn.add(radarChart).width(200).height(180).padBottom(20).row();

        Table levelExpTable = new Table(skin);
        levelExpTable.add(new Label("等级", skin)).left().padRight(10);
        levelLabel = new Label("8", skin);
        levelExpTable.add(levelLabel).left().row();

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        Pixmap greenPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        greenPixmap.setColor(Color.GREEN);
        greenPixmap.fill();
        progressBarStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(new Texture(greenPixmap)));
        greenPixmap.dispose();

        Pixmap grayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        grayPixmap.setColor(Color.DARK_GRAY);
        grayPixmap.fill();
        progressBarStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(grayPixmap)));
        grayPixmap.dispose();

        ProgressBar expBar = new ProgressBar(0, 500, 1, false, progressBarStyle);
        expBar.setValue(500);
        // 让经验条可以横向填充可用空间，但给一个最小和首选宽度
        levelExpTable.add(expBar).colspan(2).minWidth(150).prefWidth(180).expandX().fillX().height(20).padTop(5).row();

        expLabel = new Label("500", skin);
        levelExpTable.add(expLabel).colspan(2).center().padTop(5);

        leftColumn.add(levelExpTable).padBottom(30).row();

        Table titlesTable = new Table(skin);
        titlesTable.add(new Label("称号:", skin)).left().padRight(10);
        titlesTable.add(new Label("超级", skin, "titleBoxStyle")).pad(3);
        titlesTable.add(new Label("人将", skin, "titleBoxStyle")).pad(3);
        titlesTable.add(new Label("五行", skin, "titleBoxStyle")).pad(3);
        // fillX让称号行能利用横向空间
        leftColumn.add(titlesTable).fillX().padBottom(10).row();

        Table skillsTable = new Table(skin);
        skillsTable.add(new Label("技能:", skin)).left().padRight(10).top();

        Table skillItemsTable = new Table();
        skillItemsTable.add(new Label("铁壁", skin, "skillStyle")).pad(3);
        skillItemsTable.add(new Label("无双", skin, "skillStyle")).pad(3);
        skillsTable.add(skillItemsTable).left();
        // fillX 和 expandY().top() 帮助技能部分在垂直方向上良好定位并利用横向空间
        leftColumn.add(skillsTable).fillX().expandY().top();
    }

    private void populateMiddleColumn(Table middleColumn) {
        middleColumn.top().pad(10);

        // ----- 1. 标题 & 选项卡 -----
        Table header = new Table(skin);
        // — 主标题 “主公”(老Demo上有)
        Label titleLabel = new Label("主公", skin, "titleBoxStyle");
        header.add(titleLabel)
            .colspan(2)
            .center()
            .padBottom(10)
            .row();

        // — 选项卡按钮- 个人/能力
        TextButton personalTab = new TextButton("个人", skin, "default");
        TextButton abilityTab = new TextButton("能力", skin, "default");
        header.add(personalTab).padRight(5);
        header.add(abilityTab);
        middleColumn.add(header).fillX().row();

        // ----- 2. 个人信息 -----
        Table personalInfo = new Table(skin);
        // 势力
        personalInfo.add(new Label("势力", skin, "golden")).padRight(5);
        forceValueLabel = new Label("九爷", skin, "contentBoxStyle");
        personalInfo.add(forceValueLabel).padRight(20);
        // 城市
        personalInfo.add(new Label("城市", skin, "golden")).padRight(5);
        cityValueLabel = new Label("洛阳", skin, "contentBoxStyle");
        personalInfo.add(cityValueLabel).padRight(20);
        // 俸禄
        personalInfo.add(new Label("俸禄", skin, "golden")).padRight(5);
        salaryValueLabel = new Label("30/月", skin);
        personalInfo.add(salaryValueLabel);
        middleColumn.add(personalInfo).fillX().padBottom(10).row();

        // ------ 3. 能力数值 ------
        Table abilities = new Table(skin);

        abilities.add(new Label("体", skin, "golden")).padRight(5);
        bodyLabel = new Label("99", skin, "golden");
        abilities.add(bodyLabel).padRight(20);

        abilities.add(new Label("武", skin, "golden")).padRight(5);
        martialLabel = new Label("99", skin, "golden");
        abilities.add(martialLabel).padRight(20);

        abilities.add(new Label("知", skin, "golden")).padRight(5);
        intellectLabel = new Label("99", skin, "golden");
        abilities.add(intellectLabel).padRight(20);

        abilities.add(new Label("德", skin, "golden")).padRight(5);
        virtueLabel = new Label("99", skin, "golden");
        abilities.add(virtueLabel).padRight(20);

        abilities.add(new Label("统", skin, "golden")).padRight(5);
        leadershipLabel = new Label("99", skin, "golden");
        abilities.add(leadershipLabel).padRight(20);

        abilities.add(new Label("政", skin, "golden")).padRight(5);
        politicsLabel = new Label("99", skin, "golden");
        abilities.add(politicsLabel).padRight(20);

        abilities.add(new Label("忠", skin, "golden")).padRight(5);
        loyaltyLabel = new Label("99", skin, "golden");
        abilities.add(loyaltyLabel).padRight(20);

        abilities.add(new Label("相性", skin, "golden")).padRight(5);
        affinityLabel = new Label("99", skin, "golden");
        abilities.add(affinityLabel);


        middleColumn.add(abilities)
            .fillX()// 横向填满可用宽度
            .padBottom(20)// 底部间距
            .row();


        // ----- 4. 部队信息 -----
        Table troopsSection = new Table(skin);
        // 板块标题
        troopsSection.add(new Label("部队", skin, "titleBoxStyle"))
            .left().padBottom(5).row();

        // 兵种 / 专精 / 兵力 / 机动
        Table troopsInfo = new Table(skin);

        troopsInfo.add(new Label("兵种", skin)).padRight(5);
        troopTypeLabel = new Label("山军", skin, "contentBoxStyle");
        troopsInfo.add(troopTypeLabel).padRight(20);

        troopsInfo.add(new Label("专精", skin)).padRight(5);
        specialtyLabel = new Label("剑", skin, "contentBoxStyle");
        troopsInfo.add(specialtyLabel).padRight(20);

        troopsInfo.add(new Label("兵力", skin)).padRight(5);
        troopCountLabel = new Label("3000", skin, "contentBoxStyle");
        troopsInfo.add(troopCountLabel).padRight(20);

        troopsInfo.add(new Label("机动", skin)).padRight(5);
        mobilityLabel = new Label("20", skin, "contentBoxStyle");
        troopsInfo.add(mobilityLabel);
        troopsSection.add(troopsInfo).left().padBottom(20).row();

        middleColumn.add(troopsSection).fillX().row();

        // 大分割栏
        middleColumn.add(new Image(separatorTexture))
            .fillX()
            .padBottom(10)
            .row();

        // ----- 5. 装备槽 -----
        Table equipmentSection = new Table(skin);
        equipmentSection.add(new Label("装备", skin, "titleBoxStyle"))
            .left().padBottom(5).row();

        Table equipGrid = new Table(skin);

        Texture[] slotBgTextures = new Texture[]{
            weaponSlotBgTexture,
            armorSlotBgTexture,
            mountSlotBgTexture,
            bookSlotBgTexture
        };

        Texture[] itemTextures = new Texture[]{
            weaponItemTexture,
            armorItemTexture,
            mountItemTexture,
            bookItemTexture
        };

        for (int i = 0; i < slotBgTextures.length; i++) {
            Texture slotBg = slotBgTextures[i];
            Texture itemTex = itemTextures[i];

            Stack slot = new Stack();

            slot.add(new Image(slotBg));

            if (itemTex != null) {
                Image itemImg = new Image(itemTex);
                itemImg.setScaling(Scaling.fit);
                slot.add(itemImg);
            }

            equipGrid.add(slot)
                .size(160, 160)
                .pad(5);
        }

        equipmentSection.add(equipGrid)
            .left()
            .padBottom(20)
            .row();

        middleColumn.add(equipmentSection).fillX().row();

        // 大分割栏
        middleColumn.add(new Image(separatorTexture))
            .fillX()
            .padBottom(10)
            .row();


        // ----- 6. 生涯战绩 -----
        Table careerSection = new Table(skin);

        careerSection.add(new Label("生涯", skin, "titleBoxStyle"))
            .colspan(8).left().padBottom(10).row();

        String[] careerStatNamesTop = {
            "白兵胜利", "单挑胜利", "计策成功", "战役胜利",
            "武将击杀", "武将俘虏", "内政成功", "白兵击杀"
        };
        String[] careerStatNamesBottom = {
            "白兵失败", "单挑失败", "计策失败", "战役失败",
            "武将死亡", "武将被俘", "外交成功", "计策击杀"
        };

        for (int i = 0; i < careerStatNamesTop.length; i++) {
            // 字段名
            careerSection.add(new Label(careerStatNamesTop[i], skin))
                .padRight(5);
            // 数值框
            Stack box = new Stack(
                new Image(careerBoxBgTexture),
                new Label("0", skin)
            );
            if (i == careerStatNamesTop.length - 1) {
                // 最后一项“白兵击杀”用大格
                careerSection.add(box).size(75, 35).padRight(10);
            } else {
                // 其余用小格
                careerSection.add(box).size(55, 35).padRight(10);
            }
        }
        // 换行并加点顶距
        careerSection.row().padTop(5);

        for (int i = 0; i < careerStatNamesBottom.length; i++) {
            careerSection.add(new Label(careerStatNamesBottom[i], skin))
                .padRight(5);
            Stack box = new Stack(
                new Image(careerBoxBgTexture),
                new Label("0", skin)
            );
            if (i == careerStatNamesBottom.length - 1) {
                // 最后一项“计策击杀”用大格
                careerSection.add(box).size(75, 35).padRight(10);
            } else {
                careerSection.add(box).size(55, 35).padRight(10);
            }
        }

        middleColumn.add(careerSection)
            .fillX()
            .expandY()
            .top()
            .row();
    }

    private void populateRightColumn(Table rightColumn) {
        rightColumn.top().pad(10);

        // ----- 1. 人物关系按钮 -----
        TextButton relationsButton = new TextButton("人物关系", skin, "default");
        rightColumn.add(relationsButton)
            .prefWidth(Value.percentWidth(0.8f, rightColumn))
            .height(60)
            .center()
            .padBottom(20)
            .row();

        // ----- 2. 列传显示区 -----
        Image bioBgImage = new Image(biographyBgTexture);

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
        // 把 Label 放在 bioStack 的内边距容器里，以留出背景四周的边框
        Container<Label> bioTextContainer = new Container<>(biographyLabel);
        bioTextContainer.pad(20);
        bioTextContainer.fill();
        bioStack.add(bioTextContainer);

        // 2.4 添加到右列，并让它占据中部主要空间
        rightColumn.add(bioStack)
            .expand()
            .fillX()
            .height(Value.percentHeight(0.6f, rightColumn))  // 根据需要调整高度占比
            .row();

        // ----- 3. 翻页 & 返回 按钮组 -----
        Table navButtons = new Table(skin);

        TextButton prevButton = new TextButton("上一页", skin, "default");
        TextButton nextButton = new TextButton("下一页", skin, "default");
        TextButton backButton = new TextButton("返回", skin, "default");

        // 点击时更新 biographyLabel.setText(...)，此处示例
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
            .fillX()
            .bottom()
            .padTop(10)
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
//System.out.println("Viewport worldWidth="+viewport.getWorldWidth()
//    +", worldHeight="+viewport.getWorldHeight());
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
        if (font != null) font.dispose();

        if (backgroundTexture != null) backgroundTexture.dispose();
        if (portraitTexture != null) portraitTexture.dispose();
        if (genderMaleTexture != null) genderMaleTexture.dispose();
        if (greenBgTexture != null) greenBgTexture.dispose();
        if (titleBgTexture != null) titleBgTexture.dispose();
        if (textBoxBackgroundTexture != null) textBoxBackgroundTexture.dispose();
        if (weaponSlotBgTexture != null) weaponSlotBgTexture.dispose();
        if (armorSlotBgTexture != null) armorSlotBgTexture.dispose();
        if (mountSlotBgTexture != null) mountSlotBgTexture.dispose();
        if (bookSlotBgTexture != null) bookSlotBgTexture.dispose();
        if (weaponItemTexture != null) weaponItemTexture.dispose();
        if (armorItemTexture != null) armorItemTexture.dispose();
        if (mountItemTexture != null) mountItemTexture.dispose();
        // if (bookItemTexture != null) bookItemTexture.dispose();
        if (careerBoxBgTexture != null) careerBoxBgTexture.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();

        if (generalFrameTexture  != null) generalFrameTexture.dispose();
        if (greenVerticalTexture != null) greenVerticalTexture.dispose();
        if (biographyBgTexture   != null) biographyBgTexture.dispose();

        if (radarChart != null) radarChart.dispose();
    }

    // --- 自定义雷达图 Actor ---
    // (RadarChartActor 代码保持不变)
    public static class RadarChartActor extends Actor {
        private ShapeRenderer shapeRenderer;
        private float[] stats;
        private Color axisColor = new Color(0.5f, 0.5f, 0.5f, 1f);// 轴线颜色
        private Color polygonColor = new Color(0.2f, 0.8f, 0.2f, 0.6f);// 属性多边形颜色
        private Color polygonBorderColor = new Color(0.3f, 1f, 0.3f, 1f);// 属性多边形边框颜色

        public RadarChartActor(float[] initialStats) {
            this.stats = initialStats;
            this.shapeRenderer = new ShapeRenderer();
        }

        public void setStats(float[] newStats) {
            this.stats = newStats;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            // 应用Actor的位置和原点
            shapeRenderer.translate(getX() + getOriginX(), getY() + getOriginY(), 0);
            shapeRenderer.rotate(0, 0, 1, getRotation());
            shapeRenderer.scale(getScaleX(), getScaleY(), 1);
            shapeRenderer.translate(-getOriginX(), -getOriginY(), 0);


            float centerX = getWidth() / 2;
            float centerY = getHeight() / 2;
            float radius = Math.min(getWidth(), getHeight()) / 2 * 0.9f;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(axisColor);

            for (int i = 0; i < 6; i++) {
                float angleRad = (float) (Math.PI / 2 - (i * Math.PI / 3));
                float outerX = centerX + radius * (float) Math.cos(angleRad);
                float outerY = centerY + radius * (float) Math.sin(angleRad);
                shapeRenderer.line(centerX, centerY, outerX, outerY);
            }

            int concentricLevels = 4;
            for (int level = 1; level <= concentricLevels; level++) {
                float currentRadius = radius * ((float) level / concentricLevels);
                for (int i = 0; i < 6; i++) {
                    float angle1Rad = (float) (Math.PI / 2 - (i * Math.PI / 3));
                    float angle2Rad = (float) (Math.PI / 2 - ((i + 1) * Math.PI / 3));
                    float x1 = centerX + currentRadius * (float) Math.cos(angle1Rad);
                    float y1 = centerY + currentRadius * (float) Math.sin(angle1Rad);
                    float x2 = centerX + currentRadius * (float) Math.cos(angle2Rad);
                    float y2 = centerY + currentRadius * (float) Math.sin(angle2Rad);
                    shapeRenderer.line(x1, y1, x2, y2);
                }
            }
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(polygonColor);
            for (int i = 0; i < 6; i++) {
                if (stats == null || stats.length < 6) break;
                float statValue = Math.max(0, Math.min(1, stats[i]));

                float angle1Rad = (float) (Math.PI / 2 - (i * Math.PI / 3));
                float statRadius1 = radius * statValue;
                float x1 = centerX + statRadius1 * (float) Math.cos(angle1Rad);
                float y1 = centerY + statRadius1 * (float) Math.sin(angle1Rad);

                int next = (i + 1) % 6;
                float statValueNext = Math.max(0, Math.min(1, stats[next]));
                float angle2Rad = (float) (Math.PI / 2 - (next * Math.PI / 3));
                float statRadius2 = radius * statValueNext;
                float x2 = centerX + statRadius2 * (float) Math.cos(angle2Rad);
                float y2 = centerY + statRadius2 * (float) Math.sin(angle2Rad);

                shapeRenderer.triangle(centerX, centerY, x1, y1, x2, y2);
            }
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(polygonBorderColor);
            for (int i = 0; i < 6; i++) {
                if (stats == null || stats.length < 6) break;
                float statValue = Math.max(0, Math.min(1, stats[i]));
                float angle1Rad = (float) (Math.PI / 2 - (i * Math.PI / 3));
                float x1 = centerX + radius * statValue * (float) Math.cos(angle1Rad);
                float y1 = centerY + radius * statValue * (float) Math.sin(angle1Rad);

                int next = (i + 1) % 6;
                float statValueNext = Math.max(0, Math.min(1, stats[next]));
                float angle2Rad = (float) (Math.PI / 2 - (next * Math.PI / 3));
                float x2 = centerX + radius * statValueNext * (float) Math.cos(angle2Rad);
                float y2 = centerY + radius * statValueNext * (float) Math.sin(angle2Rad);
                shapeRenderer.line(x1, y1, x2, y2);
            }
            shapeRenderer.end();

            batch.begin();
        }

        public void dispose() {
            if (shapeRenderer != null) shapeRenderer.dispose();
        }
    }
}
