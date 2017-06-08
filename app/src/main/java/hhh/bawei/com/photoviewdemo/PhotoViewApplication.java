package hhh.bawei.com.photoviewdemo;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class PhotoViewApplication extends Application {

	@Override
    public void onCreate() {
		super.onCreate();
		// 在使用imageLoader之前，必须先设置imageLoader
		initImageLoader();
	}

	  /**
     * 初始化图片控件加载器配置
     */
    public void initImageLoader() {
        // 创建默认的ImageLoader配置参数
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

        .threadPoolSize(3)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory().memoryCacheSize(8 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024) // default
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}