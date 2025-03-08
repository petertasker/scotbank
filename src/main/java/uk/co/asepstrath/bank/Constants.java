package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public final class Constants {

    private Constants() {

    }
    /**
     * Endpoints
     * NOTE: If you refactor these make sure that they are reflected in the handlebars templates,
     * the variables there don't use these constants.
     **/

    public static final String TEMPLATE_LOGIN = "login_user.hbs";
    public static final String TEMPLATE_ACCOUNT = "account.hbs";
    public static final String TEMPLATE_DEPOSIT = "deposit.hbs";
    public static final String TEMPLATE_WITHDRAW = "withdraw.hbs";
    public static final String TEMPLATE_MANAGER_LOGIN = "login_manager.hbs";
    public static final String TEMPLATE_MANAGER_DASHBOARD = "manager_dashboard.hbs";

    public static final String ROUTE_LOGIN = "/login";
    public static final String ROUTE_ACCOUNT = "/account";
    public static final String ROUTE_DEPOSIT = "/deposit";
    public static final String ROUTE_WITHDRAW = "/withdraw";
    public static final String ROUTE_MANAGER = "/manager";
    public static final String ROUTE_DASHBOARD = "/dashboard";
    public static final String ROUTE_PROCESS = "/process";
    public static final String ROUTE_LOGOUT = "/logout";


    /**
     * Error routes
     */
    public static final String ROUTE_ERROR = "/error";
    public static final String ROUTE_403_FORBIDDEN = "/403_forbidden";
    public static final String ROUTE_404_NOT_FOUND = "/404_not_found";
    public static final String ROUTE_405_METHOD_NOT_ALLOWED = "/405_method_not_allowed";
    public static final String ROUTE_400_BAD_REQUEST = "/400_bad_request";

    public static final String ROUTE_505_SERVER_ERROR = "/505_server_error";

    public static final String ROUTE_GENERIC_ERROR = "/generic_error";

    public static final String SESSION_SERVER_ERROR_TITLE = "errorTitle";
    public static final String SESSION_SERVER_ERROR_CODE = "errorCode";
    public static final String SESSION_SERVER_ERROR_MESSAGE = "errorMessage";
    public static final String SESSION_SERVER_ERROR_SUGGESTION = "errorSuggestion";

    /**
     * Error template
     */
    public static final String TEMPLATE_ERROR = "error.hbs";


    /**
     * Session Success / Error messages
     */
    public static final String SESSION_ERROR_MESSAGE = "error";
    public static final String SESSION_SUCCESS_MESSAGE = "success";
    /**
     * Session Variables
     */
    public static final String SESSION_ACCOUNT_ID = "accountid";
    public static final String SESSION_ACCOUNT_NAME = "name";
    public static final String SESSION_MANAGER_ID = "managerid";
    public static final String SESSION_MANAGER_NAME = "managername";

    /**
     * Transaction Variables
     */
    public static final String TRANSACTION_OBJECT_LIST = "transactions";
    public static final String TRANSACTION_OBJECT_LIST_EXISTS = "hastransactions";


    /**
     * Account Variables
     */
    public static final BigDecimal ACCOUNT_OBJECT_MAX_BALANCE = BigDecimal.valueOf(999_999_999.99);

    /**
     * Manager Variables
     */
    public static final String ACCOUNT_OBJECT_LIST = "accounts";
    public static final String ACCOUNT_OBJECT_LIST_EXISTS = "hasaccounts";

    /**
     * Password Hashing Constants
     */
    public static final int ITERATION = 100_000;
    public static final int SALT_SIZE = 16;
    public static final int KEY_LENGTH = 256;
    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Default Password Use
     */
    public static final String DEFAULT_PASSWORD = "MyFixedPassword";
    public static final String DEFAULT_MANAGER_PASSWORD = "MyFixedManagerPassword";
}


