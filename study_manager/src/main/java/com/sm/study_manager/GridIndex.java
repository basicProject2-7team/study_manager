package com.sm.study_manager;

import java.util.Objects;

public class GridIndex {
    private int row;
    private int column;

    public GridIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // getter와 setter 구현
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    // equals와 hashCode 메서드 오버라이드
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GridIndex gridIndex = (GridIndex) obj;
        return row == gridIndex.row && column == gridIndex.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
