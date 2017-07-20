package bizbrolly.svarochiapp;

import android.support.test.espresso.action.CoordinatesProvider;
import android.view.View;

/**
 * Created by Jaadugar on 6/30/2017.
 */

public class CustomisableCoordinatesProvider implements CoordinatesProvider {
    private int x;
    private int y;

    public CustomisableCoordinatesProvider(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float[] calculateCoordinates(View view) {
        return new float[]{x,y};
    }
}
