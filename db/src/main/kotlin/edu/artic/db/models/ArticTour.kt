package edu.artic.db.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import edu.artic.db.*
import edu.artic.localization.SpecifiesLanguage
import edu.artic.ui.util.asCDNUri
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Entity
@Parcelize
data class ArticTour(
        @Json(name = "title") val title: String,
        @Json(name = "status") val status: String?,
        @Json(name = "nid") @PrimaryKey val nid: String,
        @Json(name = "type") val type: String?,
        @Json(name = "translations") val translations: List<Translation>,
        @Json(name = "location") val location: String?,
        @Json(name = "latitude") val latitude: String?,
        @Json(name = "longitude") val longitude: String?,
        /**
         * [FloorAdapter] parses non null string values since Moshi throws the null exception before it is passed to [FloorAdapter].
         * Therefore had to make this field optional.
         * There is an open issue in github for this bug.
         * https://github.com/square/moshi/issues/586
         */
        @Floor @Json(name = "floor") var floor: Int?,
        @Json(name = "image_filename") val imageFileName: String?,
        @Json(name = "image_url") val imageUrl: String?,
        @Json(name = "image_filemime") val imageFileMime: String?,
        @Json(name = "image_filesize") val imageFileSize: String?,
        @Json(name = "image_width") val imageWidth: String?,
        @Json(name = "image_height") val imageHeight: String?,
        @Json(name = "thumbnail_full_path") val thumbnailFullPath: String?,
        @Json(name = "large_image_crop_v2") @Embedded(prefix = "large_image_crop") val largeImageCropV2: CropRect?,
        @Json(name = "large_image_full_path") val largeImageFullPath: String?,
        @Json(name = "tour_banner") val tourBanner: String?,
        @Json(name = "selector_number") val selectorNumber: String?,
        @Json(name = "description") val description: String?,
        @Json(name = "description_html") val descriptionHtml: String?,
        @Json(name = "intro") val intro: String?,
        @Json(name = "intro_html") val introHtml: String?,
        @Json(name = "tour_duration") val tourDuration: String?,
        @Json(name = "tour_audio") val tourAudio: String?,
        @Json(name = "category") @Embedded(prefix = "tour_cat") val category: TourCategory?,
        @Json(name = "weight") val weight: Int,
        @Json(name = "tour_stops") val tourStops: MutableList<TourStop>

) : Parcelable, Playable, AccessibilityAware {

    data class TourDate(
            @Json(name = "start_date") val startDate: String?,
            @Json(name = "end_date") val endDate: String?
    )

    @JsonClass(generateAdapter = true)
    @Parcelize
    data class TourStop(
            @Json(name = "object") val objectId: String?,
            @Json(name = "audio_id") val audioId: String?,
            @Json(name = "audio_bumper") val audioBumper: String?,
            @Json(name = "sort") val order: Int
    ) : Parcelable

    @JsonClass(generateAdapter = true)
    @Parcelize
    data class TourCategory(
            val id: String?,
            val title: String?
    ) : Parcelable

    @JsonClass(generateAdapter = true)
    @Parcelize
    data class Translation(
            @Json(name = "language") val language: String?,
            @Json(name = "title") val title: String?,
            @Json(name = "description") val description: String?,
            @Json(name = "description_html") val description_html: String?,
            @Json(name = "intro") val intro: String?,
            @Json(name = "intro_html") val intro_html: String?,
            @Json(name = "tour_duration") val tour_duration: String?
    ) : Parcelable, SpecifiesLanguage {
        override fun underlyingLanguage(): String? {
            return language
        }
    }

    /**
     * Alias for [imageUrl], adjusted to the [CDN endpoint][String.asCDNUri] if appropriate.
     */
    val standardImageUrl: String?
        get() {
            return imageUrl?.asCDNUri()
        }

    /**
     * Alias for [largeImageFullPath], adjusted to the [CDN endpoint][String.asCDNUri] if appropriate.
     */
    val largeImageUrl: String?
        get() {
            return largeImageFullPath?.asCDNUri()
        }

    /**
     * Alias for [thumbnailFullPath], adjusted to the [CDN endpoint][String.asCDNUri] if appropriate.
     */
    val thumbUrl: String?
        get() {
            return thumbnailFullPath?.asCDNUri()
        }

    /**
     * Returns [floor], parsed to an integer. We default to [Int.MIN_VALUE] as 0 is a valid floor.
     */
    val floorAsInt: Int
        get() = floor ?: INVALID_FLOOR


    override fun getPlayableThumbnailUrl(): String? {
        return largeImageUrl
    }

    override fun getPlayableTitle(): String? {
        return this.title
    }

    override fun getContentDescription(): String {
        return this.title
    }

    /**
     * Returns the translations including english language
     */
    val allTranslations: List<Translation>
        get() {
            return mutableListOf(this.toEnglishTranslation()).apply { addAll(translations) }
        }

}

/**
 * Builds english translation for [ArticTour].
 * [ArticTour]'s properties are in english, so copying the required properties suffice the english translation.
 */
fun ArticTour.toEnglishTranslation() : ArticTour.Translation {
    return ArticTour.Translation(
            language = "en-US",
            title = this.title,
            description = this.description,
            description_html = this.descriptionHtml,
            intro = this.intro,
            intro_html = this.introHtml,
            tour_duration = this.tourDuration
    )
}

fun ArticTour.getIntroStop(): ArticTour.TourStop {
    return ArticTour.TourStop(INTRO_TOUR_STOP_OBJECT_ID, this.tourAudio, null, -1)
}

fun ArticTour.TourStop.isIntroStop(): Boolean {
    return this.objectId === INTRO_TOUR_STOP_OBJECT_ID
}