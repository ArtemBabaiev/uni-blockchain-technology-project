package com.ababaiev.views.profile;

import com.ababaiev.views.profile.models.ReadingGridModel;
import com.vaadin.flow.component.grid.Grid;

import java.time.format.DateTimeFormatter;

public class MeterReadingGrid extends Grid<ReadingGridModel> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public MeterReadingGrid() {
        super(ReadingGridModel.class, false);
        addColumn(ReadingGridModel::getReadingValue).setHeader("Reading Value");
        addColumn(i -> formatter.format(i.getTimestamp())).setHeader("Timestamp");
        setAllRowsVisible(true);
        setEmptyStateText("No Meter Readings found.");
    }
}
