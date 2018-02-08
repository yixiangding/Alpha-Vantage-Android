package com.example.yixiangding.hw9;

import java.util.Comparator;

/**
 * Comparators used for list
 */

public class ListComparators {
    public Comparator<FavData> getDefaultAscending() {
        return new DefaultAscending();
    }

    public Comparator<FavData> getDefaultDescending() {
        return new DefaultDescending();
    }

    public Comparator<FavData> getSymbolAscending() {
        return new SymbolAscending();
    }

    public Comparator<FavData> getSymbolDescending() {
        return new SymbolDescending();
    }

    public Comparator<FavData> getPriceAscending() {
        return new PriceAscending();
    }

    public Comparator<FavData> getPriceDescending() {
        return new PriceDescending();
    }

    public Comparator<FavData> getChangeAscending() {
        return new ChangeAscending();
    }

    public Comparator<FavData> getChangeDescending() {
        return new ChangeDescending();
    }

    public Comparator<FavData> getPercentAscending() {
        return new PercentAscending();
    }

    public Comparator<FavData> getPercenDescending() {
        return new PercentDescending();
    }

    public class DefaultAscending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return o1.getAddedTime() == o2.getAddedTime() ? 0 : o1.getAddedTime() > o2.getAddedTime() ? 1 : -1;
        }
    }

    public class DefaultDescending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return o1.getAddedTime() == o2.getAddedTime() ? 0 : o1.getAddedTime() > o2.getAddedTime() ? -1 : 1;
        }
    }

    public class SymbolAscending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return o1.getSymbol().compareTo(o2.getSymbol());
        }
    }

    public class SymbolDescending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return o2.getSymbol().compareTo(o1.getSymbol());
        }
    }

    public class PriceAscending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getPrice()) - Double.valueOf(o2.getPrice()) > 0 ? 1 : -1;
        }
    }

    public class PriceDescending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getPrice()) - Double.valueOf(o2.getPrice()) > 0 ? -1 : 1;
        }
    }

    public class ChangeAscending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getChange()) - Double.valueOf(o2.getChange()) > 0 ? 1 : -1;
        }
    }

    public class ChangeDescending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getChange()) - Double.valueOf(o2.getChange()) > 0 ? -1 : 1;
        }
    }

    public class PercentAscending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getChangePercentage()) - Double.valueOf(o2.getChangePercentage()) > 0 ? 1 : -1;
        }
    }

    public class PercentDescending implements Comparator<FavData> {
        @Override
        public int compare(FavData o1, FavData o2) {
            return Double.valueOf(o1.getChangePercentage()) - Double.valueOf(o2.getChangePercentage()) > 0 ? -1 : 1;
        }
    }
}
