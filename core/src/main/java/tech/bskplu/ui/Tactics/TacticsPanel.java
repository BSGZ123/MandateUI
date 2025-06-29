package tech.bskplu.ui.Tactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @ClassName: TacticsPanel
 * @Description: 小战场
 * @Author BsKPLu
 * @Date 2025/6/28
 * @Version 1.1
 */
public class TacticsPanel extends ApplicationAdapter {

    private static final int GRID_COLS = 16;
    private static final int GRID_ROWS = 11;
    private static final boolean DEBUG_GRID = true;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GRID_COLS, GRID_ROWS, camera);
        // 初始将相机置于世界中心
        camera.position.set(GRID_COLS / 2f, GRID_ROWS / 2f, 0);

        if (DEBUG_GRID) {
            shapeRenderer = new ShapeRenderer();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // 每次resize后保持相机居中
        camera.position.set(viewport.getWorldWidth() / 2f,
            viewport.getWorldHeight() / 2f, 0);
    }

    @Override
    public void render() {
        clearScreen();

        viewport.apply();
        camera.update();

        if (DEBUG_GRID) {
            drawDebugGrid();
        }
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    /**
     * 清除屏幕为黑色
     */
    private static void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * 绘制动态计算的调试网格，铺满整个可视世界
     */
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


