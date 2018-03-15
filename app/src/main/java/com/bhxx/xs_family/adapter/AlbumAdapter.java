package com.bhxx.xs_family.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ImageDisplayActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.AlbumModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class AlbumAdapter extends CommonAdapter<AlbumModel> {

    public AlbumAdapter(List<AlbumModel> dataList, Context context) {
        super(dataList, context, new AlbumItemSupport());
    }

    @Override
    public void convert(final ViewHolder holder, final AlbumModel data) {
        if (data.getAbPublisher() != null) {
            UserModel user = data.getAbPublisher();
            if (!TextUtils.isEmpty(user.getuHeadPic())) {
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + user.getuHeadPic(), (CircleImageView) holder.getView(R.id.album_user_pic), LoadImage.getHeadImgOptions());
            }
            if (!TextUtils.isEmpty(user.getuName())) {
                holder.setText(R.id.album_user_name, user.getuName());
            }
        }
        if (!TextUtils.isEmpty(data.getAbCraeteTime())) {
            holder.setText(R.id.album_create_time, data.getAbCraeteTime());
        }
        if (!TextUtils.isEmpty(data.getAbDesction())) {
            holder.setText(R.id.album_content, data.getAbDesction());
        }
        if (!TextUtils.isEmpty(data.getAbIsCollect())) {
            if (data.getAbIsCollect().equals("0")) {
                holder.setImageResource(R.id.album_collect_icon, R.mipmap.album_collect);
            } else if (data.getAbIsCollect().equals("1")) {
                holder.setImageResource(R.id.album_collect_icon, R.mipmap.album_collect_pre);
            }
        }
        if (data.getAbIsClick() == 0) {
            holder.setImageResource(R.id.album_like_icon, R.mipmap.album_like);
        } else if (data.getAbIsClick() == 1) {
            holder.setImageResource(R.id.album_like_icon, R.mipmap.album_like_pre);
        }
        holder.setText(R.id.album_like_num, data.getAbClickCount() + "");
        holder.setText(R.id.album_collect_num, data.getAbCollectCount() + "");
        holder.setOnClickListener(R.id.album_more_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbumDialog(data);
            }
        });
        holder.setOnClickListener(R.id.album_collect_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.app
                        .getData("uRole").equals("2")) {
                    collectAlbum(data, (ImageView) (holder.getView(R.id.album_collect_icon)));
                } else {
                    showToast("无权限");
                }
            }
        });
        holder.setOnClickListener(R.id.album_like_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.app
                        .getData("uRole").equals("2")) {
                    likeAlbum(data, (ImageView) (holder.getView(R.id.album_like_icon)));
                } else {
                    showToast("无权限");
                }
            }
        });
        switch (holder.getLayoutId()) {
            case R.layout.album_no_pic_item:
                break;
            case R.layout.album_single_pic_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    final String[] single = data.getAbPics().split(";");
                    if (!TextUtils.isEmpty(single[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + single[0], (ImageView) holder.getView(R.id.album_single_img), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_single_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> singUrl = new ArrayList<String>();
                                singUrl.add(single[0]);
                                ImageDisplayActivity.start(context, singUrl, 0);
                            }
                        });
                    }
                }
                break;
            case R.layout.album_double_pic_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    final String[] doublePic = data.getAbPics().split(";");
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(doublePic[0]);
                    doubleUrl.add(doublePic[1]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[0], (ImageView) holder.getView(R.id.album_double_img_1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_double_img_1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[1], (ImageView) holder.getView(R.id.album_double_img_2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_double_img_2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 1);
                            }
                        });
                    }
                }
                break;
            case R.layout.album_list_pics_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    String[] pics = data.getAbPics().split(";");
                    final ArrayList<String> listPic = new ArrayList<String>();
                    for (int i = 0; i < pics.length; i++) {
                        listPic.add(pics[i]);
                    }
                    holder.setAdapter(R.id.album_pic_gv, new CommonAdapter<String>(listPic, context, R.layout.album_pics_item) {
                        @Override
                        public void convert(ViewHolder holders, String img) {
                            if (!TextUtils.isEmpty(img)) {
                                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + img, (ImageView) holders.getView(R.id.album_pics_img), LoadImage.getDefaultOptions());
                            }
                        }
                    });

                    holder.setOnItemClickListener(R.id.album_pic_gv, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageDisplayActivity.start(context, listPic, position);
                        }
                    });
                }
                break;
        }
    }

    /**
     * 更多操作弹窗
     *
     * @param album 对象
     */
    private void showAlbumDialog(final AlbumModel album) {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.album_caution_dialog, null);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        Button album_report = (Button) view.findViewById(R.id.album_report);
        Button album_delete = (Button) view.findViewById(R.id.album_delete);
        Button album_cancle = (Button) view.findViewById(R.id.album_cancle);
        ImageView album_share_qq = (ImageView) view.findViewById(R.id.album_share_qq);
        ImageView album_share_wx = (ImageView) view.findViewById(R.id.album_share_wx);
        if (album.getAbPublisherId() == Integer.parseInt(App.app.getData("uId"))) {
            if (album.getAbDelete() != 1) {
                album_delete.setVisibility(View.VISIBLE);
            }
            album_report.setVisibility(View.GONE);
        }
        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();

        album_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showReportDialog(album);
            }
        });
        album_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDeleteDialog(album);
            }
        });
        album_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        album_share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new ShareAction((Activity) context)
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(umShareListener)
                        .withText(album.getAbDesction())
                        .withTargetUrl(GlobalValues.SHARE_ALBUM + album.getAbId())
                        .withMedia(new UMImage(context, GlobalValues.IMG_IP + album.getAbPics().split(";")[0]))
                        .share();
            }
        });

        album_share_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                LogUtils.i("wx url =" + GlobalValues.IMG_IP + album.getAbPics().split(";")[0]);
                new ShareAction((Activity) context)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .withText(album.getAbDesction())
                        .withTargetUrl(GlobalValues.SHARE_ALBUM + album.getAbId())
                        .withMedia(new UMImage(context, GlobalValues.IMG_IP + album.getAbPics().split(";")[0]))
                        .share();
            }
        });
    }


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                showToast(platform + " 收藏成功啦");
            } else {
                showToast(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast(platform + " 分享失败啦");
            if (t != null) {
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast(platform + " 分享取消了");
        }
    };

    /**
     * 举报弹窗
     */
    private void showReportDialog(final AlbumModel album) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_report_dialog, null);
        final Dialog log = new Dialog(context, R.style.transparentFrameWindowStyle);
        Button report_cancle = (Button) view.findViewById(R.id.report_cancle);
        Button report_confirm = (Button) view.findViewById(R.id.report_confirm);
        final EditText report_edit = (EditText) view.findViewById(R.id.report_edit);
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.show();
        int measureWidth = context.getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        report_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
        report_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                log.dismiss();
                if (TextUtils.isEmpty(report_edit.getText().toString())) {
                    showToast("请输入举报内容");
                    return;
                }
                reportAlbum(album, report_edit.getText().toString());
            }
        });
    }

    /**
     * 展示删除相册弹窗
     */
    private void showDeleteDialog(final AlbumModel album) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_delete_dialog, null);
        final Dialog log = new Dialog(context, R.style.transparentFrameWindowStyle);
        Button album_delete_cancle = (Button) view.findViewById(R.id.album_delete_cancle);
        Button album_delete_confirm = (Button) view.findViewById(R.id.album_delete_confirm);
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.show();
        int measureWidth = context.getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        album_delete_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
        album_delete_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                log.dismiss();
                deleteAlbum(album);
            }
        });
    }

    /**
     * 举报相册
     *
     * @param album
     * @param report
     */
    private void reportAlbum(final AlbumModel album, String report) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("rtAlbumId", album.getAbId() + "");
        params.put("rtUserId", App.app.getData("uId"));
        params.put("rtDesction", report);
        String token = TokenUtils.getInstance().configParams(GlobalValues.REPORT_ALBUM + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.REPORT_ALBUM, "REPORT", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        showToast("举报成功,我们会尽快受理");
                    } else {
                        showToast(bean.getMessage());
                    }
                } else {
                    showToast("操作失败");
                }
            }
        });
    }

    /**
     * 相册删除
     *
     * @param album
     */
    private void deleteAlbum(final AlbumModel album) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("abId", album.getAbId() + "");
        params.put("uId", App.app.getData("uId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.DELETE_ALBUM + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.DELETE_ALBUM, "DELETE", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("删除失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        showToast("删除成功,等待审核");
                        changeDelete(album.getAbId());
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void changeDelete(int abId) {
        for (AlbumModel albumModel : getDataList()) {
            if (albumModel.getAbId() == abId) {
                albumModel.setAbDelete(1);
            }
        }
    }

    /**
     * 收藏相册
     *
     * @param album
     * @param collectView
     */
    private void collectAlbum(final AlbumModel album, final ImageView collectView) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("clHomeLeaderId", App.app.getData("uId"));
        params.put("clCollectedId", album.getAbId() + "");
        params.put("clCollectedType", GlobalValues.COLLECT_ALBUM);
        String token = TokenUtils.getInstance().configParams(GlobalValues.SOME_COLLECT_CANClE + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.SOME_COLLECT_CANClE, "COLLECT", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        if (!TextUtils.isEmpty(album.getAbIsCollect())) {
                            if (album.getAbIsCollect().equals("0")) {
                                doCollect(album.getAbId(), "1");
                                collectView.setImageResource(R.mipmap.album_collect_pre);
                                changeCollectNum(album.getAbId(), 1);
                                notifyDataSetChanged();
                            } else if (album.getAbIsCollect().equals("1")) {
                                doCollect(album.getAbId(), "0");
                                collectView.setImageResource(R.mipmap.album_collect);
                                changeCollectNum(album.getAbId(), 0);
                                notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 收藏
     *
     * @param abId   相册id
     * @param result 结果
     */
    private void doCollect(int abId, String result) {
        for (AlbumModel albumModel : getDataList()) {
            if (albumModel.getAbId() == abId) {
                albumModel.setAbIsCollect(result);
            }
        }
    }

    /**
     * 改变收藏数
     *
     * @param abId 相册id
     * @param done 0减1  1加1
     */
    private void changeCollectNum(int abId, int done) {
        for (AlbumModel albumModel : getDataList()) {
            if (albumModel.getAbId() == abId) {
                if (done == 0) {
                    albumModel.setAbCollectCount(albumModel.getAbCollectCount() - 1);
                } else {
                    albumModel.setAbCollectCount(albumModel.getAbCollectCount() + 1);
                }
            }
        }
    }

    /**
     * 喜欢相册操作
     *
     * @param album
     * @param likeView
     */
    private void likeAlbum(final AlbumModel album, final ImageView likeView) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("clUserId", App.app.getData("uId"));
        params.put("clClickedId", album.getAbId() + "");
        params.put("clType", "0");
        String token = TokenUtils.getInstance().configParams(GlobalValues.APP_CLICK + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.APP_CLICK, "LIKE", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        if (album.getAbIsClick() == 0) {
                            doLike(album.getAbId(), 1);
                            likeView.setImageResource(R.mipmap.album_like_pre);
                            changeLikeNum(album.getAbId(), 1);
                            notifyDataSetChanged();
                        } else if (album.getAbIsClick() == 1) {
                            doLike(album.getAbId(), 0);
                            likeView.setImageResource(R.mipmap.album_like);
                            changeLikeNum(album.getAbId(), 0);
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    /**
     * 是否点赞
     *
     * @param abId
     * @param isLike
     */
    private void doLike(int abId, int isLike) {
        for (AlbumModel albumModel : getDataList()) {
            if (albumModel.getAbId() == abId) {
                albumModel.setAbIsClick(isLike);
            }
        }
    }

    /**
     * 改变点赞数
     *
     * @param abId 相册id
     * @param done 0减1  1加1
     */
    private void changeLikeNum(int abId, int done) {
        for (AlbumModel albumModel : getDataList()) {
            if (albumModel.getAbId() == abId) {
                if (done == 0) {
                    albumModel.setAbClickCount(albumModel.getAbClickCount() - 1);
                } else {
                    albumModel.setAbClickCount(albumModel.getAbClickCount() + 1);
                }
            }
        }
    }
}
