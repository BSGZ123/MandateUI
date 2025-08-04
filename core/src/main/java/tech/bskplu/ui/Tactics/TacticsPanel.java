package tech.bskplu.ui.Tactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * @ClassName: TacticsPanel
 * @Description: 小战场 (已重构为双Stage结构)
 * @Author BsKPLu
 * @Date 2025/6/28
 * @Version 2.0
 */
public final class TacticsPanel extends ApplicationAdapter {

    // --- 世界相关常量 ---
    private static final int WORLD_GRID_COLS = 16;
    private static final int WORLD_GRID_ROWS = 11;
    private static final boolean DEBUG_GRID = true;

    // --- UI相关常量 ---
    private static final int UI_PANEL_HEIGHT_PX = 327;
    private static final int UI_CENTER_WIDTH_PX = 1600;

    // --- 舞台与视口 ---
    private Stage worldStage;
    private Stage uiStage;
    private ExtendViewport worldViewport;
    private ScreenViewport uiViewport;
    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;

    // --- 调试与资源 ---
    private ShapeRenderer shapeRenderer;
    private InputMultiplexer inputMultiplexer;
    private BitmapFont buttonFont;
    private BitmapFont generalFont;
    private Texture panelBgTexture, buttonNormalTexture, buttonCheckedTexture;


