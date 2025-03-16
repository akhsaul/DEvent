package org.akhsaul.dicodingevent.di

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import org.akhsaul.dicodingevent.R
import java.util.Collections

class CoilInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                add(SvgDecoder.Factory())
            }
            .placeholder(
                AppCompatResources.getDrawable(context, R.drawable.placeholder_img_svg)?.asImage()
            )
            .error(AppCompatResources.getDrawable(context, R.drawable.error_img_svg)?.asImage())
            .memoryCache(MemoryCache.Builder().maxSizePercent(context, 0.25).build())
            .build()

        SingletonImageLoader.setSafe { imageLoader }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}