package com.bhxx.xs_family.values;

public class GlobalValues {

    public static final String IP = "http://xs.seasons-edu.com:8080/xisheng/";
    public static final String IMG_IP = "http://xs.seasons-edu.com:8079/";
    public static final String IP1 = "http://172.16.3.77:8089/xisheng/";//内网
    public static final String PAGE_SIZE = "10";
//    public static final String HIKVISION_SDK_IP = "https://58.216.171.118:443";
    /**
     * 收藏小课堂
     */
    public static final String COLLECT_CLASS = "0";
    /**
     * 收藏动态
     */
    public static final String COLLECT_DYNAMIC = "1";
    /**
     * 收藏相册
     */
    public static final String COLLECT_ALBUM = "2";
    /**
     * 登录
     */
    public static final String LOGIN = IP + "appUser/login.do";
    /**
     * 忘记密码
     */
    public static final String FORGET_PWD = IP + "appUser/forgetPassWord.do";
    /**
     * 发送验证码
     */
    public static final String MOBILE_CODE = IP + "valid/queryByMobileCode.do";
    /**
     * 校验验证码
     */
    public static final String CHECK_CODE = IP + "valid/checkValidCode.do";
    /**
     * 首页精选活动
     */
    public static final String APP_ACT = IP + "appActivity/queryPage.do";
    /**
     * 小课堂 动态  相册收藏或取消
     */
    public static final String SOME_COLLECT_CANClE = IP + "appCollect/saveOrCancel.do";
    /**
     * 活动报名
     */
    public static final String APPLY_ACT = IP + "appActivityOrder/saveOrder.do";
    /**
     * 相册查询
     */
    public static final String CLASS_ALBUM = IP + "appAlbum/queryPage.do";
    /**
     * 相册分享url
     */
    public static final String SHARE_ALBUM = IP + "before/ablum.html?abId=";
    /**
     * 作息表查询
     */
    public static final String REST_SCHEDULE = IP + "appSchedule/queryAll.do";
    /**
     * 小课堂查询
     */
    public static final String QUERY_LITTLE_CLASS = IP + "appParenting/queryPage.do";
    /**
     * 小课堂详情的url
     */
    public static final String LITTLE_CLASS_URL = IP + "before/parenting.html?ptId=";
    /**
     * 通讯录
     */
    public static final String QUERY_ADDRESS_BOOK = IP + "appUser/queryMailList.do";
    /**
     * 班级动态信息查询
     */
    public static final String APP_DYNAMIC = IP + "appDynamic/queryPage.do";
    /**
     * 食谱信息获取
     */
    public static final String APP_RECIPE = IP + "appRecipe/queryByDate.do";
    /**
     * 食谱，相册点赞
     */
    public static final String APP_CLICK = IP + "appClick/saveOrCancel.do";
    /**
     * 食谱点不喜欢
     */
    public static final String APP_UNLIKE = IP + "appUnLike/save.do";
    /**
     * 活动管理
     */
    public static final String ACT_MANAGER = IP + "appActivity/queryByPublisherId.do";
    /**
     * 发布活动
     */
    public static final String PUBLISH_ACTION = IP + "appActivity/save.do";
    /**
     * 老师打卡
     */
    public static final String TEACHER_KQ_CARD = IP + "appCheckWork/save.do";
    /**
     * 老师考勤信息查询
     */
    public static final String TEACHER_MOVEOA_KQ = IP + "appCheckWork/queryDate.do";
    /**
     * 请假申请
     */
    public static final String PUBLISH_QJ = IP + "appLeave/save.do";
    /**
     * 查询请假申请列表
     */
    public static final String MOVEOA_QJ = IP + "appLeave/queryByUserId.do";
    /**
     * 查询物品申领列表
     */
    public static final String MOVEOA_WP = IP + "appTeachingTools/queryByUserId.do";
    /**
     * 发布物品申领
     */
    public static final String PUBLISH_WP = IP + "appTeachingTools/save.do";
    /**
     * 查询费用报销信息列表
     */
    public static final String MOVEOA_FY = IP + "appReimbursement/queryByUserId.do";
    /**
     * 发布费用报销
     */
    public static final String PUBLISH_FY = IP + "appReimbursement/save.do";
    /**
     * 发布班级动态
     */
    public static final String PUBLISH_CLASSDYNAMIC = IP + "appDynamic/save.do";
    /**
     * 发布相册
     */
    public static final String PUBLISH_ALBUM = IP + "appAlbum/save.do";
    /**
     * 相册审核信息查询
     */
    public static final String ALBUMCHECK = IP + "appAlbum/queryAuditing.do";
    /**
     * 相册审核(批准或不批准)
     */
    public static final String ALBUMCHECKAPPROVE = IP + "appAlbum/AuditingAlbum.do";
    /**
     * 活动审核信息查询
     */
    public static final String ACTIONCHECK = IP + "appActivity/queryPageByAuditing.do";
    /**
     * 活动审核(批准或不批准)
     */
    public static final String ACTIONCHECKAPPROVE = IP + "appActivity/AuditingActivity.do";
    /**
     * 动态审核信息查询
     */
    public static final String DYNAMICSCHECK = IP + "appDynamic/queryPageByAuditing.do";
    /**
     * 动态审核(批准或不批准)
     */
    public static final String DYNAMICSCHECKAPPROVE = IP + "appDynamic/auditingDynamic.do";
    /**
     * 查询当前园长对应园所班级信息
     */
    public static final String ALLCLASS = IP + "appClass/queryByPark.do";
    /**
     * 移动办公审核 请假申请
     */
    public static final String MOVEOACHECKQJ = IP + "appLeave/queryAuditing.do";
    /**
     * 移动办公审核 请假申请(批准或不批准)
     */
    public static final String MOVEOACHECKQJAPPROVE = IP + "appLeave/doAuditing.do";
    /**
     * 移动办公审核 物品申领
     */
    public static final String MOVEOACHECKWP = IP + "appReachingTools/queryAuditing.do";
    /**
     * 移动办公审核 物品申领(批准或不批准)
     */
    public static final String MOVEOACHECKWPAPPROVE = IP + "appTeachingTools/audityTeachingTool.do";
    /**
     * 移动办公审核 费用申领
     */
    public static final String MOVEOACHECKFY = IP + "appReimbursement/queryPageByAuditing.do";
    /**
     * 移动办公审核 费用申领(批准或不批准)
     */
    public static final String MOVEOACHECKFYAPPROVE = IP + "appTeachingTools/auditingReimburse.do";