    @Override
    public void create() {
        // 初始化世界和UI
        createWorld();
        createUi();

        // 设置输入处理器，优先处理UI事件
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(worldStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * 创建游戏世界相关的舞台和演员
     */
    private void createWorld() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(WORLD_GRID_COLS, WORLD_GRID_ROWS, worldCamera);
        worldStage = new Stage(worldViewport);

        if (DEBUG_GRID) {
            shapeRenderer = new ShapeRenderer();
            // 创建16×11的调试网格
            Table gridTable = new Table();
            gridTable.setFillParent(true);
            //gridTable.debugCell();

            for (int r = 0; r < WORLD_GRID_ROWS; r++) {
                for (int c = 0; c < WORLD_GRID_COLS; c++) {
                    gridTable.add().expand().fill();
                }
                gridTable.row();
            }
            worldStage.addActor(gridTable);
        }
    }

    /**
     * 创建UI相关的舞台和演员
     */
    private void createUi() {
        uiCamera = new OrthographicCamera();

        uiViewport = new ScreenViewport(uiCamera);

        uiStage = new Stage(uiViewport);

        loadAssets();

        Table rootUiTable = new Table();
        rootUiTable.setFillParent(true);
        //rootUiTable.setDebug(true);
        rootUiTable.bottom();
        uiStage.addActor(rootUiTable);

        Stack panelStack = new Stack();
        Image panelBgImage = new Image(panelBgTexture);
        panelBgImage.setScaling(Scaling.stretch);
        panelStack.add(panelBgImage);

        Table contentTable = new Table();
        contentTable.setDebug(true);
        panelStack.add(contentTable);

        rootUiTable.add(panelStack)
            .growX()
            .height(UI_PANEL_HEIGHT_PX);

        Table leftTable = new Table();
        Table centerTable = new Table();
        Table rightTable = new Table();

        // 使用像素宽度定义中间列，左右两列自适应填充
        contentTable.add(leftTable).grow();
        contentTable.add(centerTable).width(UI_CENTER_WIDTH_PX);
        contentTable.add(rightTable).grow();

        populateCenterPanel(centerTable);
        populateSidePanels(leftTable,rightTable);
    }

    /**
     * 填充左右两侧的面板容器
     * @param leftContainer 左侧主要容器
     * @param rightContainer 右侧主要容器
     */
    private void populateSidePanels(Table leftContainer, Table rightContainer){
        // --- 左侧面板 ---
        // 1.创建用于放置特殊元素的占位区
        Table specialLeft = new Table();
        specialLeft.setDebug(true);
        specialLeft.add(new Label("特殊区域\n(左)", new Label.LabelStyle(generalFont, Color.WHITE))).center();

        // 2.创建可复用的角色信息面板，并设置为'isLeft = true'(镜像)
        Table commonLeft = createCharacterPanel(true);

        // 3.将特殊区(15%)和通用区(85%)添加到左侧主容器
        leftContainer.add(specialLeft).growY().width(Value.percentWidth(0.15f, leftContainer));
        leftContainer.add(commonLeft).growY().width(Value.percentWidth(0.85f, leftContainer));

        // --- 右侧面板 ---
        // 1.创建用于放置特殊元素的占位区
        Table specialRight = new Table();
        specialRight.setDebug(true);
        specialRight.add(new Label("特殊区域\n(右)", new Label.LabelStyle(generalFont, Color.WHITE))).center();

        // 2.创建可复用的角色信息面板，并设置为'isLeft = false' (标准)
        Table commonRight = createCharacterPanel(false);

        // 3.将通用区(85%)和特殊区(15%)添加到右侧主容器
        rightContainer.add(commonRight).growY().width(Value.percentWidth(0.85f, rightContainer));
        rightContainer.add(specialRight).growY().width(Value.percentWidth(0.15f, rightContainer));
    }

    /**
     * 创建可复用的角色信息面板 先占位后精细调整
     * @param isLeft 如果为true，则生成左侧的镜像布局；否则生成右侧的标准布局
     * @return 一个包含角色信息的Table组件
     */
    private Table createCharacterPanel(boolean isLeft) {
        Table container = new Table();
        container.setDebug(true);

        Label.LabelStyle textStyle = new Label.LabelStyle(generalFont, Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(generalFont, Color.ORANGE);

        // --- 顶部信息栏 ---
        Table topBar = new Table();
        topBar.add(new Label(isLeft ? "长蛇" : "雁行", textStyle)).pad(5);
        topBar.add(new Label(isLeft ? "术 15" : "术 10", textStyle)).pad(5);
        topBar.add(new Label(isLeft ? "机 20" : "机 16", textStyle)).pad(5);
        topBar.add(new Label(isLeft ? "平" : "山", textStyle)).pad(5);

        // --- 主内容区 ---
        Table mainContent = new Table();
        // 使用一个已有贴图作为角色头像的占位符
        Image portraitPlaceholder = new Image(buttonNormalTexture);
        // 属性信息表格
        Table statsTable = new Table();
        String[] statNames = isLeft ?
            new String[]{"内连", "知", "政", "神威", "德", "统"} :
            new String[]{"武后", "知", "大奇", "智神", "德", "统"};
        for (int i = 0; i < statNames.length; i++) {
            statsTable.add(new Label(statNames[i], titleStyle)).right().padRight(10);
            statsTable.add(new Label("100", textStyle)).left().width(100);
            if (i % 2 == 1) { // 每两个属性换一行
                statsTable.row();
            }
        }

        // 根据isLeft标志位，决定头像和属性的左右位置，实现镜像效果
        if (isLeft) {
            mainContent.add(portraitPlaceholder).size(160, 200).pad(10);// 尺寸占位
            mainContent.add(statsTable).growX().left();
        } else {
            mainContent.add(statsTable).growX().right();
            mainContent.add(portraitPlaceholder).size(160, 200).pad(10);// 尺寸占位
        }

        // --- 最终组装 ---
        container.add(topBar).growX().left();// 顶部信息总是靠左对齐
        container.row();
        container.add(mainContent).grow();

        return container;

    }


    /**
     * 填充中央操作区域的4x4按钮
     * @param centerTable 中央布局表格
     */
    private void populateCenterPanel(Table centerTable) {
        // 创建按钮样式
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(buttonNormalTexture);
        buttonStyle.checked = new TextureRegionDrawable(buttonCheckedTexture);

        // 按钮文字和列分组
        String[] buttonTexts = {"待", "进", "围", "退"};
        // 为每一行创建一个按钮组，以确保每行只有一个按钮能被选中
        ButtonGroup<TextButton>[] rowGroups = new ButtonGroup[4];
        for (int i = 0; i < 4; i++) {
            rowGroups[i] = new ButtonGroup<>();
            rowGroups[i].setMaxCheckCount(1);// 最多一个被选中
            rowGroups[i].setMinCheckCount(0);// 允许全部不选中 (若要强制一个被选中，设为1)
        }

        // 创建并添加4x4按钮
        TextButton[][] buttons = new TextButton[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                TextButton button = new TextButton(buttonTexts[col], buttonStyle);
                buttons[row][col] = button;

                // 定义一个像素尺寸
                float buttonSizePx = 64f;
                centerTable.add(button).size(buttonSizePx).space(10f);// 64x64像素的按钮，10像素的间距

                rowGroups[row].add(button);
            }
            centerTable.row();
        }

        buttons[0][3].setChecked(true);
        buttons[1][1].setChecked(true);
        buttons[2][2].setChecked(true);
        buttons[3][0].setChecked(true);
    }

    private void loadAssets() {
        // 加载贴图
        panelBgTexture = new Texture(Gdx.files.internal("tactics/bg_panel.png"));
        buttonNormalTexture = new Texture(Gdx.files.internal("tactics/brown_square_bg.png"));
        buttonCheckedTexture = new Texture(Gdx.files.internal("tactics/light_blue_frame_bg.png"));

        // 按钮字体
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Alibaba-PuHuiTi-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = "待进围退";
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        buttonFont = generator.generateFont(parameter);

        // 通用信息字体
        parameter.size = 20;
        parameter.characters = "长蛇术机平山排行内连知政神威德统武后大奇智100特殊区域()左右";
        parameter.color = Color.WHITE;
        parameter.borderWidth = 0;
        generalFont = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // 同步更新两个视口
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- 渲染世界 ---
        worldViewport.apply();
        worldStage.act(Gdx.graphics.getDeltaTime());
        worldStage.draw();
        if (DEBUG_GRID) {
            drawDebugGrid();
        }

        // --- 渲染UI (UI会覆盖在世界上方) ---
        uiViewport.apply();
        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    @Override
    public void dispose() {
        // 释放所有资源
        if (worldStage != null) worldStage.dispose();
        if (uiStage != null) uiStage.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (panelBgTexture != null) panelBgTexture.dispose();
        if (buttonNormalTexture != null) buttonNormalTexture.dispose();
        if (buttonCheckedTexture != null) buttonCheckedTexture.dispose();
    }

    private void drawDebugGrid() {
        float w = worldViewport.getWorldWidth();
        float h = worldViewport.getWorldHeight();
        float cellW = w / WORLD_GRID_COLS;
        float cellH = h / WORLD_GRID_ROWS;

        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for (int i = 0; i <= WORLD_GRID_COLS; i++) shapeRenderer.line(i * cellW, 0, i * cellW, h);
        for (int j = 0; j <= WORLD_GRID_ROWS; j++) shapeRenderer.line(0, j * cellH, w, j * cellH);

        shapeRenderer.end();
    }
}
