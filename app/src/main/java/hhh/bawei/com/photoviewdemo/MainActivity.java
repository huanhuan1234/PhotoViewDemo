package hhh.bawei.com.photoviewdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

public class MainActivity extends Activity {
    private ViewPager viewPager;// 声明ViewPager
    // 创建数据源，这里采用本地数据源
  /*  private int[] photoId = { R.drawable.icon_001, R.drawable.icon_002,
            R.drawable.icon_003 };*/



/*
    // 创建网络数据源
    private String[] urls = {
            "http://file.otcgd.com/travel/80/line/da/20110314/2011314_1280358529.jpg",
            "http://file25.mafengwo.net/M00/C1/0A/wKgB4lImkp2AbhsjAA3FZePQ0-o73.jpeg",
            "http://www.373sqs.com/upfile/images/2009-12/9/200912910821692.jpg",
            "http://img101.mypsd.com.cn/20120526/1/Mypsd_176980_201205260857370023B.jpg",
            "http://photo.66diqiu.com/uploads/756/ue/image/20141220/1419058611477838.jpg",
            "http://pic12.nipic.com/20110221/2707401_092004783000_2.jpg",
            "http://pic.nipic.com/2008-05-20/2008520112050960_2.jpg",
            "http://pic16.nipic.com/20110913/8361282_172836540179_2.jpg", };*/
    private List<PhotoView> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setVies();// 初始化控件

        list = new ArrayList<>();

        PhotoView photoView1 = new PhotoView(this);
        photoView1.setImageResource(R.drawable.icon_001);
        photoView1.setScaleType(ImageView.ScaleType.FIT_XY);

        PhotoView photoView2 = new PhotoView(this);
        photoView2.setImageResource(R.drawable.icon_002);
        photoView2.setScaleType(ImageView.ScaleType.FIT_XY);

        PhotoView photoView3 = new PhotoView(this);
        photoView3.setImageResource(R.drawable.icon_003);
        photoView3.setScaleType(ImageView.ScaleType.FIT_XY);

        list.add(photoView1);
        list.add(photoView2);
        list.add(photoView3);

        // 给viewPager设置adapter，将每个图片设置到每个页面当中
        viewPager.setAdapter(new MyAdapter());
        // 给viewPager设置监听
       /* viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // viewPager得到页面的数量
                int childCount = viewPager.getChildCount();

                // 遍历当前所有加载过的PhotoView，恢复所有图片的默认状态

                for (int i = 0; i < childCount; i++) {
                    View childAt = viewPager.getChildAt(i);

                    try {
                        if (childAt != null && childAt instanceof PhotoView) {
                            PhotoView photoView = (PhotoView) childAt;// 得到viewPager里面的页面
                            PhotoViewAttacher mAttacher = new PhotoViewAttacher(
                                    photoView);// 把得到的photoView放到这个负责变形的类当中
                            // mAttacher.getDisplayMatrix().reset();//得到这个页面的显示状态，然后重置为默认状态
                            mAttacher.setScaleType(ImageView.ScaleType.FIT_XY);// 设置充满全屏
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });*/
    }

    /**
     * 初始化控件
     */
    private void setVies() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    /**
     * 自定义pagerAdapter
     */
    public class MyAdapter extends PagerAdapter {


        // 得到要显示的图片数量
        @Override
        public int getCount() {

           // return urls.length;//得到网络图片url的数量
            return list.size();//得到本地资源图片id的数量
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
           // 然后将加载了图片的photoView添加到viewpager中，并且设置宽高
            container.addView(list.get(position));
            PhotoView photoView = list.get(position);

            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view1 = View.inflate(MainActivity.this, R.layout.altdiog, null);
                    builder.setView(view1);
                    builder.setTitle("保存到相册?");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveImageView(getViewBitmap(view));

                        }
                    });
                    builder.show();
                    return true;
                }

            });

            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class SaveObservable implements Observable.OnSubscribe<String> {

        private Bitmap drawingCache = null;

        public SaveObservable(Bitmap drawingCache) {
            this.drawingCache = drawingCache;
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            if (drawingCache == null) {
                subscriber.onError(new NullPointerException("imageview的bitmap获取为null,请确认imageview显示图片了"));
            } else {
                try {
                    File imageFile = new File(Environment.getExternalStorageDirectory(), "saveImageview.jpg");
                    FileOutputStream outStream;
                    outStream = new FileOutputStream(imageFile);
                    drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    subscriber.onNext(Environment.getExternalStorageDirectory().getPath());
                    subscriber.onCompleted();
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }
    }

    private class SaveSubscriber extends Subscriber<String> {

        @Override
        public void onCompleted() {
            Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            Log.i(getClass().getSimpleName(), e.toString());
            Toast.makeText(getApplicationContext(), "保存失败——> " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(String s) {
            Toast.makeText(getApplicationContext(), "保存路径为：-->  " + s, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImageView(Bitmap drawingCache) {
        Observable.create(new SaveObservable(drawingCache))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SaveSubscriber());
    }

    /**
     * 某些机型直接获取会为null,在这里处理一下防止国内某些机型返回null
     */
    private Bitmap getViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}


