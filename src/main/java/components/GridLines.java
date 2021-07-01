package components;


import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.DebugDraw;

import static Util.Settings.*;

public class GridLines extends Component{
    @Override
    public void update(float deltaTime) {
        Vector2f cameraPos = Window.getScene().camera().position;
        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();

        int firstX = ((int)(cameraPos.x/ GRID_WIDTH - 1) * GRID_HEIGHT);
        int firstY = ((int)(cameraPos.y/ GRID_HEIGHT - 1) * GRID_HEIGHT);

        int verticalLines = (int)(projectionSize.x / GRID_WIDTH) + 2;
        int horizontalLines = (int)(projectionSize.y / GRID_HEIGHT) + 2;

        int width = (int)projectionSize.x + GRID_WIDTH * 2;
        int height = (int)projectionSize.y + GRID_HEIGHT * 2;

        int maxLines = Math.max(verticalLines, horizontalLines);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (GRID_WIDTH * i);
            int y = firstY + (GRID_HEIGHT * i);

            if (i < verticalLines){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), new Vector3f(0, 0 ,0));
            }
            if (i < horizontalLines){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), new Vector3f(0, 0 ,0));
            }
        }
    }
}
