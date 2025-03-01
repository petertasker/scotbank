package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public final class Constants {

    private Constants() {

    }

    /**
     * Endpoints
     **/
    public static final String URL_PAGE_LOGIN = "login_user.hbs";
    public static final String URL_PAGE_ACCOUNT = "account.hbs";
    public static final String URL_PAGE_ACCOUNT_DEPOSIT = "deposit.hbs";
    public static final String URL_PAGE_ACCOUNT_WITHDRAW = "withdraw.hbs";

    public static final String URL_PAGE_MANAGER_LOGIN = "login_manager.hbs";
    public static final String URL_PAGE_MANAGER_DASHBOARD = "manager_dashboard.hbs";

    // NOTE: If you refactor these make sure that they are reflected in the handlebars templates,
    // the variables there don't use these constants.

    /**
     * Success / Error messages
     */
    public static final String URL_ERROR_MESSAGE = "error";
    public static final String URL_SUCCESS_MESSAGE = "success";

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

}


