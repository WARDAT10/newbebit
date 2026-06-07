package webit.Poject.dto;

import webit.Poject.model.Location;


public record LocationResponse(String id, String name, int sortOrder, String imageUrl) {

    public static LocationResponse from(Location l) {
        return new LocationResponse(l.getId(), l.getName(), l.getSortOrder(), l.getImageUrl());
    }
}