    /**
     * 上传图片
     */
    public static final String UPLOADIMG = IP + "image/save.do";
    /**
     * 用户信息修改
     */
    public static final String APP_USER = IP + "appUser/updateUser.do";
    /**
     * 意见反馈
     */
    public static final String FEED_BACK = IP + "feedBack/saveFeedBack.do";
    /**
     * 头像上传
     */
    public static final String APP_USERPHOTO = IP + "appUser/updateHeadPic.do";
    /**
     * 使用帮助
     */
    public static final String USER_HELP = IP + "appSysUsedHelp/queryPage.do";
    /**
     * 相册举报
     */
    public static final String REPORT_ALBUM = IP + "appReport/save.do";
    /**
     * 相册删除
     */
    public static final String DELETE_ALBUM = IP + "appAlbum/updateIsDelete.do";
    /**
     * 用户信息查询
     */
    public static final String USER_INFO = IP + "appUser/queryById.do";

    /**
     * 家长端 收藏班级 动态信息
     */
    public static final String PARENT_CONNECTION_DYNAMIC = IP + "appCollect/queryDynamic.do";
    /**
     * 家长端 收藏班级 相册信息
     */
    public static final String PARENT_CONNECTION_XIANGCE = IP + "appCollect/queryAlbum.do";
    /**
     * 家长端 收藏班级 亲子信息
     */
    public static final String PARENT_CONNECTION_QINZI = IP + "appParenting/queryPageByCollect.do";
    /**
     * 家长端 我的活动
     */
    public static final String PARENT_MYACT = IP + "appActivity/queryPageByUId.do";
    /**
     * 登录监控sdk
     */
    public static final String HKSDK_LOGIN = IP + "monitor/hklogin.do";
    /**
     * 退出登录
     */
    public static final String LOGINOUT = IP + "appUser/userRetirement.do";
    /**
     * 获取首页轮播资源
     */
    public static final String CAROUSEL = IP + "appActivity/queryFirstActivity.do";
}
