package tech.bskplu.ui.Tactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @ClassName: TacticsPanel
 * @Description: 小战场
 * @Author BsKPLu
 * @Date 2025/6/28
 * @Version 1.2
 */
public final class TacticsPanel extends ApplicationAdapter {

    private static final int GRID_COLS = 16;
    private static final int GRID_ROWS = 11;
    private static final int PANEL_HEIGHT = 4;
    private static final boolean DEBUG_GRID = true;
    private static final int CENTER_WIDTH = 3;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Stage stage;

    private Table gridTable;// 整个16x11的网格
    private Stack panelStack;// 底部16x4的操作区域Stack
    private Image panelBg;
    private Table uiTable;

    private Table leftTable;
    private Table centerTable;
    private Table rightTable;

    private Texture panelTexture;

    @Override
    public void create() {
        // 1. 摄像机 + ExtendViewport
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GRID_COLS, GRID_ROWS, camera);
        camera.position.set(GRID_COLS / 2f, GRID_ROWS / 2f, 0);

        if (DEBUG_GRID) shapeRenderer = new ShapeRenderer();

        // 2. Stage
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // 3. 创建16×11的调试网格
        gridTable = new Table();
        gridTable.setFillParent(true);
        //gridTable.debugCell();

        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                gridTable.add().expand().fill();
            }
            gridTable.row();
        }
        stage.addActor(gridTable);

        // 4. 创建底部16×4操作面板堆栈
        panelStack = new Stack();
        stage.addActor(panelStack);

        // 加载背景图
        panelTexture = new Texture(Gdx.files.internal("tactics/bg_panel.png"));
        panelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // 创建背景图
        panelBg = new Image(panelTexture);
        panelBg.setScaling(Scaling.stretch);// 确保背景图拉伸填充

        // 创建UI表格 (作为UI元素的容器)
        uiTable = new Table();
        uiTable.setFillParent(true);// 填充整个Stack


        // 三个子表格
        leftTable   = new Table();
        centerTable = new Table();
        rightTable  = new Table();

        leftTable.debugTable();
        centerTable.debugTable();
        rightTable.debugTable();

        // 把子表格按 6.5 : 3 : 6.5 布局
        float sideWidth = (GRID_COLS - CENTER_WIDTH) / 2f;
        uiTable.add(leftTable)
            .width(sideWidth)
            .expand().fill();
        uiTable.add(centerTable)
            .width(CENTER_WIDTH)
            .expand().fill();
        uiTable.add(rightTable)
            .width(sideWidth)
            .expand().fill();
        uiTable.row();


        // 按顺序添加到Stack (背景在底层)
        panelStack.add(panelBg);
        panelStack.add(uiTable);

        // 5. 初次计算面板尺寸位置
        updatePanelBounds();
    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f,
            viewport.getWorldHeight() / 2f, 0);
        stage.getViewport().update(width, height, true);
        updatePanelBounds();
    }

    @Override
    public void render() {
        clearScreen();
        viewport.apply();
        camera.update();

        if (DEBUG_GRID) drawDebugGrid();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (panelTexture != null) panelTexture.dispose();
        stage.dispose();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawDebugGrid() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float cellW = w / GRID_COLS;
        float cellH = h / GRID_ROWS;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for (int i = 0; i <= GRID_COLS; i++) shapeRenderer.line(i * cellW, 0, i * cellW, h);
        for (int j = 0; j <= GRID_ROWS; j++) shapeRenderer.line(0, j * cellH, w, j * cellH);

        shapeRenderer.end();
    }

    /**
     * 根据当前世界宽度重新调整面板位置和大小
     */
    private void updatePanelBounds() {
        float worldW = viewport.getWorldWidth();
        panelStack.setSize(worldW, PANEL_HEIGHT);// 高度固定4世界单位
        panelStack.setPosition(0, 0);// 左下角对齐
    }
}
