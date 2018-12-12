package info.android.technologies.indoreconnect.util;

/**
 * Created by kamlesh on 11/10/2017.
 */
public class WebAPI {

    // Message Gatway API
    public static final String URL_MESSAGE_GATEWAY = "http://bulksms.viasgroups.com/api/sendhttp.php?authkey=192872Aqsk2Qzw5a58a289&";
    // GET

    public static final String BASE_URL = "http://indoreconnect.in/";

    public static final String URL_CATEGORY = BASE_URL + "admin/Api/category-api.php";

    public static final String URL_BUSINESS_LIST = BASE_URL + "admin/Api/business-list-api.php?id=";

    public static final String URL_BUSNESS_DETAILS = BASE_URL + "admin/Api/bussiness-detail-api.php?id=";

    public static final String URL_OFFER = BASE_URL + "admin/Api/category_offer_detail-api.php";

    public static final String URL_BANNER = BASE_URL + "admin/Api/banner-api.php";

    // info category
    public static final String URL_INFO_CATEGORY = BASE_URL + "admin/Api/category_info-api.php";

    public static final String URL_INFO_SUB_CATEORY = BASE_URL + "admin/Api/category-info-sub-api.php?id=";
    //    UW4E0
    public static final String URL_INFO_SUB_SUB_CATEGORY = BASE_URL + "admin/Api/category-info-list-api.php?id=";

    public static final String URL_NOTIFICATION = BASE_URL + "admin/Api/notification.php?dt=";
    // POST

    public static final String URL_REGISTER = BASE_URL + "admin/Api/register-api.php";
    //      name , email , phone_no , password

    public static final String URL_LOGIN = BASE_URL + "admin/Api/login-api.php";
    //      email , password

    public static final String URL_FORGOT = BASE_URL + "admin/Api/project url/Api/forgot-password-api.php";
    //      email

    public static final String URL_FEEDBACK = BASE_URL + "admin/Api/feedback-api.php";
    //      about , msg , rate , email

    public static final String URL_LIST_YOURSELF = BASE_URL + "admin/Api/contact-api.php";
    //      name , email , mob , cate

    public static final String URL_RATING = BASE_URL + "admin/Api/addrating-api.php";

    public static final String URL_SEARCH = BASE_URL + "admin/Api/search-api.php";

    public static final String URL_INFO_BUSINESS_DETAILS = BASE_URL + "admin/Api/category-info-detail-api.php?id=";

    public static final String URL_SEARCH_KEYWORD = BASE_URL + "admin/Api/search-api-key.php?keyword=";

}
