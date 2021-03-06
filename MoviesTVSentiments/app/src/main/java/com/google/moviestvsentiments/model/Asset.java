package com.google.moviestvsentiments.model;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.time.Instant;

/**
 * A record in the assets database table.
 */
@AutoValue
@Entity(tableName = "assets_table", primaryKeys = {"asset_id", "asset_type"})
public abstract class Asset implements Parcelable {

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "asset_id")
    public abstract String id();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "asset_type")
    public abstract AssetType type();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "title")
    public abstract String title();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "poster")
    public abstract String poster();

    @AutoValue.CopyAnnotations
    @Nullable
    @ColumnInfo(name = "banner")
    public abstract String banner();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "plot")
    public abstract String plot();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "runtime")
    public abstract String runtime();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "year")
    public abstract String year();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    public abstract Instant timestamp();

    @AutoValue.CopyAnnotations
    @Nullable
    @ColumnInfo(name = "imdb_rating")
    public abstract String imdbRating();

    @AutoValue.CopyAnnotations
    @Nullable
    @ColumnInfo(name = "rt_rating")
    public abstract String rottenTomatoesRating();

    /**
     * Creates a new Asset object with the given fields. Room and Jackson need this factory method
     * to create Asset objects. Other code should use the builder() method instead of this method.
     * @return A new Asset object with the specified fields.
     */
    @JsonCreator
    public static Asset create(@JsonProperty("assetId") String id,
                               @JsonProperty("assetType") AssetType type,
                               @JsonProperty("title") String title,
                               @JsonProperty("poster") String poster,
                               @JsonProperty("banner") String banner,
                               @JsonProperty("plot") String plot,
                               @JsonProperty("runtime") String runtime,
                               @JsonProperty("year") String year,
                               @JsonProperty("timestamp") Instant timestamp,
                               @JsonProperty("imdbRating") String imdbRating,
                               @JsonProperty("rottenTomatoesRating") String rottenTomatoesRating) {
        return Asset.builder().setId(id).setType(type).setTitle(title).setPoster(poster)
                .setBanner(banner).setPlot(plot).setRuntime(runtime).setYear(year)
                .setTimestamp(timestamp).setImdbRating(imdbRating)
                .setRottenTomatoesRating(rottenTomatoesRating).build();
    }

    /**
     * Returns an Asset builder.
     */
    public static Builder builder() {
        return new AutoValue_Asset.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(String value);
        public abstract Builder setType(AssetType value);
        public abstract Builder setTitle(String value);
        public abstract Builder setPoster(String value);
        public abstract Builder setBanner(String value);
        public abstract Builder setPlot(String value);
        public abstract Builder setRuntime(String value);
        public abstract Builder setYear(String value);
        public abstract Builder setTimestamp(Instant value);
        public abstract Builder setImdbRating(String value);
        public abstract Builder setRottenTomatoesRating(String value);
        public abstract Asset build();
    }
}
