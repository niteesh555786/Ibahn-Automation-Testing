/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package bizbrolly.svarochiapp.model.places;

/**
 *
 */
public class PlaceItemSection implements PlaceItem {

    private final String title;

    public PlaceItemSection(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
