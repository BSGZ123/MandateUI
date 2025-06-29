package tech.bskplu.ui.Tactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @ClassName: TacticsPanel
 * @Description: 小战场
 * @Author BsKPLu
 * @Date 2025/6/28
 * @Version 1.1
 */
public final class TacticsPanel extends ApplicationAdapter {

    private static final int GRID_COLS = 16;
    private static final int GRID_ROWS = 11;
    private static final boolean DEBUG_GRID = false;
    private static final boolean DEBUG_TABLE = true;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Table table;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GRID_COLS, GRID_ROWS, camera);
        camera.position.set(GRID_COLS / 2f, GRID_ROWS / 2f, 0);

        if (DEBUG_GRID) {
            shapeRenderer = new ShapeRenderer();
        }

        // 初始化Stage与Table，生成16x11单元格
        stage = new Stage(viewport);
        table = new Table();
        table.setFillParent(true);
        //table.setDebug(DEBUG_TABLE);
        table.debugCell();
        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                table.add().expand().fill();
            }
            table.row();
        }
        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f,
            viewport.getWorldHeight() / 2f, 0);
        // 同步更新Stage视口
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        clearScreen();

        viewport.apply();
        camera.update();

        if (DEBUG_GRID) {
            drawDebugGrid();
        }

        // 更新并渲染UI表格
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        stage.dispose();
    }

    private static void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawDebugGrid() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float cellWidth = worldWidth / GRID_COLS;
        float cellHeight = worldHeight / GRID_ROWS;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        // 垂直线
        for (int i = 0; i <= GRID_COLS; i++) {
            float x = i * cellWidth;
            shapeRenderer.line(x, 0, x, worldHeight);
        }
        // 水平线
        for (int j = 0; j <= GRID_ROWS; j++) {
            float y = j * cellHeight;
            shapeRenderer.line(0, y, worldWidth, y);
        }
        shapeRenderer.end();
    }
}



