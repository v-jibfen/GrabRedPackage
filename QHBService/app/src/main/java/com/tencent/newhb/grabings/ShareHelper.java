package com.tencent.newhb.grabings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class ShareHelper {
    //分享文字
//    public static void shareText(View view) {
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my Share text.");
//        shareIntent.setType("text/plain");
//
//        //设置分享列表的标题，并且每次都显示分享列表
//        startActivity(Intent.createChooser(shareIntent, "分享到"));
//    }
//
//    //分享单张图片
//    public void shareSingleImage(View view) {
//        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
//        //由文件得到uri
//        Uri imageUri = Uri.fromFile(new File(imagePath));
//        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//        shareIntent.setType("image/*");
//        startActivity(Intent.createChooser(shareIntent, "分享到"));
//    }
//
//    //分享多张图片
//    public void shareMultipleImage(View view) {
//        ArrayList<uri> uriList = new ArrayList<>();
//
//        String path = Environment.getExternalStorageDirectory() + File.separator;
//        uriList.add(Uri.fromFile(new File(path+"australia_1.jpg")));
//        uriList.add(Uri.fromFile(new File(path+"australia_2.jpg")));
//        uriList.add(Uri.fromFile(new File(path+"australia_3.jpg")));
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
//        shareIntent.setType("image/*");
//        startActivity(Intent.createChooser(shareIntent, "分享到"));
//    }

    /**
     * 分享功能
     *
     * @param context
     *		  上下文
     * @param activityTitle
     *		  Activity的名字
     * @param msgTitle
     *		  消息标题
     * @param msgText
     *		  消息内容
     * @param imageSrc
     *		  图片，不分享图片则传0
     */
    public static void shareMsg(final Activity context, final String activityTitle, final String msgTitle,
                                final String msgText, int imageSrc) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        if (imageSrc != 0) {

            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .asBitmap()
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>(96, 96) {
                        @Override
                        public void onResourceReady(Bitmap data, GlideAnimation anim) {
                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), data, null, null));

                            intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
                            intent.putExtra(Intent.EXTRA_TEXT, msgText);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setType("image/*");
                            context.startActivity(Intent.createChooser(intent, activityTitle));
                        }
                    });
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }
}
