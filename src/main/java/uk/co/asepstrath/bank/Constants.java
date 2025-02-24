package uk.co.asepstrath.bank;

public final class Constants {

    private Constants() {

    }

    public static final String URL_PAGE_LOGIN = "login_user.hbs";
    public static final String URL_PAGE_ACCOUNT = "account.hbs";
    public static final String URL_PAGE_ACCOUNT_DEPOSIT = "deposit.hbs";
    public static final String URL_PAGE_ACCOUNT_WITHDRAW = "withdraw.hbs";

    // NOTE: If you refactor these make sure that they are reflected in the handlebars templates,
    // the variables there don't use these constants.
    public static final String URL_ERROR_MESSAGE = "error";
    public static final String URL_SUCCESS_MESSAGE = "success";

    public static final String SESSION_ACCOUNT_ID = "accountid";
    public static final String SESSION_ACCOUNT_NAME = "name";

    public static final String TRANSACTION_OBJECT_LIST = "transactions";
    public static final String TRANSACTION_OBJECT_EXISTS = "hastransactions";

    public static final String ACCOUNT_OBJECT_BALANCE = "balance";
}


